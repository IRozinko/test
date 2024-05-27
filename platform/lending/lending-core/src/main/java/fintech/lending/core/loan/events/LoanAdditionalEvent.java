package fintech.lending.core.loan.events;

import fintech.lending.core.loan.Loan;
import lombok.Value;

import java.math.BigDecimal;

@Value
public class LoanAdditionalEvent extends AbstractLoanEvent {

    BigDecimal disbursedAmount;

    public LoanAdditionalEvent(Loan loan, BigDecimal disbursedAmount) {
        super(loan);
        this.disbursedAmount = disbursedAmount;
    }
}
