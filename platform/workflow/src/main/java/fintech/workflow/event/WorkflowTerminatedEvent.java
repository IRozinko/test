package fintech.workflow.event;


import fintech.workflow.Workflow;

public class WorkflowTerminatedEvent extends AbstractWorkflowEvent {
    public WorkflowTerminatedEvent(Workflow workflow) {
        super(workflow);
    }
}
