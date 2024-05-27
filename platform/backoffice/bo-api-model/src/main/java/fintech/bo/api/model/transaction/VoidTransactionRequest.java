package fintech.bo.api.model.transaction;

import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class VoidTransactionRequest {

    @NotNull
    private Long transactionId;
}
