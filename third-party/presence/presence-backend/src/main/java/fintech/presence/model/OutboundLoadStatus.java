package fintech.presence.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

import java.util.HashMap;
import java.util.Map;

public enum OutboundLoadStatus {
    ENABLED,
    DISABLED;

    private static Map<Boolean, OutboundLoadStatus> namesMap = new HashMap<>();

    static {
        namesMap.put(true, ENABLED);
        namesMap.put(false, DISABLED);
    }

    @JsonCreator
    public static OutboundLoadStatus forValue(Boolean value) {
        return namesMap.get(value);
    }

    @JsonValue
    public Boolean toValue() {
        for (Map.Entry<Boolean, OutboundLoadStatus> entry : namesMap.entrySet()) {
            if (entry.getValue() == this)
                return entry.getKey();
        }

        return null;
    }
}
