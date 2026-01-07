package justs_js.integrationapi;

import justs_js.integrationapi.api.ApiConfig;
import justs_js.integrationapi.impl.twitch.eventsub.TwitchEventTypes;
import justs_js.integrationapi.impl.twitch.eventsub.TwitchIntegrationImpl;
import justs_js.integrationapi.impl.twitch.eventsub.event.TwitchChatMessageEvent;
import justs_js.integrationapi.impl.twitch.eventsub.event.TwitchIntegrationRefreshTokenEvent;
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
		ApiConfig.Builder configBuilder = new ApiConfig.Builder();
		twitch = new TwitchIntegrationImpl(
				configBuilder
						.enableEvent(TwitchEventTypes.INTEGRATION_REFRESH_TOKEN)
						.enableEvent(TwitchEventTypes.CHANNEL_CHAT_MESSAGE)
						.withApiName("Twitch-API")
						.withAuthParam("channel", "JustS-js")
						.withApiParam("foo", "bar")
						.build()
		);
		// 2. Subscribe to relevant events with your custom callbacks
		twitch.subscribe(
				TwitchEventTypes.CHANNEL_CHAT_MESSAGE,
				event -> LOGGER.info(
						"[{}]: {}",
						((TwitchChatMessageEvent) event).getChatterUserName(),
						((TwitchChatMessageEvent) event).getMessageText()
				)
		);
		twitch.subscribe(
				TwitchEventTypes.INTEGRATION_REFRESH_TOKEN,
				event -> LOGGER.info(
						"Tokens were refreshed! access_token: {} ; refresh_token: {}",
						((TwitchIntegrationRefreshTokenEvent) event).getAccessToken(),
						((TwitchIntegrationRefreshTokenEvent) event).getRefreshToken()
				)
		);
		// 3. Start the Integration instance
		boolean success = twitch.start();
		if (success) {
			LOGGER.info("Started twitch integration successfully!");
		} else {
			LOGGER.warn("Could not start twitch integration!");
			// might want to fix something and restart it...
		}
	}
}