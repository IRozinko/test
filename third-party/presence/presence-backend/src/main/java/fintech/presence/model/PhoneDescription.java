package fintech.presence.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

import java.util.HashMap;
import java.util.Map;

public enum PhoneDescription {
    NOT_SPECIFIED,
    MOBILE,
    OTHER;

    private static Map<Integer, PhoneDescription> namesMap = new HashMap<>();

    static {
        namesMap.put(0, NOT_SPECIFIED);
        namesMap.put(1, MOBILE);
        namesMap.put(2, OTHER);
    }

    @JsonCreator
    public static PhoneDescription forValue(Integer value) {
        return namesMap.get(value);
    }

    @JsonValue
    public Integer toValue() {
        for (Map.Entry<Integer, PhoneDescription> entry : namesMap.entrySet()) {
            if (entry.getValue() == this)
                return entry.getKey();
        }

        return 0;
    }
}
