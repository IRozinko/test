package fintech.lending.core.payments;

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
public class AddPaymentTransactionCommand {

    @NotNull
    private Long paymentId;
    @NotNull
    private BigDecimal amount;

    @NotNull
    private String transactionSubType;

    private Long clientId;

    private String comments;
}
