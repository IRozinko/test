package fintech.bo.api.model.workflow;

import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class TerminateWorkflowRequest {

    @NotNull
    private Long workflowId;

    @NotNull
    private String reason;
}
