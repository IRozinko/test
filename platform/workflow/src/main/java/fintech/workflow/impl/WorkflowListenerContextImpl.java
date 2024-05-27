package fintech.workflow.impl;


import fintech.workflow.Workflow;
import fintech.workflow.spi.WorkflowDefinition;
import fintech.workflow.spi.WorkflowListenerContext;

public class WorkflowListenerContextImpl implements WorkflowListenerContext {

    private final WorkflowDefinition workflowDefinition;
    private final Workflow workflow;


    public WorkflowListenerContextImpl(WorkflowDefinition workflowDefinition, Workflow workflow) {
        this.workflowDefinition = workflowDefinition;
        this.workflow = workflow;
    }

    @Override
    public Workflow getWorkflow() {
        return workflow;
    }

    @Override
    public WorkflowDefinition getDefinition() {
        return workflowDefinition;
    }
}
