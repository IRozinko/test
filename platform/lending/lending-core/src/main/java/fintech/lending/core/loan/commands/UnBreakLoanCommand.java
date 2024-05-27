package fintech.lending.core.loan.commands;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDate;

@Data
@AllArgsConstructor
public class UnBreakLoanCommand {

    private Long loanId;

    private LocalDate when;

}
