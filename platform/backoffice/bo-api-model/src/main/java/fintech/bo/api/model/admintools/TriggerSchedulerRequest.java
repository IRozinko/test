package fintech.bo.api.model.admintools;

import lombok.Data;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotNull;

@Data
@Accessors(chain = true)
public class TriggerSchedulerRequest {

    @NotNull
    private String name;

}
