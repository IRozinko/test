package fintech.spain.callcenter;

import lombok.Data;

@Data
public class Call {
    private Long id;
    private Long providerId;
    private Long clientId;
    private CallStatus status;
}
