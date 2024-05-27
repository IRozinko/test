package fintech.lending.core.application.events;

import fintech.lending.core.application.LoanApplication;

public class LoanApplicationCancelledEvent extends AbstractLoanApplicationEvent {

    public LoanApplicationCancelledEvent(LoanApplication loanApplication) {
        super(loanApplication);
    }
}
