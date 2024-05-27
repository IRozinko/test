package fintech.bo.api.model.payments;

import lombok.Data;

import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

@Data
public class AddDisbursementSettledTransactionRequest {

    @NotNull
    private Long paymentId;

    @NotNull
    private BigDecimal paymentAmount;

    @NotNull
    private Long disbursementId;

    private String comments;

}
