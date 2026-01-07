package justs_js.integrationapi.api;

import java.util.Date;

public abstract class ApiEvent {
    private final String eventId;
    private final String apiName;
    private final Date timestamp;
    private final Object rawData;

    public ApiEvent(String eventId, String apiName, Object rawData) {
        this.eventId = eventId;
        this.apiName = apiName;
        this.timestamp = new Date();
        this.rawData = rawData;
    }

    public String getEventId() { return eventId; }
    public String getApiName() { return apiName; }
    public Date getTimestamp() { return timestamp; }
    public Object getRawData() { return rawData; }
}
