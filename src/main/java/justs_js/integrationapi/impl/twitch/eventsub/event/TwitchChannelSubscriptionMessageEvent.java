package justs_js.integrationapi.impl.twitch.eventsub.event;

import com.google.gson.JsonObject;

public class TwitchChannelSubscriptionMessageEvent extends TwitchEvent {
    private final String broadcasterUserId;
    private final String broadcasterUserLogin;
    private final String broadcasterUserName;
    private final String userId;
    private final String userLogin;
    private final String userName;
    private final String tier;
    private final String messageText;
    private final int cumulativeMonths;
    private final int streakMonths;
    private final int durationMonths;

    public TwitchChannelSubscriptionMessageEvent(String eventId, String apiName, JsonObject rawData) {
        super(eventId, apiName, rawData);
        this.broadcasterUserId = rawData.get("broadcaster_user_id").getAsString();
        this.broadcasterUserLogin = rawData.get("broadcaster_user_login").getAsString();
        this.broadcasterUserName = rawData.get("broadcaster_user_name").getAsString();
        this.userId = rawData.get("user_id").getAsString();
        this.userLogin = rawData.get("user_login").getAsString();
        this.userName = rawData.get("user_name").getAsString();
        this.tier = rawData.get("tier").getAsString();
        JsonObject message = rawData.getAsJsonObject("message");
        this.messageText = message.get("text").getAsString();
        this.cumulativeMonths = rawData.get("cumulative_months").getAsInt();
        this.streakMonths = rawData.get("streak_months").isJsonNull() ? -1 : rawData.get("streak_months").getAsInt();
        this.durationMonths = rawData.get("duration_months").getAsInt();
    }

    public String getBroadcasterUserId() { return broadcasterUserId; }
    public String getBroadcasterUserLogin() { return broadcasterUserLogin; }
    public String getBroadcasterUserName() { return broadcasterUserName; }
    public String getUserId() { return userId; }
    public String getUserLogin() { return userLogin; }
    public String getUserName() { return userName; }
    public String getTier() { return tier; }
    public String getMessageText() { return messageText; }
    public int getCumulativeMonths() { return cumulativeMonths; }
    /**
     * -1 if not shared
     * */
    public int getStreakMonths() { return streakMonths; }
    public int getDurationMonths() { return durationMonths; }
}
