package justs_js.integrationapi.impl.twitch.eventsub.event;

import com.google.gson.JsonObject;

public class TwitchIntegrationRefreshTokenEvent  extends TwitchEvent {
    private final String accessToken;
    private final String refreshToken;
    public TwitchIntegrationRefreshTokenEvent(String eventId, String apiName, JsonObject rawData) {
        super(eventId, apiName, rawData);
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