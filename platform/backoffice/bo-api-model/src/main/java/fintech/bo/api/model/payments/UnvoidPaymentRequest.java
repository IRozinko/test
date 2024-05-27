package fintech.bo.api.model.payments;

import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class UnvoidPaymentRequest {

    @NotNull
    private Long paymentId;
}
