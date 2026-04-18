package justs_js.integrationapi.impl.donationalerts.socketio;

import justs_js.integrationapi.api.EventTypes;
import justs_js.integrationapi.impl.donationalerts.socketio.event.*;

public class DonationAlertsEventTypes extends EventTypes<DonationAlertsEvent> {
    public final Class<? extends DonationAlertsEvent> DONATION = register("1", DonationAlertsDonationEvent.class);
    public final Class<? extends DonationAlertsEvent> BOOSTY_SUBSCRIBER = register("20", DonationAlertsBoostySubscriberEvent.class);
    public final Class<? extends DonationAlertsEvent> VKPLAY_SUBSCRIPTION_RECURRENT_PAY = register("28", DonationAlertsVKPlaySubscriptionRecurrentPayEvent.class);
    public final Class<? extends DonationAlertsEvent> VKPLAY_SUBSCRIPTION_ACCEPTATION = register("29", DonationAlertsVKPlayGiftedSubscriptionAcceptationEvent.class);
    public final Class<? extends DonationAlertsEvent> VKPLAY_SUBSCRIPTION_PURCHASE = register("30", DonationAlertsVKPlaySubscriptionPurchaseEvent.class);

    private static DonationAlertsEventTypes instance;
    public static DonationAlertsEventTypes getInstance() {
        if (instance == null) {
            instance = new DonationAlertsEventTypes();
        }
        return instance;
    }
}
