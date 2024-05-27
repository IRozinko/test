package fintech.strategy;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class UpdateCalculationStrategyCommand {

    private Long strategyId;
    private String version;
    private Object properties;
    private boolean enabled;
    private boolean isDefault;
}
