package fintech.iovation.impl;

import lombok.Data;

import java.util.HashMap;
import java.util.Map;

@Data
public class IovationResponse {

    private static final String DEVICE_ID_PROPERTY_NAME = "device.alias";

    private String endBlackBox;
    private String result;
    private String reason;
    private String trackingNumber;
    private Map<String, String> details = new HashMap<>();

    public String getDeviceId() {
        return details.get(DEVICE_ID_PROPERTY_NAME);
    }
}
