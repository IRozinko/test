package fintech.bo.api.model.loan;

import lombok.Data;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotNull;

@Data
@Accessors(chain = true)
public class UpdatePromoCodeClientsRequest {

    @NotNull
    private Long promoCodeId;

    @NotNull
    private Long clientFileId;

}
