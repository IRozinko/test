package fintech.lending.core.application.events;

import fintech.lending.core.application.LoanApplication;

public class LoanApplicationApprovedEvent extends AbstractLoanApplicationEvent {

    public LoanApplicationApprovedEvent(LoanApplication loanApplication) {
        super(loanApplication);
    }
}
