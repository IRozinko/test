package fintech.iovation.model;

import lombok.Data;

import java.util.HashMap;
import java.util.Map;

@Data
public class IovationTransaction {

    private Long id;
    private Long clientId;
    private Long applicationId;
    private String ipAddress;
    private String blackBox;
    private IovationStatus status;
    private String endBlackBox;
    private String result;
    private String reason;
    private String trackingNumber;
    private String deviceId;
    private String error;
    private Map<String, String> details = new HashMap<>();
}
