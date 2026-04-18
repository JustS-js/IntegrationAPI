package justs_js.integrationapi.impl.donationalerts.centrifugo.event;

import com.google.gson.JsonObject;
import justs_js.integrationapi.api.ApiEvent;

public abstract class DonationAlertsCentrifugoEvent extends ApiEvent {
    public DonationAlertsCentrifugoEvent(String eventId, String apiName, JsonObject rawData) {
        super(eventId, apiName, rawData);
    }
}
