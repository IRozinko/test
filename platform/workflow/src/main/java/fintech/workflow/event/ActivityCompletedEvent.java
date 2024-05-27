package fintech.workflow.event;

import fintech.workflow.Activity;
import fintech.workflow.Workflow;

public class ActivityCompletedEvent extends AbstractActivityEvent {

    public ActivityCompletedEvent(Workflow workflow, Activity activity) {
        super(workflow, activity);
    }
}
