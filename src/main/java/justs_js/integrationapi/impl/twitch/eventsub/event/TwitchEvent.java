package justs_js.integrationapi.impl.twitch.eventsub.event;

import com.google.gson.JsonObject;
import justs_js.integrationapi.api.ApiEvent;
import justs_js.integrationapi.impl.twitch.eventsub.TwitchEventType;

public abstract class TwitchEvent extends ApiEvent<TwitchEventType> {
    public TwitchEvent(String eventId, String apiName, TwitchEventType type, JsonObject rawData) {
        super(eventId, apiName, type, rawData);
    }
}
