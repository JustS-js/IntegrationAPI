package justs_js.integrationapi.impl.twitch.eventsub;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
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

public class TwitchIntegrationImpl extends ApiIntegration<TwitchEvent, TwitchEventType> {
    public TwitchIntegrationImpl(ApiConfig<TwitchEventType> config) {
        super(config, TwitchEventType.class);

        this.clientId = config.getAuthParams().get("clientId");
        this.clientSecret = config.getAuthParams().get("clientSecret");
        this.accessToken = config.getAuthParams("accessToken");
        this.refreshToken = config.getAuthParams("refreshToken");
    }

    private final String clientId;
    private final String clientSecret;
    private String accessToken;
    private String refreshToken;

    private String socket_url;
    private String sessionId;

    private final HttpClient httpClient = HttpClient.newHttpClient();
    private final Gson gson = new GsonBuilder().setPrettyPrinting().create();
    private TwitchWebSocketClient webSocketClient;

    private static final String VALIDATE_TOKEN_URL = "https://id.twitch.tv/oauth2/validate";
    private static final String TOKEN_URL = "https://id.twitch.tv/oauth2/token";

    @Override
    protected void onInit() throws InvalidTokenException, IOException, InterruptedException {
        if (isAccessTokenValid()) return;
        refreshTokens();
        if (isAccessTokenValid()) return;
        throw new InvalidTokenException("Token pair is invalid");
    }

    @Override
    protected void connect() throws Exception {

    }

    @Override
    protected void disconnect() throws Exception {

    }

    @Override
    protected void onShutdown() throws Exception {

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
                TwitchEventType.INTEGRATION_REFRESH_TOKEN,
                jsonResponse
            )
        );
    }

    private void handleSessionWelcomeWebsocketMessage(JsonObject message) {
        JsonObject payload = message.getAsJsonObject("payload");
        JsonObject session = payload.getAsJsonObject("session");
        sessionId = session.get("id").getAsString();
    }

    private void handleReconnectWebsocketMessage(JsonObject message) throws Exception {
        JsonObject payload = message.getAsJsonObject("payload");
        JsonObject session = payload.getAsJsonObject("session");
        String newUrl = session.get("reconnect_url").getAsString();

        if (webSocketClient != null) {
            webSocketClient.close();
        }

        socket_url = newUrl;
        connect();
    }

    private void handleNotificationWebsocketMessage(JsonObject message) {
        JsonObject payload = message.getAsJsonObject("payload");
        JsonObject subscription = payload.getAsJsonObject("subscription");
        JsonObject event = payload.getAsJsonObject("event");

        String type = subscription.get("type").getAsString();
        String typeEnumName = type
                .replace('.', '_')
                .toUpperCase();
        TwitchEventType eventType = TwitchEventType.valueOf(typeEnumName);

        Class<? extends TwitchEvent> eventClass = TwitchEventTypes.get(eventType);
        try {
            Constructor<? extends TwitchEvent> eventConstructor = eventClass.getDeclaredConstructor(
                    String.class,
                    String.class,
                    TwitchEventType.class,
                    Object.class
            );
            eventConstructor.newInstance(
                    event.get("id").getAsString(),
                    getConfig().getApiName(),
                    eventType,
                    event
            );
        } catch (InvocationTargetException | IllegalAccessException | InstantiationException | NoSuchMethodException e) {
            getConfig().getErrorHandler().onError(e, getContext());
        }

    }

    private class TwitchWebSocketClient extends WebSocketClient {
        public TwitchWebSocketClient(URI serverUri) { super(serverUri); }

        @Override
        public void onOpen(ServerHandshake handshake) {}

        @Override
        public void onClose(int code, String reason, boolean remote) {}

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
                    case "session_keepalive":
                        break;
                    case "session_reconnect":
                        handleReconnectWebsocketMessage(message);
                        break;
                }

            } catch (Exception ex) {
                getConfig().getErrorHandler().onError(ex, getContext());
            }
        }
    }
}
