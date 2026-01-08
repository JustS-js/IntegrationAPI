package justs_js.integrationapi.impl.twitch.eventsub;

import com.google.gson.*;
import justs_js.integrationapi.api.ApiConfig;
import justs_js.integrationapi.api.ApiIntegration;
import justs_js.integrationapi.api.exception.InvalidTokenException;
import justs_js.integrationapi.api.util.UrlBuilder;
import justs_js.integrationapi.impl.twitch.eventsub.event.TwitchEvent;
import justs_js.integrationapi.impl.twitch.eventsub.event.TwitchIntegrationRefreshTokenEvent;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.UUID;

public class TwitchIntegrationImpl extends ApiIntegration<TwitchEvent> {
    public TwitchIntegrationImpl(ApiConfig config) {
        super(config, TwitchEventTypes.getInstance().getRegisteredClasses());

        this.clientId = config.getAuthParams().get("clientId");
        this.clientSecret = config.getAuthParams().get("clientSecret");
        this.accessToken = config.getAuthParams("accessToken");
        this.refreshToken = config.getAuthParams("refreshToken");
    }

    private final String clientId;
    private final String clientSecret;
    private String accessToken;
    private String refreshToken;

    private String socket_url = "wss://eventsub.wss.twitch.tv/ws";
    private String sessionId;

    private final HttpClient httpClient = HttpClient.newHttpClient();
    private final Gson gson = new GsonBuilder().setPrettyPrinting().create();
    private TwitchWebSocketClient webSocketClient;

    private static final String VALIDATE_TOKEN_URL = "https://id.twitch.tv/oauth2/validate";
    private static final String TOKEN_URL = "https://id.twitch.tv/oauth2/token";
    private static final String EVENTSUB_URL = "https://api.twitch.tv/helix/eventsub/subscriptions";

    @Override
    protected void onInit() throws InvalidTokenException, IOException, InterruptedException {
        if (!isAccessTokenValid()) {
            refreshTokens();
            if (!isAccessTokenValid()) {
                throw new InvalidTokenException("Token pair is invalid");
            }
        }
        webSocketClient = new TwitchWebSocketClient(URI.create(socket_url));
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

    private void sendSubscriptionWebSocketMessage(String type) throws IOException, InterruptedException {
        if (type.startsWith("integration")) {
            return; // ignore integration's internal events
        }

        JsonObject subscription = new JsonObject();
        subscription.addProperty("type", type);
        subscription.addProperty("version", "1");

        String condition = getConfig().getApiParams(type);
        if (condition == null || condition.isEmpty()) {
            getConfig().getErrorHandler().onError(new Exception("Invalid condition present for " + type), getContext());
            return;
        }
        subscription.add(
                "condition",
                JsonParser.parseString(condition).getAsJsonObject()
        );

        JsonObject transport = new JsonObject();
        transport.addProperty("method", "websocket");
        transport.addProperty("session_id", sessionId);
        subscription.add("transport", transport);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(EVENTSUB_URL))
                .header("Client-ID", clientId)
                .header("Authorization", "Bearer " + accessToken)
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(subscription)))
                .build();
        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() != 202) {
            getConfig().getErrorHandler().onError(new Exception(response.body()), getContext());
            if (response.statusCode() == 401) {
                throw new InvalidTokenException("Invalid token for subscription");
            }
            return;
        }
        JsonObject responseJson = JsonParser.parseString(response.body()).getAsJsonObject();
        JsonArray data = responseJson.getAsJsonArray("data");

        if (data == null || data.isEmpty()) {
            getConfig().getErrorHandler().onError(new Exception("Data empty for " + type + " sub"), getContext());
            return;
        }
        JsonObject transportInfo = data.get(0).getAsJsonObject().getAsJsonObject("transport");
        String sessionIdFromSub = transportInfo.get("session_id").getAsString();
        if (!sessionIdFromSub.equals(sessionId)) {
            getConfig().getErrorHandler().onError(new Exception("Wrong session_id for " + type + " sub"), getContext());
        }
    }

    private boolean isAccessTokenValid() throws IOException, InterruptedException {
        if (accessToken == null || accessToken.isEmpty()) return false;
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(VALIDATE_TOKEN_URL))
                .header("Authorization", "Bearer " + accessToken)
                .build();
        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        return response.statusCode() == 200;
    }

    private void refreshTokens() throws IOException, InterruptedException, InvalidTokenException {
        if (refreshToken == null || refreshToken.isEmpty()) throw new InvalidTokenException("Invalid refresh token");
        UrlBuilder urlBuilder = new UrlBuilder(TOKEN_URL);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(
                        urlBuilder
                                .withParam("grant_type", "refresh_token")
                                .withParam("client_id", clientId)
                                .withParam("client_secret", clientSecret)
                                .withParam("refresh_token", refreshToken)
                                .build()
                ))
                .header("Content-Type", "application/x-www-form-urlencoded")
                .build();
        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() != 200) {
            LOGGER.warn("Failed to refresh tokens.");
            throw new InvalidTokenException(response.body());
        }

        JsonObject jsonResponse = JsonParser.parseString(response.body()).getAsJsonObject();
        accessToken = jsonResponse.get("access_token").getAsString();
        refreshToken = jsonResponse.get("refresh_token").getAsString();

        publishEvent(
            new TwitchIntegrationRefreshTokenEvent(
                UUID.randomUUID().toString(),
                getConfig().getApiName(),
                jsonResponse
            )
        );
    }

    private void handleSessionWelcomeWebsocketMessage(JsonObject message) {
        JsonObject payload = message.getAsJsonObject("payload");
        JsonObject session = payload.getAsJsonObject("session");
        sessionId = session.get("id").getAsString();
        getContext().setSessionData("session_id", sessionId);

        for (String eventType : TwitchEventTypes.getInstance().getRegisteredTypes()) {
            try {
                if (getConfig().isEventEnabled(TwitchEventTypes.getInstance().get(eventType))) {
                    sendSubscriptionWebSocketMessage(eventType);
                }
            } catch (IOException | InterruptedException e) {
                getConfig().getErrorHandler().onError(e, getContext());
            }
        }
    }

    private void handleReconnectWebsocketMessage(JsonObject message) throws Exception {
        JsonObject payload = message.getAsJsonObject("payload");
        JsonObject session = payload.getAsJsonObject("session");
        String newUrl = session.get("reconnect_url").getAsString();

        if (webSocketClient != null) {
            webSocketClient.close();
        }

        socket_url = newUrl;
        sessionId = session.get("id").getAsString();
        webSocketClient = new TwitchWebSocketClient(URI.create(socket_url));
        connect();
    }

    private void handleNotificationWebsocketMessage(JsonObject message) {
        JsonObject payload = message.getAsJsonObject("payload");
        JsonObject subscription = payload.getAsJsonObject("subscription");
        JsonObject event = payload.getAsJsonObject("event");

        String type = subscription.get("type").getAsString();
        Class<? extends TwitchEvent> eventClass = TwitchEventTypes.getInstance().get(type);
        if (eventClass == null) {
            // This event type was not registered
            return;
        }

        try {
            Constructor<? extends TwitchEvent> eventConstructor = eventClass.getDeclaredConstructor(
                    String.class,
                    String.class,
                    JsonObject.class
            );
            TwitchEvent eventInstance = eventConstructor.newInstance(
                    UUID.randomUUID().toString(),
                    getConfig().getApiName(),
                    event
            );
            publishEvent(eventInstance);
        } catch (InvocationTargetException | IllegalAccessException | InstantiationException | NoSuchMethodException e) {
            getConfig().getErrorHandler().onError(e, getContext());
        }

    }

    private class TwitchWebSocketClient extends WebSocketClient {
        public TwitchWebSocketClient(URI serverUri) { super(serverUri); }

        @Override
        public void onOpen(ServerHandshake handshake) {}

        @Override
        public void onClose(int code, String reason, boolean remote) {
            if (!getContext().getSessionData("session_id").equals(sessionId)) {
                connect();
            }
        }

        @Override
        public void onError(Exception ex) {
            getConfig().getErrorHandler().onError(ex, getContext());
        }

        @Override
        public void onMessage(String jsonMessage) { handleMessage(jsonMessage); }

        private void handleMessage(String jsonMessage) {
            try {
                JsonObject message = JsonParser.parseString(jsonMessage).getAsJsonObject();
                JsonObject metadata = message.getAsJsonObject("metadata");
                String messageType = metadata.get("message_type").getAsString();

                switch (messageType) {
                    case "session_welcome":
                        handleSessionWelcomeWebsocketMessage(message);
                        break;
                    case "notification":
                        handleNotificationWebsocketMessage(message);
                        break;
                    case "session_reconnect":
                        handleReconnectWebsocketMessage(message);
                        break;
                    default:
                        break;
                }

            } catch (Exception ex) {
                getConfig().getErrorHandler().onError(ex, getContext());
            }
        }
    }
}
