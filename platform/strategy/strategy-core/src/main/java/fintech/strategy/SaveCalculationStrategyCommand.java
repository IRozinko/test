package fintech.strategy;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class SaveCalculationStrategyCommand {

    private String strategyType;
    private String calculationType;
    private String version;
    private Object properties;
    private boolean enabled;
    private boolean isDefault;

    public String strategyFullName() {
        return strategyType + calculationType + version;
    }
}
