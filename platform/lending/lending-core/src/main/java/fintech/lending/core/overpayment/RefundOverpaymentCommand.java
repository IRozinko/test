package fintech.lending.core.overpayment;

import fintech.TimeMachine;
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
public class RefundOverpaymentCommand {

    @NotNull
    private Long paymentId;

    @NotNull
    private BigDecimal amount;

    private LocalDate when = TimeMachine.today();

    @NotNull
    private Long clientId;

    @NotNull
    private Long loanId;

    private String comments;
}
