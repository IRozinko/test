package fintech.workflow.event;


import fintech.workflow.Workflow;

public class WorkflowCompletedEvent extends AbstractWorkflowEvent {
    public WorkflowCompletedEvent(Workflow workflow) {
        super(workflow);
    }
}
