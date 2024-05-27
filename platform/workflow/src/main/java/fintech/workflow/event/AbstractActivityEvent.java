package fintech.workflow.event;

import fintech.workflow.Activity;
import fintech.workflow.Workflow;

public abstract class AbstractActivityEvent extends AbstractWorkflowEvent{

    private final Activity activity;

    public AbstractActivityEvent(Workflow workflow, Activity activity) {
        super(workflow);
        this.activity = activity;
    }

    public Activity getActivity() {
        return activity;
    }
}
