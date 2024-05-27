package fintech.bo.api.model.admintools;

import lombok.Data;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotNull;
import java.util.Map;

@Data
@Accessors(chain = true)
public class RunDemoScenarioRequest {

    @NotNull
    private String name;

    private Map<String,String> parameters;
}
