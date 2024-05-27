package fintech.workflow;

import java.util.List;
import java.util.Optional;

public interface WorkflowService {

    Long startWorkflow(StartWorkflowCommand command);

    Workflow getWorkflow(Long workflowId);

    Workflow getRootWorkflow(Long workflowId);

    Activity getActivity(Long activityId);

    List<Workflow> findWorkflows(WorkflowQuery query);

    Optional<Activity> findActivity(Long clientId, String workflow, String activityName, ActivityStatus... statuses);

    List<Activity> findActivities(Long clientId, String workflow, String activityName, ActivityStatus... statuses);

    void terminateWorkflow(Long workflowId, String reason);

    void startActivity(Long activityId);

    void failActivity(Long activityId, String error);

    void completeActivity(Long activityId, String resolution, String resolutionDetail);

    Optional<String> getAttribute(Long workflowId, String key);

    void setAttribute(Long workflowId, String key, String value);

    void removeAttribute(Long workflowId, String key);

    void expireWorkflow(Long workflowId, String reason);

    void runSystemActivity(Long activityId);

    void runSystemActivity(Long workflowId, String activity);

    void runBeforeActivity(Long workflowId, String activity);

    void trigger(Long workflowId, Object event);

    void updateLoanId(Long workflowId, Long loanId);

    void resetInitialDelay(Long activityId);

    void suspend(Long workflowId);

    void resume(Long workflowId);
}
