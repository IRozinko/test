package fintech.spain.alfa.product.extension;

import fintech.strategy.model.ExtensionOffer;
import fintech.TimeMachine;
import lombok.Data;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDate;

import static fintech.BigDecimalUtils.amount;

@Data
@Accessors(chain = true)
public class ApplyAndRepayExtensionFeeCommand {

    @NotNull
    private Long loanId;

    @NotNull
    private Long paymentId;

    @NotNull
    private ExtensionOffer extensionOffer;

    @NotNull
    private BigDecimal paymentAmount = amount(0);

    @NotNull
    private BigDecimal overpaymentAmount = amount(0);

    private LocalDate valueDate = TimeMachine.today();

    private String comments;
}
