package fintech.lending.core.loan.commands;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class IssueLoanCommand {

    private Long loanApplicationId;
    private String loanNumber;
    private LocalDate issueDate;
}
