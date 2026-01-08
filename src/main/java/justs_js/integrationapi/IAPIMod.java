package justs_js.integrationapi;

import justs_js.integrationapi.impl.twitch.eventsub.TwitchIntegrationImpl;
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
	}
}