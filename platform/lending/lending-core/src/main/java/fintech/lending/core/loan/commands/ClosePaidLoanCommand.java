package fintech.lending.core.loan.commands;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ClosePaidLoanCommand {

    private Long loanId;

}
