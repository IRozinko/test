package fintech.task.command;

import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class ExpireTaskCommand {

    private Long taskId;

}
