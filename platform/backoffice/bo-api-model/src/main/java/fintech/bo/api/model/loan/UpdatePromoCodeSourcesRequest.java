package fintech.bo.api.model.loan;

import lombok.Data;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotNull;
import java.util.List;

@Data
@Accessors(chain = true)
public class UpdatePromoCodeSourcesRequest {

    @NotNull
    private Long promoCodeId;

    private List<String> sources;

}
