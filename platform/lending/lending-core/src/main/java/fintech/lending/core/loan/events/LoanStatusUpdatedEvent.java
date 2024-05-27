package fintech.lending.core.loan.events;

import fintech.lending.core.loan.Loan;

public class LoanStatusUpdatedEvent extends AbstractLoanEvent {

    public LoanStatusUpdatedEvent(Loan loan) {
        super(loan);
    }
}
