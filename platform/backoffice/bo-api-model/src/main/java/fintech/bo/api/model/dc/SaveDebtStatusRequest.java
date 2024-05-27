package fintech.bo.api.model.dc;

import lombok.Data;

@Data
public class SaveDebtStatusRequest {

    private Long debtId;
    private String status;
    private String subStatus;
    private String state;

}
