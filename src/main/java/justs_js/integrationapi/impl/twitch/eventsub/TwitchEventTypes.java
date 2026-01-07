package justs_js.integrationapi.impl.twitch.eventsub;

import justs_js.integrationapi.impl.twitch.eventsub.event.*;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class TwitchEventTypes {
    private static final Map<String, Class<? extends TwitchEvent>> typeToClassMap = new HashMap<>();
    public static final Class<? extends TwitchEvent> INTEGRATION_REFRESH_TOKEN = register("integration.refresh_token", TwitchIntegrationRefreshTokenEvent.class);
    public static final Class<? extends TwitchEvent> CHANNEL_CHAT_MESSAGE = register("channel.chat.message", TwitchChatMessageEvent.class);
    public static final Class<? extends TwitchEvent> CHANNEL_CHANNEL_POINTS_CUSTOM_REWARD_REDEMPTION_ADD = register("channel.channel_points_custom_reward_redemption.add", TwitchChannelPointsRewardRedemptionEventAdd.class);

    public static Class<? extends TwitchEvent> register(@NotNull String type, @NotNull Class<? extends TwitchEvent> eventClass) {
        typeToClassMap.put(type, eventClass);
        return eventClass;
    }

    public static Class<? extends TwitchEvent> get(@NotNull String type) {
        return typeToClassMap.get(type);
    }

    public static Collection<Class<? extends TwitchEvent>> getRegistered() {
        return Collections.unmodifiableCollection(typeToClassMap.values());
    }
}
