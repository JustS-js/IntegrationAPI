package justs_js.integrationapi.impl.donationalerts.centrifugo;

import justs_js.integrationapi.api.EventTypes;
import justs_js.integrationapi.impl.donationalerts.centrifugo.event.*;

public class DonationAlertsCentrifugoEventTypes extends EventTypes<DonationAlertsCentrifugoEvent> {
    public final Class<? extends DonationAlertsCentrifugoEvent> DONATION = register("donation", DonationAlertsDonationEvent.class);
    public final Class<? extends DonationAlertsCentrifugoEvent> GOAL = register("goal", DonationAlertsGoalEvent.class);
    public final Class<? extends DonationAlertsCentrifugoEvent> POLL = register("poll", DonationAlertsPollEvent.class);

    private static DonationAlertsCentrifugoEventTypes instance;
    public static DonationAlertsCentrifugoEventTypes getInstance() {
        if (instance == null) {
            instance = new DonationAlertsCentrifugoEventTypes();
        }
        return instance;
    }
}
