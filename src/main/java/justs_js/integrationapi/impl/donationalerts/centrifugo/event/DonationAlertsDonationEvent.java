package justs_js.integrationapi.impl.donationalerts.centrifugo.event;

import com.google.gson.JsonObject;

public class DonationAlertsDonationEvent extends DonationAlertsCentrifugoEvent {
    private final int id;
    private final String username;
    private final String message;
    private final String messageType;
    private final double amount;
    private final String currency;
    private final int isShown;
    private final double amountInUserCurrency;
    private final String recipientName;
    private final int recipientUserId;
    private final String createdAt;
    private final String reason;

    public DonationAlertsDonationEvent(String eventId, String apiName, JsonObject rawData) {
        super(eventId, apiName, rawData);

        this.id = rawData.get("id").getAsInt();
        this.username = rawData.get("username").getAsString();
        this.messageType = rawData.get("message_type").getAsString();
        String message = "";
        if ("text".equals(this.messageType)) {
            message = rawData.get("message").getAsString();
        }
        this.message = message;
        this.amount = rawData.get("amount").getAsDouble();
        this.currency = rawData.get("currency").getAsString();
        this.isShown = rawData.get("is_shown").getAsInt();
        this.amountInUserCurrency = rawData.get("amount_in_user_currency").getAsDouble();
        JsonObject recipient = rawData.getAsJsonObject("recipient");
        this.recipientName = recipient.get("name").getAsString();
        this.recipientUserId = recipient.get("user_id").getAsInt();
        this.createdAt = rawData.get("created_at").getAsString();
        this.reason = rawData.get("reason").getAsString();
    }

    public int getId() { return id; }
    public String getUsername() { return username; }
    public String getMessage() { return message; }
    public String getMessageType() { return messageType; }
    public double getAmount() { return amount; }
    public String getCurrency() { return currency; }
    public int isShown() { return isShown; }
    public double getAmountInUserCurrency() { return amountInUserCurrency; }
    public String getRecipientName() { return recipientName; }
    public int getRecipientUserId() { return recipientUserId; }
    public String getCreatedAt() { return createdAt; }
    public String getReason() { return reason; }
}
