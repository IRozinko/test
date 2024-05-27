package fintech.workflow.impl;


import fintech.workflow.Activity;
import fintech.workflow.Workflow;
import fintech.workflow.WorkflowService;
import fintech.workflow.spi.ActivityContext;
import fintech.workflow.spi.ActivityDefinition;
import fintech.workflow.spi.WorkflowDefinition;

import java.util.Optional;

public class ActivityContextImpl implements ActivityContext {

    private final WorkflowDefinition workflowDefinition;
    private final Workflow workflow;
    private final Activity activity;
    private final WorkflowService workflowService;

    public ActivityContextImpl(WorkflowDefinition workflowDefinition, Workflow workflow, Activity activity, WorkflowService workflowService) {
        this.workflowDefinition = workflowDefinition;
        this.workflow = workflow;
        this.activity = activity;
        this.workflowService = workflowService;
    }

    @Override
    public Workflow getWorkflow() {
        return workflow;
    }

    @Override
    public Activity getActivity() {
        return activity;
    }

    @Override
    public WorkflowDefinition getWorkflowDefinition() {
        return workflowDefinition;
    }

    @Override
    public ActivityDefinition getActivityDefinition() {
        return workflowDefinition.getActivity(this.activity.getName()).orElseThrow(() -> new IllegalStateException("Activity definition not found: " + activity.getName()));
    }

    @Override
    public Optional<String> getAttribute(String attribute) {
       return workflowService.getAttribute(workflow.getId(), attribute);
    }

    @Override
    public void setAttribute(String key, String value) {
        workflowService.setAttribute(workflow.getId(), key, value);
    }

    @Override
    public void removeAttribute(String key) {
        workflowService.removeAttribute(workflow.getId(), key);
    }

    @Override
    public Long getClientId() {
        return workflow.getClientId();
    }

    @Override
    public void updateLoanId(Long loanId) {
        workflowService.updateLoanId(workflow.getId(), loanId);
    }
}
