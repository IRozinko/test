package fintech.workflow.spi;

import fintech.workflow.Activity;
import fintech.workflow.Workflow;

import java.util.Optional;

public interface ActivityContext {

    Long getClientId();

    Workflow getWorkflow();

    Activity getActivity();

    WorkflowDefinition getWorkflowDefinition();

    ActivityDefinition getActivityDefinition();

    Optional<String> getAttribute(String attribute);

    void setAttribute(String key, String value);

    void removeAttribute(String key);

    void updateLoanId(Long loanId);
}
