package fintech.lending.core.loan.events;

import fintech.lending.core.loan.Loan;
import lombok.Value;

import java.time.LocalDateTime;

@Value
public class LoanDueDateEvent {

    private final LocalDateTime when;
    private final Loan loan;

}
