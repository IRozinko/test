package fintech.strategy;

import lombok.Data;

@Data
public class CalculationStrategy {

    private Long id;

    private String strategyType;

    private String calculationType;

    private String version;

    private boolean enabled;

    private Boolean isDefault;
}
