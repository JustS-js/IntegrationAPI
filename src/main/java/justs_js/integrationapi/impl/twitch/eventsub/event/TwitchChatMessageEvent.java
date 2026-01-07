package justs_js.integrationapi.impl.twitch.eventsub.event;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import justs_js.integrationapi.impl.twitch.eventsub.TwitchEventType;

public class TwitchChatMessageEvent extends TwitchEvent {
    private final String broadcasterUserId;
    private final String broadcasterUserLogin;
    private final String broadcasterUserName;
    private final String chatterUserId;
    private final String chatterUserLogin;
    private final String chatterUserName;
    private final String messageId;
    private final String messageText;
    private final String color;
    private final JsonArray badges;

    public TwitchChatMessageEvent(String eventId, String apiName, TwitchEventType type, JsonObject rawData) {
        super(eventId, apiName, TwitchEventType.CHANNEL_CHAT_MESSAGE, rawData);
        this.broadcasterUserId = rawData.get("broadcaster_user_id").getAsString();
        this.broadcasterUserLogin = rawData.get("broadcaster_user_login").getAsString();
        this.broadcasterUserName = rawData.get("broadcaster_user_name").getAsString();
        this.chatterUserId = rawData.get("chatter_user_id").getAsString();
        this.chatterUserLogin = rawData.get("chatter_user_login").getAsString();
        this.chatterUserName = rawData.get("chatter_user_name").getAsString();
        this.messageId = rawData.get("message_id").getAsString();
        JsonObject message = rawData.getAsJsonObject("message");
        this.messageText = message.get("text").getAsString();
        this.color = rawData.get("color").getAsString();
        this.badges = rawData.getAsJsonArray("badges");
    }

    public String getBroadcasterUserId() { return broadcasterUserId; }
    public String getBroadcasterUserLogin() { return broadcasterUserLogin; }
    public String getBroadcasterUserName() { return broadcasterUserName; }
    public String getChatterUserId() { return chatterUserId; }
    public String getChatterUserLogin() { return chatterUserLogin; }
    public String getChatterUserName() { return chatterUserName; }
    public String getMessageId() { return messageId; }
    public String getMessageText() { return messageText; }
    public String getColor() { return color; }
    public JsonArray getBadges() { return badges; }
}
