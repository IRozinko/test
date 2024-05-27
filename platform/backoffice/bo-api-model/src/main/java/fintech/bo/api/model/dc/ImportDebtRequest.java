package fintech.bo.api.model.dc;

import lombok.Data;

@Data
public class ImportDebtRequest {

    private Long fileId;
    private Long institutionId;
    private String companyName;
    private String portfolioName;
    private String debtState;
    private String debtStatus;
}
