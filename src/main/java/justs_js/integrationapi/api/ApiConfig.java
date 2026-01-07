package justs_js.integrationapi.api;

import justs_js.integrationapi.IAPIMod;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

public class ApiConfig<T extends EventType> {
    private final String apiName;
    private final Map<String, String> authParams;
    private final Map<String, String> apiParams;
    private final List<T> enabledEvents;
    private final ErrorHandler errorHandler;
    private static final Logger LOGGER = LoggerFactory.getLogger(IAPIMod.MOD_ID);

    private ApiConfig(Builder<T> builder) {
        this.apiName = builder.apiName;
        this.authParams = Collections.unmodifiableMap(builder.authParams);
        this.apiParams = Collections.unmodifiableMap(builder.apiParams);
        this.enabledEvents = Collections.unmodifiableList(builder.enabledEvents);
        this.errorHandler = builder.errorHandler;
    }

    public static class Builder<T extends EventType> {
        private String apiName = "Anonymous API";
        private final Map<String, String> authParams = new HashMap<>();
        private final Map<String, String> apiParams = new HashMap<>();
        private final List<T> enabledEvents = new ArrayList<>();
        private ErrorHandler errorHandler = (error, context) -> LOGGER.error(error.getMessage());

        public Builder<T> withApiName(@NotNull String apiName) {
            this.apiName = apiName;
            return this;
        }

        public Builder<T> withAuthParam(String key, String value) {
            this.authParams.put(key, value);
            return this;
        }

        public Builder<T> withApiParam(String key, String value) {
            this.apiParams.put(key, value);
            return this;
        }

        public Builder<T> enableEvent(T eventType) {
            this.enabledEvents.add(eventType);
            return this;
        }

        public Builder<T> withErrorHandler(ErrorHandler errorHandler) {
            this.errorHandler = errorHandler;
            return this;
        }

        public ApiConfig<T> build() {
            return new ApiConfig<>(this);
        }
    }

    public String getApiName() {
        return apiName;
    }

    public Map<String, String> getAuthParams() {
        return authParams;
    }

    public String getAuthParams(String key) {
        return authParams.get(key);
    }

    public Map<String, String> getApiParams() {
        return apiParams;
    }

    public String getApiParams(String key) {
        return apiParams.get(key);
    }

    public boolean isEventEnabled(T eventType) {
        return enabledEvents.contains(eventType);
    }

    public List<T> getEnabledEvents() {
        return enabledEvents;
    }

    public ErrorHandler getErrorHandler() {
        return errorHandler;
    }
}
