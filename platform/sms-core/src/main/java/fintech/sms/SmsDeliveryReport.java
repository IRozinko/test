package fintech.sms;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class SmsDeliveryReport {

    private String providerMessageId;
    private String status;
    private String status2;
    private String error;
    private LocalDateTime receivedAt;
}
