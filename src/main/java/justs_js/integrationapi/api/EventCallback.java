package justs_js.integrationapi.api;

@FunctionalInterface
public interface EventCallback<T extends ApiEvent> {
    void onEvent(T event);
}
