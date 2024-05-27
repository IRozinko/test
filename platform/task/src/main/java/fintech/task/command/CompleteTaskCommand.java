package fintech.task.command;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class CompleteTaskCommand {

    private Long taskId;
    private String resolution;
    private String resolutionDetail;
    private String resolutionSubDetail;
    private String comment;
}
