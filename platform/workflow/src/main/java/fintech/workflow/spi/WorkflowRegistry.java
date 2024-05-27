package fintech.workflow.spi;


import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;

public interface WorkflowRegistry {

    void addDefinition(Supplier<WorkflowDefinition> definition, int cacheInSeconds);

    WorkflowDefinition getDefinition(String workflowName);

    WorkflowDefinition getDefinition(String workflowName, int workflowVersion);

    Optional<ActivityDefinition> getActivityDefinition(String workflowName, int workflowVersion, String activityName);

    List<WorkflowDefinition> getDefinitions();

    void clear();
}
