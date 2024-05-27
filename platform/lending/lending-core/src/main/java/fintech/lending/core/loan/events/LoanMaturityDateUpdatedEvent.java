package fintech.lending.core.loan.events;

import fintech.lending.core.loan.Loan;

public class LoanMaturityDateUpdatedEvent extends AbstractLoanEvent {

    public LoanMaturityDateUpdatedEvent(Loan loan) {
        super(loan);
    }
}
