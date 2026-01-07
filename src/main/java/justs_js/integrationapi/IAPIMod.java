package justs_js.integrationapi;

import justs_js.integrationapi.api.ApiConfig;
import justs_js.integrationapi.impl.twitch.eventsub.TwitchEventType;
import justs_js.integrationapi.impl.twitch.eventsub.TwitchIntegrationImpl;
import justs_js.integrationapi.impl.twitch.eventsub.event.TwitchChatMessageEvent;
import net.fabricmc.api.ModInitializer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class IAPIMod implements ModInitializer {
	public static final String MOD_ID = "integrationapi";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	private static TwitchIntegrationImpl twitch;

	@Override
	public void onInitialize() {
		LOGGER.info("Integration API loaded.");

		// 1. Create Integration instance with ApiConfig
		ApiConfig.Builder<TwitchEventType> configBuilder = new ApiConfig.Builder<>();
		twitch = new TwitchIntegrationImpl(
				configBuilder
						.enableEvent(TwitchEventType.CHANNEL_CHAT_MESSAGE)
						.withApiName("Twitch-API")
						.withAuthParam("channel", "JustS-js")
						.withApiParam("foo", "bar")
						.build()
		);
		// 2. Subscribe to relevant events with your custom callbacks
		twitch.subscribe(
				TwitchEventType.CHANNEL_CHAT_MESSAGE,
				event -> LOGGER.info(
						"[{}]: {}",
						((TwitchChatMessageEvent) event).getAuthor(),
						((TwitchChatMessageEvent) event).getMessage()
				)
		);
		// 3. Start the Integration instance
		twitch.start();
	}
}