package fintech.accounting;

import lombok.Data;

import java.math.BigDecimal;

import static fintech.BigDecimalUtils.amount;
import static fintech.BigDecimalUtils.isZero;

@Data
public class AccountTrialBalance {

    private String accountCode;
    private String accountName;
    private BigDecimal openingDebit = amount(0);
    private BigDecimal openingCredit = amount(0);
    private BigDecimal turnoverDebit = amount(0);
    private BigDecimal turnoverCredit = amount(0);
    private BigDecimal closingDebit = amount(0);
    private BigDecimal closingCredit = amount(0);

    public boolean isEmpty() {
        return isZero(openingDebit)
                && isZero(openingCredit)
                && isZero(turnoverDebit)
                && isZero(turnoverCredit)
                && isZero(closingDebit)
                && isZero(closingCredit);
    }
}
