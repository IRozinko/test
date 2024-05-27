package fintech.workflow.event;


import fintech.workflow.Workflow;

public class WorkflowExpiredEvent extends AbstractWorkflowEvent {
    public WorkflowExpiredEvent(Workflow workflow) {
        super(workflow);
    }
}
