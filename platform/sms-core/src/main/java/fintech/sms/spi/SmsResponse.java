package fintech.sms.spi;


import lombok.Builder;
import lombok.Value;
import lombok.experimental.NonFinal;

@Value
@Builder
public class SmsResponse {

    private String id;
    private String providerName;

    @NonFinal
    private String message;
}
