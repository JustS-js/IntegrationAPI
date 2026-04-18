package justs_js.integrationapi.impl.twitch.eventsub.event;

import com.google.gson.JsonObject;

/**
 * If you really need to use PowerUps - please, make your own event or create a pull request.
 * I did not want to pay twitch just to see how "power_up" and "custom_power_up" JSON objects look.
 * (Yes, twitch devs really did not put them in the <a href="https://dev.twitch.tv/docs/eventsub/eventsub-subscription-types/#channelbitsuse">docs</a>)
 * */
public class TwitchChannelBitsUseEvent extends TwitchEvent {
    private final String broadcasterUserId;
    private final String broadcasterUserLogin;
    private final String broadcasterUserName;
    private final String userId;
    private final String userLogin;
    private final String userName;
    private final int bits;
    private final String type;
    private final String messageText;

    public TwitchChannelBitsUseEvent(String eventId, String apiName, JsonObject rawData) {
        super(eventId, apiName, rawData);
        this.broadcasterUserId = rawData.get("broadcaster_user_id").getAsString();
        this.broadcasterUserLogin = rawData.get("broadcaster_user_login").getAsString();
        this.broadcasterUserName = rawData.get("broadcaster_user_name").getAsString();
        this.userId = rawData.get("user_id").getAsString();
        this.userLogin = rawData.get("user_login").getAsString();
        this.userName = rawData.get("user_name").getAsString();
        this.bits = rawData.get("bits").getAsInt();
        this.type = rawData.get("type").getAsString();
        JsonObject message = rawData.getAsJsonObject("message");
        this.messageText = message.get("text").getAsString();
    }

    public String getBroadcasterUserId() { return broadcasterUserId; }
    public String getBroadcasterUserLogin() { return broadcasterUserLogin; }
    public String getBroadcasterUserName() { return broadcasterUserName; }
    public String getUserId() { return userId; }
    public String getUserLogin() { return userLogin; }
    public String getUserName() { return userName; }
    public int getBits() { return bits; }
    public String getType() { return type; }
    public String getMessageText() { return messageText; }
}
