package fintech.workflow.event;


import fintech.workflow.Activity;
import fintech.workflow.Workflow;

public class ActivityStartedEvent extends AbstractActivityEvent {

    public ActivityStartedEvent(Workflow workflow, Activity activity) {
        super(workflow, activity);
    }
}
