package fintech.bo.api.model.disbursements;

import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class ExportDisbursementsRequest {

    @NotNull
    private Long institutionId;

    @NotNull
    private Long institutionAccountId;
}
