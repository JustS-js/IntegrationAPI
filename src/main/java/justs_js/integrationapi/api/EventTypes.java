package justs_js.integrationapi.api;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public abstract class EventTypes<E> {
    private final Map<String, Class<? extends E>> typeToClassMap = new HashMap<>();

    public Class<? extends E> register(@NotNull String type, @NotNull Class<? extends E> eventClass) {
        typeToClassMap.put(type, eventClass);
        return eventClass;
    }

    public Class<? extends E> get(@NotNull String type) {
        return typeToClassMap.get(type);
    }

    public Collection<Class<? extends E>> getRegisteredClasses() {
        return Collections.unmodifiableCollection(typeToClassMap.values());
    }

    public Set<String> getRegisteredTypes() {
        return Collections.unmodifiableSet(typeToClassMap.keySet());
    }
}
