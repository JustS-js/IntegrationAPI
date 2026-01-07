package justs_js.integrationapi.impl.twitch.eventsub.event;

import justs_js.integrationapi.api.ApiEvent;
import justs_js.integrationapi.impl.twitch.eventsub.TwitchEventType;

public class TwitchEvent extends ApiEvent<TwitchEventType> {
    public TwitchEvent(String eventId, String apiName, TwitchEventType type, Object rawData) {
        super(eventId, apiName, type, rawData);
    }

    @Override
    public String toString() {
        return "{apiName: " + getApiName() + ", type: " + getType() + ", id: " + getEventId() + "}";
    }
}
