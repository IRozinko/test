package fintech.lending.core.loan.events;

import lombok.Value;

@Value
public class LoanEarlyRepaymentEvent {

    private long loanId;
}
