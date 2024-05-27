package fintech.lending.core.loan.commands;

import lombok.Data;
import lombok.experimental.Accessors;

import java.math.BigDecimal;
import java.time.LocalDate;

import static fintech.BigDecimalUtils.amount;

@Data
@Accessors(chain = true)
public class WriteOffAmountCommand {

    private Long loanId;

    private LocalDate when;

    private BigDecimal principal = amount(0);

    private BigDecimal interest = amount(0);

    private BigDecimal penalty = amount(0);

    private BigDecimal fee = amount(0);

    private String comments;

    private String subType;

}
