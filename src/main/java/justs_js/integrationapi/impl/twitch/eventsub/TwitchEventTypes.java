package justs_js.integrationapi.impl.twitch.eventsub;

import justs_js.integrationapi.impl.twitch.eventsub.event.*;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

public class TwitchEventTypes {
    private static final Map<TwitchEventType, Class<? extends TwitchEvent>> typeToClassMap = new HashMap<>();

    public static void register(@NotNull TwitchEventType type, @NotNull Class<? extends TwitchEvent> eventClass) {
        typeToClassMap.put(type, eventClass);
    }

    public static Class<? extends TwitchEvent> get(@NotNull TwitchEventType type) {
        return typeToClassMap.get(type);
    }

    static {
        register(TwitchEventType.INTEGRATION_REFRESH_TOKEN, TwitchIntegrationRefreshTokenEvent.class);
        //...
        register(TwitchEventType.CHANNEL_CHAT_MESSAGE, TwitchChatMessageEvent.class);
        //...
        register(TwitchEventType.CHANNEL_CHANNEL_POINTS_CUSTOM_REWARD_REDEMPTION_ADD, TwitchChannelPointsRewardRedemptionEventAdd.class);
        //...
    }
}
