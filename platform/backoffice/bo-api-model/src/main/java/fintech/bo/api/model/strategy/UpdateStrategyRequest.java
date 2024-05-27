package fintech.bo.api.model.strategy;

import lombok.Data;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotNull;

@Data
@Accessors(chain = true)
public class UpdateStrategyRequest {

    @NotNull
    private Long strategyId;

    @NotNull
    private String version;

    private boolean enabled;

    private boolean defaultStrategy;

    private Object properties;
}
