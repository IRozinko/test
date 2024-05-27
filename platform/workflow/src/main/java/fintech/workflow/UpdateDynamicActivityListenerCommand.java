package fintech.workflow;

import lombok.Data;
import lombok.experimental.Accessors;

import java.time.Duration;

@Data
@Accessors(chain = true)
public class UpdateDynamicActivityListenerCommand {

    private Long id;

    private String name;
    private String workflowName;
    private int version;

    private ActivityListenerStatus listenerStatus;
    private String triggerName;
    private String resolution;
    private String[] args;
    private String activityName;

    private Duration delay;
    private Boolean fromMidnight;

}
