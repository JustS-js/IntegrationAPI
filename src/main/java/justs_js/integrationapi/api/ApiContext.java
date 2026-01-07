package justs_js.integrationapi.api;

import java.util.Collections;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ApiContext {
    private final Map<String, String> config;
    private final Map<String, Object> sessionData;

    public ApiContext(Map<String, String> config) {
        this.config = Collections.unmodifiableMap(config);
        this.sessionData = new ConcurrentHashMap<>();
    }

    public String getConfig(String key) {
        return config.get(key);
    }

    public void setSessionData(String key, Object value) {
        sessionData.put(key, value);
    }

    public Object getSessionData(String key) {
        return sessionData.get(key);
    }
}
