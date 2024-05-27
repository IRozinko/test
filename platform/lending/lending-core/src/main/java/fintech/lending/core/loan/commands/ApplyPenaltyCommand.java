package fintech.lending.core.loan.commands;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDate;

import static fintech.BigDecimalUtils.amount;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
public class ApplyPenaltyCommand {

    @NotNull
    private Long loanId;
    private Long installmentId;
    @NotNull
    private BigDecimal amount = amount(0);
    @NotNull
    private BigDecimal amountInvoiced = amount(0);
    @NotNull
    private LocalDate valueDate;
    private String subType;
    private Long invoiceId;
    private String comments;
}
