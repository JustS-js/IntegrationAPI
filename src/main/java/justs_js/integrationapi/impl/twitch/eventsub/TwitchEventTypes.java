package justs_js.integrationapi.impl.twitch.eventsub;

import justs_js.integrationapi.api.EventTypes;
import justs_js.integrationapi.impl.twitch.eventsub.event.*;

public class TwitchEventTypes extends EventTypes<TwitchEvent> {
    public final Class<? extends TwitchEvent> INTEGRATION_REFRESH_TOKEN = register("integration.refresh_token", TwitchIntegrationRefreshTokenEvent.class);
    public final Class<? extends TwitchEvent> CHANNEL_CHAT_MESSAGE = register("channel.chat.message", TwitchChatMessageEvent.class);
    public final Class<? extends TwitchEvent> CHANNEL_CHANNEL_POINTS_CUSTOM_REWARD_REDEMPTION_ADD = register("channel.channel_points_custom_reward_redemption.add", TwitchChannelPointsRewardRedemptionEventAdd.class);
    public final Class<? extends TwitchEvent> CHANNEL_BITS_USE = register("channel.bits.use", TwitchChannelBitsUseEvent.class);
    public final Class<? extends TwitchEvent> CHANNEL_FOLLOW = register("channel.follow", TwitchChannelFollowEvent.class);
    public final Class<? extends TwitchEvent> CHANNEL_SUBSCRIBE = register("channel.subscribe", TwitchChannelSubscribeEvent.class);
    public final Class<? extends TwitchEvent> CHANNEL_SUBSCRIPTION_GIFT = register("channel.subscription.gift", TwitchChannelSubscriptionGiftEvent.class);
    public final Class<? extends TwitchEvent> CHANNEL_SUBSCRIPTION_MESSAGE = register("channel.subscription.message", TwitchChannelSubscriptionMessageEvent.class);

    private static TwitchEventTypes instance;
    public static TwitchEventTypes getInstance() {
        if (instance == null) {
            instance = new TwitchEventTypes();
        }
        return instance;
    }
}
