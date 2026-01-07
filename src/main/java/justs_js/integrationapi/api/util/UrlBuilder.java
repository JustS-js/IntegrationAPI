package justs_js.integrationapi.api.util;

import java.util.HashMap;
import java.util.Map;

public class UrlBuilder {
    private final String baseUrl;
    private final Map<String, String> params;

    public UrlBuilder(String baseUrl) {
        this.baseUrl = baseUrl;
        this.params = new HashMap<>();
    }

    public UrlBuilder withParam(String key, String value) {
        params.put(key, value);
        return this;
    }

    public String build() {
        String form = params.entrySet().stream()
                .map(e -> e.getKey() + "=" + e.getValue())
                .reduce((a, b) -> a + "&" + b)
                .orElse("");
        return baseUrl + "?" + form;
    }
}
