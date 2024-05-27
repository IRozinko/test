package fintech.lending.core.application.events;

import fintech.lending.core.application.LoanApplication;

public class LoanApplicationRejectedEvent extends AbstractLoanApplicationEvent {

    public LoanApplicationRejectedEvent(LoanApplication loanApplication) {
        super(loanApplication);
    }
}
