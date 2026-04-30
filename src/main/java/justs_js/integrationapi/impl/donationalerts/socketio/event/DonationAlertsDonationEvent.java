package justs_js.integrationapi.impl.donationalerts.socketio.event;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class DonationAlertsDonationEvent extends DonationAlertsEvent {
    private final int id;
    private final String alertType;
    private final boolean isShown;
    private final String billingSystem;
    private final String billingSystemType;
    private final String username;
    private final double amount;
    private final String amountFormatted;
    private final double amountMain;
    private final String currency;
    private final String message;
    private final String dateCreated;
    private final String ttsUrl;
    private final boolean isTestAlert;
    private final String messageType;

    private final int randomness;
    private final boolean isCommissionCovered;


    public DonationAlertsDonationEvent(String eventId, String apiName, JsonObject rawData) {
        super(eventId, apiName, rawData);

        this.id = rawData.get("id").getAsInt();
        this.alertType = rawData.get("alert_type").getAsString();
        this.isShown = !rawData.get("is_shown").isJsonNull() && rawData.get("is_shown").getAsInt() == 1;
        this.billingSystem = rawData.get("billing_system").getAsString();
        this.billingSystemType = rawData.get("billing_system_type").isJsonNull() ? null : rawData.get("billing_system_type").getAsString();
        this.username = rawData.get("username").isJsonNull() ? null : rawData.get("username").getAsString();
        this.amount = rawData.get("amount").getAsDouble();
        this.amountFormatted = rawData.get("amount_formatted").getAsString();
        this.amountMain = rawData.get("amount_main").getAsDouble();
        this.currency = rawData.get("currency").getAsString();
        this.message = rawData.has("message") ? (rawData.get("message").isJsonNull() ? null : rawData.get("message").getAsString()) : null;
        this.dateCreated = rawData.get("date_created").getAsString();
        this.ttsUrl = rawData.get("tts_url").isJsonNull() ? null : rawData.get("tts_url").getAsString();
        this.isTestAlert = rawData.get("_is_test_alert").getAsBoolean();
        this.messageType = rawData.has("message_type") ? rawData.get("message_type").getAsString() : null;

        JsonObject additionalData = rawData.get("additional_data").isJsonObject() ?
                rawData.get("additional_data").getAsJsonObject() :
                JsonParser.parseString(rawData.get("additional_data").getAsString()).getAsJsonObject();

        this.randomness = additionalData.has("randomness") ? additionalData.get("randomness").getAsInt() : 0;
        this.isCommissionCovered = additionalData.has("is_commission_covered") && additionalData.get("is_commission_covered").getAsInt() == 1;
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

    public double getAmount() {
        return amount;
    }

    public String getAmountFormatted() {
        return amountFormatted;
    }

    public double getAmountMain() {
        return amountMain;
    }

    public String getCurrency() {
        return currency;
    }

    public String getMessage() {
        return message;
    }

    public String getDateCreated() {
        return dateCreated;
    }

    public String getTtsUrl() {
        return ttsUrl;
    }

    public boolean isTestAlert() {
        return isTestAlert;
    }

    public String getMessageType() {
        return messageType;
    }

    public int getRandomness() {
        return randomness;
    }

    public boolean isCommissionCovered() {
        return isCommissionCovered;
    }
}
