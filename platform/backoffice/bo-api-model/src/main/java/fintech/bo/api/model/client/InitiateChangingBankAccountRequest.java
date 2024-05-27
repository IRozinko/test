package fintech.bo.api.model.client;

import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class InitiateChangingBankAccountRequest {

    @NotNull
    long clientId;
}
