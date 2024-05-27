package fintech.bo.api.model.payments;

import lombok.Data;

import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

@Data
public class AddOtherTransactionRequest {

    @NotNull
    private Long paymentId;

    @NotNull
    private BigDecimal amount;

    private String transactionSubType;

    private Long clientId;

    private String comments;
}
