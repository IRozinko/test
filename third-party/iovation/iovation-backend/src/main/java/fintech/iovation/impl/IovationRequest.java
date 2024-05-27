package fintech.iovation.impl;

import lombok.Data;
import lombok.ToString;

@ToString
@Data
public class IovationRequest {

    private String ipAddress;
    private String accountCode;
    private String beginBlackBox;
}
