package fintech.workflow.event;


import fintech.workflow.Workflow;

public class WorkflowStartedEvent extends AbstractWorkflowEvent {
    public WorkflowStartedEvent(Workflow workflow) {
        super(workflow);
    }
}
