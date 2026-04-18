package justs_js.integrationapi.api;

@FunctionalInterface
public interface EventCallback<E extends ApiEvent> {
    void onEvent(E event);
}
