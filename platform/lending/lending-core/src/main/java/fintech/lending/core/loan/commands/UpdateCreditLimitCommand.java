package fintech.lending.core.loan.commands;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDate;

import static fintech.BigDecimalUtils.amount;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateCreditLimitCommand {

    @NotNull
    private Long loanId;

    @NotNull
    private BigDecimal amount = amount(0);

    @NotNull
    private LocalDate valueDate;

    private String comments;
}
