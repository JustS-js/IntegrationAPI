package justs_js.integrationapi.impl.twitch.eventsub.event;

import justs_js.integrationapi.impl.twitch.eventsub.TwitchEventType;

public class TwitchChatMessageEvent extends TwitchEvent {
    private final String author;
    private final String message;
    public TwitchChatMessageEvent(String eventId, String author, String message) {
        super(eventId, "Twitch API", TwitchEventType.CHANNEL_CHAT_MESSAGE, message);
        this.author = author;
        this.message = message;
    }

    @Override
    public String toString() {
        return "[Twitch message] " + author + " > " + message;
    }

    public String getAuthor() {
        return author;
    }

    public String getMessage() {
        return message;
    }
}
