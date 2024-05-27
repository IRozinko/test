package fintech.email.spi;


import lombok.Builder;
import lombok.Value;
import lombok.experimental.NonFinal;

@Value
@Builder
public class EmailResponse {

    private String id;
    private String providerName;

    @NonFinal
    private String message;
}
