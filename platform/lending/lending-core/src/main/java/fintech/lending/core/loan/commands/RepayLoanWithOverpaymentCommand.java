package fintech.lending.core.loan.commands;

import fintech.TimeMachine;
import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@Builder
public class RepayLoanWithOverpaymentCommand {

    @NotNull
    private Long loanId;

    @NotNull
    private BigDecimal amount;

    @NotNull
    private LocalDate when = TimeMachine.today();

    private String comments;
}
