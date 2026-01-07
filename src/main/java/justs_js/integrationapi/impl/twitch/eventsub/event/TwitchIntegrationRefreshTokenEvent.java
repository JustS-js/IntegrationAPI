package justs_js.integrationapi.impl.twitch.eventsub.event;

import com.google.gson.JsonObject;
import justs_js.integrationapi.impl.twitch.eventsub.TwitchEventType;

public class TwitchIntegrationRefreshTokenEvent  extends TwitchEvent {
    private final String accessToken;
    private final String refreshToken;
    public TwitchIntegrationRefreshTokenEvent(String eventId, String apiName, TwitchEventType type, JsonObject rawData) {
        super(eventId, apiName, TwitchEventType.INTEGRATION_REFRESH_TOKEN, rawData);
        this.accessToken = rawData.get("access_token").getAsString();
        this.refreshToken = rawData.get("refresh_token").getAsString();
    }

    public String getAccessToken() {
        return accessToken;
    }

    public String getRefreshToken() {
        return refreshToken;
    }
}