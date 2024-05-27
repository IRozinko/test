package fintech.bo.api.model.payments;

import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class VoidPaymentRequest {

    @NotNull
    private Long paymentId;
}
