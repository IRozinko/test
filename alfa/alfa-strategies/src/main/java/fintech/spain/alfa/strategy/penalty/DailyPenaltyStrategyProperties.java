package fintech.spain.alfa.strategy.penalty;

import lombok.Data;
import lombok.experimental.Accessors;

import java.math.BigDecimal;

@Data
@Accessors(chain = true)
public class DailyPenaltyStrategyProperties {
    private BigDecimal penaltyRate;
}
