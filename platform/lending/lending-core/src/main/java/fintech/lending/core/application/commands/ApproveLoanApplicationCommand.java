package fintech.lending.core.application.commands;

import lombok.Data;

import java.time.LocalDate;

@Data
public class ApproveLoanApplicationCommand {

    private Long id;
    private Long loanId;
    private LocalDate approveDate;
}
