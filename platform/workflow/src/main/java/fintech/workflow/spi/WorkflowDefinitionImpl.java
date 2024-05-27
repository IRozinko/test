package fintech.workflow.spi;

import com.google.common.collect.ImmutableList;
import fintech.Validate;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

class WorkflowDefinitionImpl implements WorkflowDefinition {

    private static final int DEFAULT_VERSION = 0;

    private final String workflowName;
    private final Integer workflowVersion;
    private final List<ActivityDefinition> activities = new ArrayList<>();
    private final List<BeanMetadata<WorkflowListener>> onCompletedListeners = new ArrayList<>();
    private final List<BeanMetadata<WorkflowListener>> onTerminatedListeners = new ArrayList<>();
    private final List<BeanMetadata<WorkflowListener>> onExpiredListeners = new ArrayList<>();

    public WorkflowDefinitionImpl(String workflowName) {
        this(workflowName, DEFAULT_VERSION);
    }

    public WorkflowDefinitionImpl(String workflowName, Integer workflowVersion) {
        this.workflowName = Validate.notBlank(workflowName);
        this.workflowVersion = workflowVersion;
    }

    public String getWorkflowName() {
        return workflowName;
    }

    @Override
    public Integer getWorkflowVersion() {
        return workflowVersion;
    }

    @Override
    public List<ActivityDefinition> getActivities() {
        return ImmutableList.copyOf(activities);
    }

    @Override
    public Optional<ActivityDefinition> getActivity(String name) {
        return activities.stream()
            .filter(a -> a.getActivityName().equals(name))
            .findFirst();
    }

    /**
     * @param activityFrom inclusive
     * @param activityTo exclusive
     */
    @Override
    public List<ActivityDefinition> getActivities(String activityFrom, String activityTo) {
        Validate.isTrue(StringUtils.isNotBlank(activityFrom), "`activityFrom` can't be null");
        Validate.isTrue(StringUtils.isNotBlank(activityTo), "`activityTo` can't be null");

        List<String> activityNames = activities.stream().map(ActivityDefinition::getActivityName)
            .collect(Collectors.toList());
        int indexFrom = activityNames.indexOf(activityFrom);
        int indexTo = activityNames.indexOf(activityTo);

        Validate.isTrue(indexFrom < indexTo, "`activityTo` can't be before `activityFrom`");

        return ImmutableList.copyOf(activities.subList(indexFrom, indexTo));
    }

    @Override
    public List<BeanMetadata<WorkflowListener>> getOnCompletedListeners() {
        return ImmutableList.copyOf(onCompletedListeners);
    }

    @Override
    public List<BeanMetadata<WorkflowListener>> getOnTerminatedListeners() {
        return ImmutableList.copyOf(onTerminatedListeners);
    }

    @Override
    public List<BeanMetadata<WorkflowListener>> getOnExpiredListeners() {
        return ImmutableList.copyOf(onExpiredListeners);
    }

    void addActivity(ActivityDefinitionImpl activity) {
        this.activities.add(activity);
    }

    void addOnCompletedListener(Class<? extends WorkflowListener> listenerClass, Object... args) {
        this.onCompletedListeners.add(new BeanMetadata<>(listenerClass, args));
    }

    void addOnTerminatedListener(Class<? extends WorkflowListener> listenerClass, Object... args) {
        this.onTerminatedListeners.add(new BeanMetadata<>(listenerClass, args));
    }

    void addOnExpiredListener(Class<? extends WorkflowListener> listenerClass, Object... args) {
        this.onExpiredListeners.add(new BeanMetadata<>(listenerClass, args));
    }
}
