package justs_js.integrationapi;

import justs_js.integrationapi.api.ApiConfig;
import justs_js.integrationapi.impl.twitch.eventsub.TwitchEventTypes;
import justs_js.integrationapi.impl.twitch.eventsub.TwitchIntegrationImpl;
import justs_js.integrationapi.impl.twitch.eventsub.event.*;
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
						.enableEvent(TwitchEventTypes.getInstance().INTEGRATION_REFRESH_TOKEN)
						.enableEvent(TwitchEventTypes.getInstance().CHANNEL_CHAT_MESSAGE)
						.enableEvent(TwitchEventTypes.getInstance().CHANNEL_CHANNEL_POINTS_CUSTOM_REWARD_REDEMPTION_ADD)
						.withApiName("Twitch-API")
						.withAuthParam("clientId", "your clientId")
						.withAuthParam("clientSecret", "your clientSecret")
						.withAuthParam("accessToken", "your accessToken")
						.withAuthParam("refreshToken", "your refreshToken")
						.withApiParam("channel.chat.message", "{\"broadcaster_user_id\": \"1337\", \"user_id\": \"1337\"}")
						.withApiParam("channel.channel_points_custom_reward_redemption.add", "{\"broadcaster_user_id\": \"1337\"}")
						.build()
		);
		// 2. Subscribe to relevant events with your custom callbacks
		twitch.subscribe(
				TwitchEventTypes.getInstance().CHANNEL_CHAT_MESSAGE,
				event -> LOGGER.info(
						"[{}]: {}",
						((TwitchChatMessageEvent) event).getChatterUserName(),
						((TwitchChatMessageEvent) event).getMessageText()
				)
		);
		twitch.subscribe(
				TwitchEventTypes.getInstance().INTEGRATION_REFRESH_TOKEN,
				event -> LOGGER.info(
						"Tokens have been refreshed! access_token: {} ; refresh_token: {}",
						((TwitchIntegrationRefreshTokenEvent) event).getAccessToken(),
						((TwitchIntegrationRefreshTokenEvent) event).getRefreshToken()
				)
		);
		twitch.subscribe(
				TwitchEventTypes.getInstance().CHANNEL_CHANNEL_POINTS_CUSTOM_REWARD_REDEMPTION_ADD,
				event -> LOGGER.info(
						"Point Reward Redemption! {} requested {}",
						((TwitchChannelPointsRewardRedemptionEventAdd) event).getUserName(),
						((TwitchChannelPointsRewardRedemptionEventAdd) event).getRewardTitle()
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