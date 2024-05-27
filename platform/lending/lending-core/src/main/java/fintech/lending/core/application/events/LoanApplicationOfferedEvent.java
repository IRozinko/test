package fintech.lending.core.application.events;

import fintech.lending.core.application.LoanApplication;

public class LoanApplicationOfferedEvent extends AbstractLoanApplicationEvent {

    public LoanApplicationOfferedEvent(LoanApplication loanApplication) {
        super(loanApplication);
    }
}
