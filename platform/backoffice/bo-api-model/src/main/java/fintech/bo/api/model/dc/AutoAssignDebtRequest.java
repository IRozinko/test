package fintech.bo.api.model.dc;

import lombok.Data;

@Data
public class AutoAssignDebtRequest {

    private Long debtId;
    private String excludeAgent;

}
