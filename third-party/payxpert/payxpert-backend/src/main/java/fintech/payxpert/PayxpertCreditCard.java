package fintech.payxpert;

import lombok.Data;
import lombok.ToString;
import lombok.experimental.Accessors;

@ToString(exclude = {"cardNumber", "cardExpireYear", "cardExpireMonth"})
@Data
@Accessors(chain = true)
public class PayxpertCreditCard {

    private Long id;
    private Long clientId;
    private Long paymentRequestId;
    private boolean active;
    private boolean recurringPaymentsEnabled;
    private String callbackTransactionId;
    private String cardNumber;
    private Long cardExpireYear;
    private Long cardExpireMonth;
    private String cardHolderName;
    private String cardBrand;
    private Boolean cardIs3DSecure;
}
