package fintech.bo.api.model.loan;

import lombok.Data;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

@Data
@Accessors(chain = true)
public class AgentWithdrawalRequest {

    @NotNull
    private Long loanId;

    @NotNull
    private BigDecimal amount;

    @NotNull
    private Long term;

}
