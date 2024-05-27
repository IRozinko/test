package fintech.lending.core.application.events;

import fintech.lending.core.application.LoanApplication;

public class LoanApplicationSubmittedEvent extends AbstractLoanApplicationEvent {

    public LoanApplicationSubmittedEvent(LoanApplication loanApplication) {
        super(loanApplication);
    }
}
