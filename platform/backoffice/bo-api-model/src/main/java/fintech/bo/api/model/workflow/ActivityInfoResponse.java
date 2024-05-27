package fintech.bo.api.model.workflow;

import lombok.Data;
import lombok.experimental.Accessors;

import java.util.Set;

@Accessors(chain = true)
@Data
public class ActivityInfoResponse {

    private String activityName;
    private Set<String> resolutions;
}
