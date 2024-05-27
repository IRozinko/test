package fintech.payxpert;

import lombok.Data;
import lombok.experimental.Accessors;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@Accessors(chain = true)
public class CardAuthorizationRequestCommand {

    private Long clientId;
    private BigDecimal amount;
    private String currency;

    private String redirectUrl;
    private String callbackUrl;

    private String clientEmail;
    private String clientFirstName;
    private String clientLastName;
    private String clientPhone;
    private LocalDate clientDateOfBirth;
    private String clientIdNumber;

    private String orderDescription;
    private String orderId;
}
