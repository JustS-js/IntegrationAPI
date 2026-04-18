package justs_js.integrationapi.impl.donationalerts.socketio.event;

import com.google.gson.JsonObject;
import justs_js.integrationapi.api.ApiEvent;

public abstract class DonationAlertsEvent extends ApiEvent {
    public DonationAlertsEvent(String eventId, String apiName, JsonObject rawData) {
        super(eventId, apiName, rawData);
    }
}
