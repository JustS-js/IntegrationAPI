package justs_js.integrationapi.impl.donationalerts.centrifugo;

import com.google.gson.*;
import justs_js.integrationapi.api.ApiConfig;
import justs_js.integrationapi.api.ApiIntegration;
import justs_js.integrationapi.api.exception.InvalidTokenException;
import justs_js.integrationapi.impl.donationalerts.centrifugo.event.DonationAlertsCentrifugoEvent;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
* This class uses Centrifugo as a transport
* That means you can not obtain notifications for such events as YouTube subscription, Boosty gift, etc.
* The only possible types are DonationAlerts Donation, DonationAlerts Goal updates and DonationAlerts Poll updates.
* If you want to use other notifications, consider {@link justs_js.integrationapi.impl.donationalerts.socketio.DonationAlertsIntegrationImpl DonationAlertsIntegrationImpl}
* */
public class DonationAlertsCentrifugoIntegrationImpl extends ApiIntegration<DonationAlertsCentrifugoEvent>  {
    private static final String BACKUP_API_URL = "https://www.donationalerts.com/api/v1";
    private static final String BACKUP_WSS_URL = "wss://centrifugo.donationalerts.com/connection/websocket";
    private static final String BACKUP_SUB_URL = "/centrifuge/subscribe";

    public DonationAlertsCentrifugoIntegrationImpl(ApiConfig config) {
        super(config, DonationAlertsCentrifugoEventTypes.getInstance().getRegisteredClasses());

        this.widgetStaticToken = config.getAuthParams("token");
        String apiUrl = config.getApiParams("api_url");
        if (apiUrl == null) {
            apiUrl = BACKUP_API_URL;
        }
        this.apiUrl = apiUrl;
        String socketUrl = config.getApiParams("wss_url");
        if (socketUrl == null) {
            socketUrl = BACKUP_WSS_URL;
        }
        this.socketUrl = socketUrl;
        String subscribeUrl = config.getApiParams("sub_url");
        if (subscribeUrl == null) {
            subscribeUrl = BACKUP_SUB_URL;
        }
        this.subscribeUrl = subscribeUrl;
    }

    private final String widgetStaticToken;
    private String widgetAPIJWT;
    private String centrifugoJWT;
    private Map<String, String> centrifugoConnectionTokens;
    private String centrifugoClientUUID;
    private int userId;
    private final String socketUrl;
    private final String apiUrl;
    private final String subscribeUrl;

    private final HttpClient httpClient = HttpClient.newHttpClient();
    private final Gson gson = new GsonBuilder().setPrettyPrinting().create();
    private DASocketClient webSocketClient;

    @Override
    protected void onInit() throws InvalidTokenException, IOException, InterruptedException {
        if (!isTokenValid()) {
            throw new InvalidTokenException("Token is invalid");
        }

        fetchWidgetAPIToken();
        fetchCentrifugoData();

        webSocketClient = new DASocketClient(URI.create(socketUrl));
    }

    private boolean isTokenValid() {
        return widgetStaticToken != null && !widgetStaticToken.isEmpty() && !widgetStaticToken.isBlank();
    }

    @Override
    protected void connect() throws Exception {
        webSocketClient.connect();
    }

    @Override
    protected void disconnect() throws Exception {
        webSocketClient.close();
    }

    @Override
    protected void onShutdown() throws Exception {
    }

    private void fetchWidgetAPIToken() throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(this.apiUrl + "/token/widget?token=" + this.widgetStaticToken))
                .GET()
                .build();
        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        JsonObject jsonResponse = JsonParser.parseString(response.body()).getAsJsonObject();

        if (jsonResponse.has("success") && !jsonResponse.get("success").getAsBoolean()) {
            LOGGER.warn("Failed to fetch widgetJWT token.");
            if (jsonResponse.has("message")) {
                throw new InvalidTokenException(jsonResponse.get("message").getAsString());
            } else {
                throw new InvalidTokenException(response.body());
            }
        }

        this.widgetAPIJWT = jsonResponse.get("data").getAsJsonObject().get("token").getAsString();
    }

    private void fetchCentrifugoData() throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(this.apiUrl + "/user/widget"))
                .header("Authorization", "Bearer " + this.widgetAPIJWT)
                .GET()
                .build();
        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() != 200) {
            LOGGER.warn("Failed to fetch Centrifugo data.");
            throw new InvalidTokenException(response.body());
        }

        JsonObject jsonResponse = JsonParser.parseString(response.body()).getAsJsonObject();
        this.userId = jsonResponse.get("data").getAsJsonObject().get("id").getAsInt();
        this.centrifugoJWT = jsonResponse.get("data").getAsJsonObject().get("socket_connection_token").getAsString();
    }

    private void subscribeToPrivateChannels() throws IOException, InterruptedException {
        JsonObject subscription = new JsonObject();
        JsonArray channels = new JsonArray();
        for (String eventType : DonationAlertsCentrifugoEventTypes.getInstance().getRegisteredTypes()) {
            channels.add("$alerts:" + eventType + "_" + this.userId);
        }
        subscription.addProperty("client", this.centrifugoClientUUID);
        subscription.add("channels", channels);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(this.apiUrl + this.subscribeUrl))
                .header("Authorization", "Bearer " + this.widgetAPIJWT)
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(subscription)))
                .build();
        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() != 200) {
            LOGGER.warn("Failed to obtain centrifugo connection token.");
            throw new InvalidTokenException(response.body());
        }

        JsonObject responseJson = JsonParser.parseString(response.body()).getAsJsonObject();
        this.centrifugoConnectionTokens = new HashMap<>();
        JsonArray channelsResponse = responseJson.get("channels").getAsJsonArray();
        for (int i = 0; i < channelsResponse.size(); i++) {
            String channel = channelsResponse.get(i).getAsJsonObject().get("channel").getAsString();
            String token = channelsResponse.get(i).getAsJsonObject().get("token").getAsString();
            this.centrifugoConnectionTokens.put(channel, token);
        }

        for (Map.Entry<String, String> entry : this.centrifugoConnectionTokens.entrySet()) {
            JsonObject wssRequest = new JsonObject();
            wssRequest.addProperty("id", 2);
            wssRequest.addProperty("method", 1);
            JsonObject params = new JsonObject();
            params.addProperty("channel", entry.getKey());
            params.addProperty("token", entry.getValue());
            wssRequest.add("params", params);
            this.webSocketClient.send(wssRequest.toString());
        }
    }

    private void handleNotificationWebsocketMessage(JsonObject message) {
        JsonObject result = message.getAsJsonObject("result");
        if (result.has("type")) {
            LOGGER.debug("\"type\" message: {}", message);
            return;
        }
        String channel = result.get("channel").getAsString();
        String type = channel.split(":")[1].split("_")[0];

        Class<? extends DonationAlertsCentrifugoEvent> eventClass = DonationAlertsCentrifugoEventTypes.getInstance().get(type);
        if (eventClass == null) {
            // This event type was not registered
            return;
        }

        JsonObject event = result.getAsJsonObject("data").getAsJsonObject("data");

        try {
            Constructor<? extends DonationAlertsCentrifugoEvent> eventConstructor = eventClass.getDeclaredConstructor(
                    String.class,
                    String.class,
                    JsonObject.class
            );
            DonationAlertsCentrifugoEvent eventInstance = eventConstructor.newInstance(
                    UUID.randomUUID().toString(),
                    getConfig().getApiName(),
                    event
            );
            publishEvent(eventInstance);
        } catch (InvocationTargetException | IllegalAccessException | InstantiationException | NoSuchMethodException e) {
            handleError(e);
        }

    }

    private void handleClientUUIDMessage(JsonObject message) {
        JsonObject result = message.getAsJsonObject("result");
        this.centrifugoClientUUID = result.get("client").getAsString();

        try {
            subscribeToPrivateChannels();
        } catch (IOException | InterruptedException e) {
            LOGGER.error("Could not subscribe to private channel.");
        }

    }

    private class DASocketClient extends WebSocketClient {
        public DASocketClient(URI serverUri) { super(serverUri); }

        @Override
        public void onOpen(ServerHandshake handshakedata) {
            JsonObject request = new JsonObject();
            request.addProperty("id", 1);
            JsonObject params = new JsonObject();
            params.addProperty("token", centrifugoJWT);
            request.add("params", params);
            this.send(request.toString());
        }

        @Override
        public void onClose(int code, String reason, boolean remote) {

        }

        @Override
        public void onError(Exception ex) {

        }

        @Override
        public void onMessage(String message) { handleMessage(message); }

        private void handleMessage(String jsonMessage) {
            try {
                JsonObject message = JsonParser.parseString(jsonMessage).getAsJsonObject();
                if (message.has("id")) {
                    if (message.get("id").getAsInt() == 1) {
                        handleClientUUIDMessage(message);
                    }
                    return;
                }
                handleNotificationWebsocketMessage(message);
            } catch (Exception ex) {
                getConfig().getErrorHandler().onError(ex, getContext());
            }
        }
    }
}
