package justs_js.integrationapi.impl.twitch.eventsub.event;

import com.google.gson.JsonObject;
import justs_js.integrationapi.api.ApiEvent;

public abstract class TwitchEvent extends ApiEvent {
    public TwitchEvent(String eventId, String apiName, JsonObject rawData) {
        super(eventId, apiName, rawData);
    }
}
