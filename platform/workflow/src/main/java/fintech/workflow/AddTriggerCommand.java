package fintech.workflow;

import lombok.Data;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;

@Data
@Accessors(chain = true)
public class AddTriggerCommand {

    private Long activityId;

    private String name;

    private String params;

    private LocalDateTime nextAttemptAt;
}
