package justs_js.integrationapi.impl.twitch.eventsub.event;

import com.google.gson.JsonObject;

public class TwitchChannelPointsRewardRedemptionEventAdd extends TwitchEvent {
    private final String broadcasterUserId;
    private final String broadcasterUserLogin;
    private final String broadcasterUserName;
    private final String userId;
    private final String userLogin;
    private final String userName;
    private final String userInput;
    private final String status;
    private final String rewardId;
    private final String rewardTitle;
    private final int rewardCost;
    private final String rewardPrompt;
    private final String redeemedAt;

    public TwitchChannelPointsRewardRedemptionEventAdd(String eventId, String apiName, JsonObject rawData) {
        super(eventId, apiName, rawData);
        this.broadcasterUserId = rawData.get("broadcaster_user_id").getAsString();
        this.broadcasterUserLogin = rawData.get("broadcaster_user_login").getAsString();
        this.broadcasterUserName = rawData.get("broadcaster_user_name").getAsString();
        this.userId = rawData.get("user_id").getAsString();
        this.userLogin = rawData.get("user_login").getAsString();
        this.userName = rawData.get("user_name").getAsString();
        this.userInput = rawData.get("user_input").getAsString();
        this.status = rawData.get("status").getAsString();
        JsonObject reward = rawData.getAsJsonObject("reward");
        this.rewardId = reward.get("id").getAsString();
        this.rewardTitle = reward.get("title").getAsString();
        this.rewardCost = reward.get("cost").getAsInt();
        this.rewardPrompt = reward.get("prompt").getAsString();
        this.redeemedAt = rawData.get("redeemed_at").getAsString();
    }

    public String getBroadcasterUserId() { return broadcasterUserId; }
    public String getBroadcasterUserLogin() { return broadcasterUserLogin; }
    public String getBroadcasterUserName() { return broadcasterUserName; }
    public String getUserId() { return userId; }
    public String getUserLogin() { return userLogin; }
    public String getUserName() { return userName; }
    public String getUserInput() { return userInput; }
    public String getStatus() { return status; }
    public String getRewardId() { return rewardId; }
    public String getRewardTitle() { return rewardTitle; }
    public int getRewardCost() { return rewardCost; }
    public String getRewardPrompt() { return rewardPrompt; }
    public String getRedeemedAt() { return redeemedAt; }
}
