package justs_js.integrationapi.api;

@FunctionalInterface
public interface ErrorHandler {
    void onError(Throwable error, ApiContext context);
}
