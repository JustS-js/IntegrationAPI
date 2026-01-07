package justs_js.integrationapi.impl.twitch.eventsub;

import justs_js.integrationapi.api.ApiConfig;
import justs_js.integrationapi.api.ApiIntegration;
import justs_js.integrationapi.impl.twitch.eventsub.event.TwitchEvent;

public class TwitchIntegrationImpl extends ApiIntegration<TwitchEvent, TwitchEventType> {
    public TwitchIntegrationImpl(ApiConfig<TwitchEventType> config) {
        super(config, TwitchEventType.class);
    }

    @Override
    protected void onInit() throws Exception {

    }

    @Override
    protected void connect() throws Exception {

    }

    @Override
    protected void disconnect() throws Exception {

    }

    @Override
    protected void onShutdown() throws Exception {

    }
}
