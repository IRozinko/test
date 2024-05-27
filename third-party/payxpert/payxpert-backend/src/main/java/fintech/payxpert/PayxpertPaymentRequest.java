package fintech.payxpert;

import lombok.Data;
import lombok.ToString;
import lombok.experimental.Accessors;

import java.math.BigDecimal;

@ToString(exclude = {"cardNumber", "cardExpireYear", "cardExpireMonth"})
@Data
@Accessors(chain = true)
public class PayxpertPaymentRequest {

    private Long id;
    private Long clientId;
    private BigDecimal amount;
    private String currency;
    private PaymentRequestStatus status;
    private String statusDetail;
    private String orderId;
    private String customerRedirectUrl;
    private String merchantToken;
    private String customerToken;
    private String callbackTransactionId;
    private String cardNumber;
    private Long cardExpireYear;
    private Long cardExpireMonth;
    private String cardHolderName;
    private String cardBrand;
    private Boolean cardIs3DSecure;
    private Long statusCheckAttempts;
    private boolean saveCreditCard;
}
