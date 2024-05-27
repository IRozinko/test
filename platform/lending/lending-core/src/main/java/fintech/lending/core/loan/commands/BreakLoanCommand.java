package fintech.lending.core.loan.commands;

import lombok.Data;

import java.time.LocalDate;

@Data
public class BreakLoanCommand {

    private Long loanId;

    private String reasonForBreak;

    private LocalDate when;

}
