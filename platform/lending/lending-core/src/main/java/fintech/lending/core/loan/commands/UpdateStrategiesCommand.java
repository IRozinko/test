package fintech.lending.core.loan.commands;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotNull;

@Data
@Accessors(chain = true)
@AllArgsConstructor
@NoArgsConstructor
public class UpdateStrategiesCommand {

    @NotNull
    private Long loanId;

    private Long interestStrategyId;
    private Long feeStrategyId;
    private Long penaltyStrategyId;

}
