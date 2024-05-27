package fintech.bo.api.model.disbursements;

import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class ExportSingleDisbursementRequest {

    @NotNull
    private Long disbursementId;
}
