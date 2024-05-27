package fintech.bo.api.model.workflow;

import lombok.Data;
import lombok.experimental.Accessors;

import java.util.List;

@Accessors(chain = true)
@Data
public class WorkflowInfoResponse {

    private String workflowName;
    private int workflowVersion;
    private List<ActivityInfoResponse> activities;

}
