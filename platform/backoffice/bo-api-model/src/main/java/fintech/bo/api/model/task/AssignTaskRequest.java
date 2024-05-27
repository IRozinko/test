package fintech.bo.api.model.task;

import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class AssignTaskRequest {

    @NotNull
    private Long taskId;

    @NotNull
    private String agent;

    private String comment;
}
