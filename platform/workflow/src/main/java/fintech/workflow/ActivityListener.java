package fintech.workflow;

import lombok.Data;
import lombok.ToString;

@Data
@ToString(of = {"id", "activityName", "status", "resolution"})
public class ActivityListener {

    private Long id;
    private String name;
    private String workflowName;
    private String activityName;
    private String triggerName;
    private String resolution;
    private ActivityListenerStatus status;
    private Integer delay;
}
