package fintech.lending.core.overpayment;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ApplyOverpaymentCommand {

    @NotNull
    private Long clientId;

    private Long loanId;

    @NotNull
    private Long paymentId;

    @NotNull
    private BigDecimal amount;

    private String comments;
}
