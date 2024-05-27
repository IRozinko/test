package fintech.bo.api.model.movements;

import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class ParseBankMovementsRequest {
    @NotNull
    Long fileId;
    @NotNull
    Long institutionId;
}
