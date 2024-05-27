package fintech.lending.core.loan.events;

import fintech.lending.core.loan.Loan;
import lombok.ToString;

@ToString
public class AbstractLoanEvent {

    private final Loan loan;

    public AbstractLoanEvent(Loan loan) {
        this.loan = loan;
    }

    public Loan getLoan() {
        return loan;
    }
}
