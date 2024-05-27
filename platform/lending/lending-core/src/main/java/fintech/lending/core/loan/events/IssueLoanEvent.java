package fintech.lending.core.loan.events;

import fintech.lending.core.loan.commands.CreateLoanCommand;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class IssueLoanEvent {

    CreateLoanCommand createLoanCommand;
}
