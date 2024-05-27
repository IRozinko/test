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
public class WithdrawCommand {

    private Long loanApplicationId;
    private LocalDate date;

}
