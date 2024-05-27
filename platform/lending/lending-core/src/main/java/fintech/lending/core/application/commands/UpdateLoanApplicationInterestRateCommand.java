package fintech.lending.core.application.commands;

import lombok.Data;

import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

@Data
public class UpdateLoanApplicationInterestRateCommand {

    @NotNull
    private Long applicationId;

    @NotNull
    private BigDecimal nominalInterestRate;

    @NotNull
    private BigDecimal effectiveInterestRate;

}
