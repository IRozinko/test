package fintech.workflow.spi;

import java.util.List;
import java.util.Optional;

public interface WorkflowDefinition {

    String getWorkflowName();

    Integer getWorkflowVersion();

    List<ActivityDefinition> getActivities();

    Optional<ActivityDefinition> getActivity(String name);

    List<ActivityDefinition> getActivities(String activityFrom, String activityTo);

    List<BeanMetadata<WorkflowListener>> getOnCompletedListeners();

    List<BeanMetadata<WorkflowListener>> getOnTerminatedListeners();

    List<BeanMetadata<WorkflowListener>> getOnExpiredListeners();
}
