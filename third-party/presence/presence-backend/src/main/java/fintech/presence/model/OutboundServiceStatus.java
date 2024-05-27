package fintech.presence.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

import java.util.HashMap;
import java.util.Map;

public enum OutboundServiceStatus {
    ENABLED,
    DISABLED;

    private static Map<String, OutboundServiceStatus> namesMap = new HashMap<>();

    static {
        namesMap.put("E", ENABLED);
        namesMap.put("D", DISABLED);
    }

    @JsonCreator
    public static OutboundServiceStatus forValue(String value) {
        return namesMap.get(value);
    }

    @JsonValue
    public String toValue() {
        for (Map.Entry<String, OutboundServiceStatus> entry : namesMap.entrySet()) {
            if (entry.getValue() == this)
                return entry.getKey();
        }

        return null;
    }
}
