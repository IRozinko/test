package fintech.payxpert;

import lombok.Data;
import lombok.experimental.Accessors;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Accessors(chain = true)
public class PayxpertRebill {

    private Long id;
    private LocalDateTime createdAt;
    private Long clientId;
    private BigDecimal amount;
    private String currency;
    private Long loanId;
    private Long invoiceId;
    private RebillStatus status;
    private String errorCode;
    private String errorMessage;
    private String responseTransactionId;
}
