package fintech.workflow;

import com.google.common.collect.ImmutableList;

import java.util.ArrayList;
import java.util.List;

public class WorkflowQuery {

    private Long clientId;
    private Long loanId;
    private Long applicationId;
    private List<String> workflowNames = new ArrayList<>();
    private WorkflowStatus[] statuses = new WorkflowStatus[0];

    public Long getClientId() {
        return clientId;
    }

    public Long getLoanId() {
        return loanId;
    }

    public WorkflowStatus[] getStatuses() {
        return statuses;
    }

    public List<String> getWorkflowNames() {
        return workflowNames;
    }

    public Long getApplicationId() {
        return applicationId;
    }

    public static WorkflowQuery byLoanId(long loanId, String workflowName, WorkflowStatus... statuses) {
        WorkflowQuery query = new WorkflowQuery();
        query.workflowNames = ImmutableList.of(workflowName);
        query.loanId = loanId;
        query.statuses = statuses;
        return query;
    }

    public static WorkflowQuery byLoanId(long loanId, WorkflowStatus... statuses) {
        WorkflowQuery query = new WorkflowQuery();
        query.loanId = loanId;
        query.statuses = statuses;
        return query;
    }

    public static WorkflowQuery byClientId(long clientId, WorkflowStatus... statuses) {
        WorkflowQuery query = new WorkflowQuery();
        query.clientId = clientId;
        query.statuses = statuses;
        return query;
    }

    public static WorkflowQuery byClientId(long clientId, String workflowName, WorkflowStatus... statuses) {
        WorkflowQuery query = new WorkflowQuery();
        query.workflowNames = ImmutableList.of(workflowName);
        query.clientId = clientId;
        query.statuses = statuses;
        return query;
    }

    public static WorkflowQuery byClientId(Long clientId, List<String> workflowNames, WorkflowStatus... statuses) {
        WorkflowQuery query = new WorkflowQuery();
        query.clientId = clientId;
        query.workflowNames = workflowNames;
        query.statuses = statuses;
        return query;
    }

    public static WorkflowQuery byWorkflowName(String workflowName, WorkflowStatus... statuses) {
        WorkflowQuery query = new WorkflowQuery();
        query.workflowNames = ImmutableList.of(workflowName);
        query.statuses = statuses;
        return query;
    }

    public static WorkflowQuery byWorkflowNames(List<String> workflowNames, WorkflowStatus... statuses) {
        WorkflowQuery query = new WorkflowQuery();
        query.workflowNames = workflowNames;
        query.statuses = statuses;
        return query;
    }


    public static WorkflowQuery byLoanApplicationId(long applicationId, WorkflowStatus... statuses) {
        WorkflowQuery query = new WorkflowQuery();
        query.applicationId = applicationId;
        query.statuses = statuses;
        return query;
    }

    public static WorkflowQuery byLoanApplicationId(long applicationId, String workflowName, WorkflowStatus... statuses) {
        WorkflowQuery query = new WorkflowQuery();
        query.workflowNames = ImmutableList.of(workflowName);
        query.applicationId = applicationId;
        query.statuses = statuses;
        return query;
    }
}
