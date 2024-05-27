package fintech.lending.core.loan.commands;

import lombok.Data;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

@Data
@Accessors(chain = true)
public class SettleDisbursementCommand {

    @NotNull
    private Long disbursementId;

    private Long paymentId;

    private String comments;

    private BigDecimal amount;
}
