package justs_js.integrationapi.impl.donationalerts.centrifugo.event;

import com.google.gson.JsonObject;

public class DonationAlertsGoalEvent extends DonationAlertsCentrifugoEvent {
    public DonationAlertsGoalEvent(String eventId, String apiName, JsonObject rawData) {
        super(eventId, apiName, rawData);
    }
}
