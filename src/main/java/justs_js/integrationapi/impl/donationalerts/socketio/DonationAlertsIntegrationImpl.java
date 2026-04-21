package justs_js.integrationapi.impl.donationalerts.socketio;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import io.socket.client.IO;
import io.socket.client.Socket;
import justs_js.integrationapi.api.ApiConfig;
import justs_js.integrationapi.api.ApiIntegration;
import justs_js.integrationapi.api.exception.InvalidTokenException;
import justs_js.integrationapi.impl.donationalerts.socketio.event.DonationAlertsEvent;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.net.URI;
import java.util.Map;
import java.util.UUID;

/**
 * Please consider that this api is very unstable.
 * This implementation uses {@link io.socket.client.Socket SocketIO} websocket that is designated for embedded widgets.
 * DA's widget api is not documented at all and is a subject to a constant change.
 * This implementation may (surely will) be outdated some day.
 * I'll try to keep up with DA's updates but can't guarantee anything.
 * If you want to use a properly documented implementation only for donations (without other types of notifications)
 * Consider using {@link justs_js.integrationapi.impl.donationalerts.centrifugo.DonationAlertsCentrifugoIntegrationImpl DonationAlertsCentrifugoIntegrationImpl}
 * */
public class DonationAlertsIntegrationImpl extends ApiIntegration<DonationAlertsEvent> {
    private static final String BACKUP_WSS_URL = "wss://socket.donationalerts.com:443";

    public DonationAlertsIntegrationImpl(ApiConfig config) {
        super(config, DonationAlertsEventTypes.getInstance().getRegisteredClasses());

        this.token = config.getAuthParams("token");
        String socketUrl = config.getApiParams("wss_url");
        if (socketUrl == null) {
            socketUrl = BACKUP_WSS_URL;
        }
        this.socketUrl = socketUrl;
    }

    private final String token;
    private final String socketUrl;

    private DASocketClient webSocketClient;

    @Override
    protected void onInit() throws Exception {
        if (!isTokenValid()) {
            throw new InvalidTokenException("Token is invalid");
        }

        webSocketClient = new DASocketClient(URI.create(socketUrl));
    }

    private boolean isTokenValid() {
        return token != null && !token.isEmpty() && !token.isBlank();
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

    private void handleSocketMessage(JsonObject message) {

        String type = message.get("alert_type").getAsString();

        Class<? extends DonationAlertsEvent> eventClass = DonationAlertsEventTypes.getInstance().get(type);
        if (eventClass == null) {
            // This event type was not registered
            return;
        }
        if ("1".equals(type) && message.has("header")) {
            // for some reason donations duplicate if resent from panel,
            // and the "fake" notification has "header" field.
            return;
        }

        try {
            Constructor<? extends DonationAlertsEvent> eventConstructor = eventClass.getDeclaredConstructor(
                    String.class,
                    String.class,
                    JsonObject.class
            );
            DonationAlertsEvent eventInstance = eventConstructor.newInstance(
                    UUID.randomUUID().toString(),
                    getConfig().getApiName(),
                    message
            );
            publishEvent(eventInstance);
        } catch (InvocationTargetException | IllegalAccessException | InstantiationException | NoSuchMethodException e) {
            handleError(e);
        }
    }

    private class DASocketClient {
        private final Socket socket;

        public DASocketClient(URI serverUri) {
            IO.Options options = new IO.Options();
            options.reconnection = true;
            options.reconnectionDelayMax = 40_000;
            options.reconnectionDelay = 2_000;
            options.timeout = 30_000;

            this.socket = IO.socket(serverUri, options);
            this.socket
                    .on(Socket.EVENT_CONNECT, this::onConnection)
                    .on("reconnect", this::onReconnection)
                    .on(Socket.EVENT_CONNECT_ERROR, this::onError)
                    .on("error", this::onError)
                    .on("donation", this::onDonation);
        }

        private void onConnection(Object... objects) {
            this.socket.emit("add-user", Map.of("token", token));
        }

        private void onReconnection(Object... objects) {
            this.socket.emit("add-user", Map.of("token", token));
        }

        private void onError(Object... objects) {
            handleError((Exception)objects[0]);
        }

        private void onDonation(Object... objects) {
            String jsonMessage = (String)objects[0];
            handleSocketMessage(JsonParser.parseString(jsonMessage).getAsJsonObject());
        }

        public void connect() {
            this.socket.connect();
        }

        public void close() {
            this.socket.close();
        }
    }
}
