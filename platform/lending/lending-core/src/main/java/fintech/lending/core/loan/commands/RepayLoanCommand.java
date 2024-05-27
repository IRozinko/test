package fintech.lending.core.loan.commands;

import lombok.Data;
import lombok.experimental.Accessors;

import java.math.BigDecimal;
import java.time.LocalDate;

import static fintech.BigDecimalUtils.amount;

@Data
@Accessors(chain = true)
public class RepayLoanCommand {

    private Long loanId;

    private Long paymentId;

    private BigDecimal paymentAmount = amount(0);

    private BigDecimal overpaymentAmount = amount(0);

    private LocalDate valueDate;

    private String comments;

}
