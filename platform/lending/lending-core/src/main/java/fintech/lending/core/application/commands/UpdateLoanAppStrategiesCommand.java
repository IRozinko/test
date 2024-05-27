package fintech.lending.core.application.commands;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.experimental.Accessors;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Data
@Accessors(chain = true)
public class UpdateLoanAppStrategiesCommand {

    private Long applicationId;

    private Long interestStrategyId;
    private Long penaltyStrategyId;
    private Long extensionStrategyId;
    private Long feeStrategyId;

    public UpdateLoanAppStrategiesCommand(Long applicationId) {
        this.applicationId = applicationId;
    }
}
