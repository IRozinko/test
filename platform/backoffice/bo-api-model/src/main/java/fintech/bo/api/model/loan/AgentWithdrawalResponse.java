package fintech.bo.api.model.loan;

import lombok.Data;
import lombok.experimental.Accessors;

@Accessors(chain = true)
@Data
public class AgentWithdrawalResponse {

    private Long withdrawalApplicationId;

}
