package fintech.bo.api.model.payments;

import lombok.Data;

import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

@Data
public class OperateOverpaymentTransactionRequest {

    @NotNull
    private Long paymentId;

    @NotNull
    private BigDecimal amount;

    @NotNull
    private Long clientId;

    private Long loanId;

    private String comments;
}
