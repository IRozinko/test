package fintech.workflow.impl;

import fintech.workflow.Activity;
import fintech.workflow.Workflow;
import fintech.workflow.spi.TriggerContext;
import lombok.Value;

@Value
class TriggerContextImpl implements TriggerContext {

    Workflow workflow;

    Activity activity;
}
