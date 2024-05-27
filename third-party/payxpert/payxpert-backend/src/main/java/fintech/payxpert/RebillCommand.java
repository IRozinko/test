package fintech.payxpert;

import lombok.Data;
import lombok.experimental.Accessors;

import java.math.BigDecimal;

@Data
@Accessors(chain = true)
public class RebillCommand {

    private Long clientId;
    private Long creditCardId;
    private Long loanId;
    private Long invoiceId;
    private BigDecimal amount;
    private String currency;
}
