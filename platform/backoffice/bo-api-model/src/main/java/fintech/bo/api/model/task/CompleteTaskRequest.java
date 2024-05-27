package fintech.bo.api.model.task;

import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class CompleteTaskRequest {

    @NotNull
    private Long taskId;

    @NotNull
    private String resolution;

    private String resolutionDetail;

    private String resolutionSubDetail;

    private String comment;

    private boolean postpone;

    private Long postponeByHours;

}
