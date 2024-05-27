package fintech.sms;

import lombok.Data;

@Data
public class IncomingSms {
    private String source;
    private String phoneNumber;
    private String text;
    private String rawDataJson;
}
