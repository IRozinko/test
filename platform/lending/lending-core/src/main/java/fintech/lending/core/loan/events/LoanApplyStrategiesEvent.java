package fintech.lending.core.loan.events;

import lombok.Value;
@Value
public class LoanApplyStrategiesEvent {

    Long loanId;
    public LoanApplyStrategiesEvent(Long loanId) {
        this.loanId = loanId;
    }
}
