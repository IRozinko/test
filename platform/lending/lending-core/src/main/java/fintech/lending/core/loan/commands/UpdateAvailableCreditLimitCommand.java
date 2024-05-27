package fintech.lending.core.loan.commands;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateAvailableCreditLimitCommand {

    @NotNull
    private Long loanId;

    @NotNull
    private LocalDate valueDate;

    @NotNull
    private BigDecimal amount;

}
