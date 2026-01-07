package justs_js.integrationapi.api;

import justs_js.integrationapi.IAPIMod;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public abstract class ApiIntegration<E extends ApiEvent<T>, T extends Enum<T> & EventType> {
    private static final Logger LOGGER = LoggerFactory.getLogger(IAPIMod.MOD_ID);
    protected final Class<T> eventTypeClass;

    private final ApiConfig<T> config;
    private final ApiContext context;
    private final Map<T, List<EventCallback<E>>> callbacks;
    private final ExecutorService callbackExecutor;

    private volatile boolean isRunning = false;
    private final Object lifecycleLock = new Object();

    protected ApiIntegration(ApiConfig<T> config, Class<T> eventTypeClass) {
        this.eventTypeClass = eventTypeClass;
        this.config = config;
        this.context = new ApiContext(config.getAuthParams());
        this.callbacks = new ConcurrentHashMap<>();
        this.callbackExecutor = Executors.newCachedThreadPool(r -> {
            Thread thread = new Thread(r);
            thread.setName("api-callback-" + config.getApiName() + "-" + thread.getId());
            thread.setDaemon(true);
            return thread;
        });
    }

    public void subscribe(EventCallback<E> listener) {
        for (T eventType : eventTypeClass.getEnumConstants()) {
            subscribe(eventType, listener);
        }
    }

    public void subscribe(T eventType, EventCallback<E> listener) {
        callbacks.computeIfAbsent(eventType, k -> new CopyOnWriteArrayList<>()).add(listener);
    }

    public void unsubscribe(EventCallback<E> listener) {
        for (T eventType : eventTypeClass.getEnumConstants()) {
            unsubscribe(eventType, listener);
        }
    }

    public void unsubscribe(T eventType, EventCallback<E> listener) {
        List<EventCallback<E>> listeners = callbacks.get(eventType);
        if (listeners != null) {
            listeners.remove(listener);
        }
    }

    public final boolean start() {
        synchronized (lifecycleLock) {
            if (isRunning) return false;

            try {
                onInit();
                connect();
                isRunning = true;
                LOGGER.debug("API Integration '{}' started", config.getApiName());
                return true;
            } catch (Exception e) {
                handleError(e);
            }
            return false;
        }
    }

    public final void stop() {
        synchronized (lifecycleLock) {
            if (!isRunning) return;

            try {
                disconnect();
                onShutdown();
                isRunning = false;
                callbackExecutor.shutdown();
                LOGGER.debug("API Integration '{}' stopped", config.getApiName());
            } catch (Exception e) {
                handleError(e);
            }
        }
    }

    public boolean isEventEnabled(T eventType) {
        return getConfig().isEventEnabled(eventType);
    }

    protected void publishEvent(E event) {
        if (!isRunning) return;

        callbacks.getOrDefault(event.getType(), Collections.emptyList())
                .forEach(callback -> invokeCallback(callback, event));
    }

    private void invokeCallback(EventCallback<E> callback, E event) {
        callbackExecutor.submit(() -> {
            try {
                callback.onEvent(event);
            } catch (Exception e) {
                handleError(e);
            }
        });
    }


    protected void handleError(Throwable error) {
        if (config.getErrorHandler() != null) {
            config.getErrorHandler().onError(error, context);
        }
    }

    protected abstract void onInit() throws Exception;
    protected abstract void connect() throws Exception;
    protected abstract void disconnect() throws Exception;
    protected abstract void onShutdown() throws Exception;

    public boolean isRunning() { return isRunning; }
    protected ApiConfig<T> getConfig() { return config; }
    protected ApiContext getContext() { return context; }
}
