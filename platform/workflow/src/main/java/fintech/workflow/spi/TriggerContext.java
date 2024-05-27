package fintech.workflow.spi;

import fintech.workflow.Activity;
import fintech.workflow.Workflow;

public interface TriggerContext {

    Workflow getWorkflow();

    Activity getActivity();
}
