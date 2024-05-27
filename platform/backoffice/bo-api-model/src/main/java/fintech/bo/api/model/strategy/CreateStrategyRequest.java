package fintech.bo.api.model.strategy;

import lombok.Data;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotNull;

@Data
@Accessors(chain = true)
public class CreateStrategyRequest {

    @NotNull
    private String strategyType;

    @NotNull
    private String calculationType;

    @NotNull
    private String version;

    private boolean enabled;

    private boolean defaultStrategy;

    private Object properties;
}
