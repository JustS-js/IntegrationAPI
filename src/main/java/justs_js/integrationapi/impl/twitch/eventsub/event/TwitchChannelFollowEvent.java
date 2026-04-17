package justs_js.integrationapi.impl.twitch.eventsub.event;

import com.google.gson.JsonObject;

public class TwitchChannelFollowEvent extends TwitchEvent {
    private final String broadcasterUserId;
    private final String broadcasterUserLogin;
    private final String broadcasterUserName;
    private final String userId;
    private final String userLogin;
    private final String userName;
    private final String followedAt;

    public TwitchChannelFollowEvent(String eventId, String apiName, JsonObject rawData) {
        super(eventId, apiName, rawData);
        this.broadcasterUserId = rawData.get("broadcaster_user_id").getAsString();
        this.broadcasterUserLogin = rawData.get("broadcaster_user_login").getAsString();
        this.broadcasterUserName = rawData.get("broadcaster_user_name").getAsString();
        this.userId = rawData.get("user_id").getAsString();
        this.userLogin = rawData.get("user_login").getAsString();
        this.userName = rawData.get("user_name").getAsString();
        this.followedAt = rawData.get("followed_at").getAsString();
    }

    public String getBroadcasterUserId() { return broadcasterUserId; }
    public String getBroadcasterUserLogin() { return broadcasterUserLogin; }
    public String getBroadcasterUserName() { return broadcasterUserName; }
    public String getUserId() { return userId; }
    public String getUserLogin() { return userLogin; }
    public String getUserName() { return userName; }
    public String getFollowedAt() { return followedAt; }
}
