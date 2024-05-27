package fintech.lending.core.loan.events;

import fintech.lending.core.loan.Loan;

public class LoanIssuedEvent extends AbstractLoanEvent {

    public LoanIssuedEvent(Loan loan) {
        super(loan);
    }
}
