package fintech.workflow.spi;

import fintech.workflow.Workflow;

public interface WorkflowListenerContext {

    Workflow getWorkflow();

    WorkflowDefinition getDefinition();
}
