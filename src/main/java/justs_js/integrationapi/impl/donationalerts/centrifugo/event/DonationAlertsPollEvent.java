package justs_js.integrationapi.impl.donationalerts.centrifugo.event;

import com.google.gson.JsonObject;

public class DonationAlertsPollEvent extends DonationAlertsCentrifugoEvent {
    public DonationAlertsPollEvent(String eventId, String apiName, JsonObject rawData) {
        super(eventId, apiName, rawData);
    }
}
