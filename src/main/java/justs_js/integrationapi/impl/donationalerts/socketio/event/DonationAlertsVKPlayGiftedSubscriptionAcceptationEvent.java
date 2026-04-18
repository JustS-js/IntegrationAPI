package justs_js.integrationapi.impl.donationalerts.socketio.event;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class DonationAlertsVKPlayGiftedSubscriptionAcceptationEvent extends DonationAlertsEvent{
    private final int id;
    private final String alertType;
    private final boolean isShown;
    private final String billingSystem;
    private final String billingSystemType;
    private final String username;
    private final String header;
    private final String dateCreated;
    private final boolean isTestAlert;

    private final int randomness;
    private final String levelName;
    private final int period;
    private final String sender;
    public DonationAlertsVKPlayGiftedSubscriptionAcceptationEvent(String eventId, String apiName, JsonObject rawData) {
        super(eventId, apiName, rawData);
        this.id = rawData.get("id").getAsInt();
        this.alertType = rawData.get("alert_type").getAsString();
        this.isShown = !rawData.get("is_shown").isJsonNull() && rawData.get("is_shown").getAsInt() == 1;
        this.billingSystem = rawData.get("billing_system").getAsString();
        this.billingSystemType = rawData.get("billing_system_type").getAsString();
        this.username = rawData.get("username").getAsString();
        this.header = rawData.get("header").getAsString();
        this.dateCreated = rawData.get("date_created").getAsString();
        this.isTestAlert = rawData.get("_is_test_alert").getAsBoolean();

        String additionalDataString = rawData.get("additional_data").getAsString();
        JsonObject additionalData = JsonParser.parseString(additionalDataString).getAsJsonObject();

        this.randomness = additionalData.has("randomness") ? additionalData.get("randomness").getAsInt() : 0;
        JsonObject eventData = additionalData.get("event_data").getAsJsonObject();
        this.levelName = eventData.get("level_name").getAsString();
        this.period = eventData.get("period").getAsInt();
        this.sender = eventData.get("sender").getAsString();
    }

    public int getId() {
        return id;
    }

    public String getAlertType() {
        return alertType;
    }

    public boolean isShown() {
        return isShown;
    }

    public String getBillingSystem() {
        return billingSystem;
    }

    public String getBillingSystemType() {
        return billingSystemType;
    }

    public String getUsername() {
        return username;
    }

    public String getHeader() {
        return header;
    }

    public String getDateCreated() {
        return dateCreated;
    }

    public boolean isTestAlert() {
        return isTestAlert;
    }

    public int getRandomness() {
        return randomness;
    }

    public String getLevelName() {
        return levelName;
    }

    public int getPeriod() {
        return period;
    }

    public String getSender() {
        return sender;
    }
}
