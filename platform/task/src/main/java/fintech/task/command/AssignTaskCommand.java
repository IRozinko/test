package fintech.task.command;

import fintech.TimeMachine;
import lombok.Data;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;

@Data
@Accessors(chain = true)
public class AssignTaskCommand {

    private Long taskId;
    private String agent;
    private LocalDateTime when = TimeMachine.now();
    private String comment;
}
