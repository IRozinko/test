package fintech.lending.core.application.events;

import fintech.lending.core.application.LoanApplication;

public class AbstractLoanApplicationEvent {

    private final LoanApplication loanApplication;

    public AbstractLoanApplicationEvent(LoanApplication loanApplication) {
        this.loanApplication = loanApplication;
    }

    public LoanApplication getLoanApplication() {
        return loanApplication;
    }
}
