package fintech.lending.core.application.events;

import fintech.lending.core.application.LoanApplication;

public class LoanApplicationRetriedEvent extends AbstractLoanApplicationEvent {

    public LoanApplicationRetriedEvent(LoanApplication loanApplication) {
        super(loanApplication);
    }

}
