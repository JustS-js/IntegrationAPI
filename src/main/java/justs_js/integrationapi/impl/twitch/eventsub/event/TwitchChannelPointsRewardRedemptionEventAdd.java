package justs_js.integrationapi.impl.twitch.eventsub.event;

import justs_js.integrationapi.impl.twitch.eventsub.TwitchEventType;

public class TwitchChannelPointsRewardRedemptionEventAdd extends TwitchEvent {
    private final String user;
    private final String reward;
    public TwitchChannelPointsRewardRedemptionEventAdd(String eventId, String user, String reward) {
        super(eventId, "Twitch API", TwitchEventType.CHANNEL_CHANNEL_POINTS_CUSTOM_REWARD_REDEMPTION_ADD, reward);
        this.user = user;
        this.reward = reward;
    }

    @Override
    public String toString() {
        return "[Twitch reward] " + user + " > " + reward;
    }
}
