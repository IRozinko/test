package fintech.lending.core.loan.events;

import fintech.lending.core.loan.Loan;

public class LoanVoidedEvent extends AbstractLoanEvent {

    public LoanVoidedEvent(Loan loan) {
        super(loan);
    }
}
