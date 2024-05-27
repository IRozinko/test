package fintech.task.model;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class TaskQuery {

    private Long activityId;
    private Long workflowId;
    private Long clientId;
    private Long loanId;
    private Long parentTaskId;

    private String type;
    private String resolution;

    public static TaskQuery byActivityId(Long activityId) {
        return new TaskQuery().setActivityId(activityId);
    }

    public static TaskQuery byWorkflowId(Long workflowId) {
        return new TaskQuery().setWorkflowId(workflowId);
    }

    public static TaskQuery byParentTaskId(Long parentTaskId) {
        return new TaskQuery().setParentTaskId(parentTaskId);
    }

    public static TaskQuery byClientId(Long clientId, String taskType) {
        return new TaskQuery().setClientId(clientId).setType(taskType);
    }

}
