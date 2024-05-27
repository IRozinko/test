package fintech.workflow.event;

import fintech.workflow.Workflow;

public abstract class AbstractWorkflowEvent {

    private final Workflow workflow;

    protected AbstractWorkflowEvent(Workflow workflow) {
        this.workflow = workflow;
    }

    public Workflow getWorkflow() {
        return workflow;
    }
}
