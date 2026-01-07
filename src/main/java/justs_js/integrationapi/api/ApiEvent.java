package justs_js.integrationapi.api;

import java.util.Date;

public abstract class ApiEvent<T extends Enum<T> & EventType> {
    private final String eventId;
    private final String apiName;
    private final Date timestamp;
    private final Object rawData;
    private final T type;

    public ApiEvent(String eventId, String apiName, T type, Object rawData) {
        this.eventId = eventId;
        this.apiName = apiName;
        this.timestamp = new Date();
        this.rawData = rawData;
        this.type = type;
    }

    public String getEventId() { return eventId; }
    public String getApiName() { return apiName; }
    public Date getTimestamp() { return timestamp; }
    public Object getRawData() { return rawData; }
    public T getType() { return type; }

    @Override
    public abstract String toString();
}
