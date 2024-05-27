package fintech.task.command;

import fintech.TimeMachine;
import lombok.Data;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Data
@Accessors(chain = true)
public class AddTaskCommand {

    private Long clientId;
    private Long applicationId;
    private Long loanId;
    private Long activityId;
    private Long workflowId;
    private Long installmentId;
    private Long parentTaskId;
    private String type;
    private String group;
    private LocalDateTime dueAt = TimeMachine.now();
    private LocalDateTime expiresAt;
    private Map<String, String> attributes = new HashMap<>();

}
