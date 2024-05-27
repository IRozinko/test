package fintech.lending.core.loan.events;

import fintech.lending.core.loan.Loan;

public class LoanDisbursedEvent extends AbstractLoanEvent {

    public LoanDisbursedEvent(Loan loan) {
        super(loan);
    }
}
