package fintech.task.spi;


import com.google.common.collect.ImmutableList;
import com.google.common.collect.Maps;
import fintech.Validate;
import lombok.Builder;
import lombok.Value;
import org.apache.commons.lang3.tuple.Pair;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static com.google.common.collect.ImmutableList.of;


public class TaskDefinition {

    public static final long POSTPONE_FOREVER = 2400L;
    public static final ImmutableList<Long> DEFAULT_POSTPONE_HOURS = of(1L, 2L, 4L, 8L, 24L, 48L, 120L, POSTPONE_FOREVER);

    private final String type;
    private String group;
    private String description = "";
    private Pair<String, Integer> dependsOnTaskWithOrder;
    private Long priority = 0L;
    private Long priorityAfterPostpone = 0L;

    private final Map<String, TaskResolutionDefinition> resolutionsData = Maps.newHashMap();

    private String defaultExpireResolution;

    public TaskDefinition(String type) {
        this.type = type;
    }

    public static class TaskListenerMeta {
        private final Class<? extends TaskListener> listenerClass;
        private final Object[] args;

        public TaskListenerMeta(Class<? extends TaskListener> listenerClass, Object[] args) {
            this.listenerClass = listenerClass;
            this.args = args;
        }

        public Class<? extends TaskListener> getListenerClass() {
            return listenerClass;
        }

        public Object[] getArgs() {
            return args;
        }
    }

    @Value
    @Builder
    public static class TaskResolutionDefinition {
        List<String> resolutionDetails;
        List<Long> postponeHours;
        boolean commentRequired;
        boolean isPostpone;
        List<TaskListenerMeta> onCompletedListeners;
    }

    public String getType() {
        return type;
    }

    public String getGroup() {
        return group;
    }

    void setGroup(String group) {
        this.group = group;
    }

    public String getDescription() {
        return description;
    }

    void setDescription(String description) {
        this.description = description;
    }

    void setDependsOnTask(String task, int displayOrder) {
        dependsOnTaskWithOrder = Pair.of(task, displayOrder);
    }

    public String getDependedTask() {
        return dependsOnTaskWithOrder == null ? null : dependsOnTaskWithOrder.getLeft();
    }

    public Pair<String, Integer> getDependsOnTaskWithOrder() {
        return dependsOnTaskWithOrder;
    }

    public Long getPriority() {
        return priority;
    }

    public Long getPriorityAfterPostpone() {
        return priorityAfterPostpone;
    }

    void setPriority(Long priority) {
        this.priority = priority;
    }

    void setPriorityAfterPostpone(Long priority) {
        this.priorityAfterPostpone = priority;
    }

    public Set<String> getResolutions() {
        return Collections.unmodifiableSet(resolutionsData.keySet());
    }

    void addResolution(String resolution, TaskResolutionDefinition definition) {
        this.resolutionsData.put(resolution, definition);
    }

    void defaultExpireResolution(String resolution) {
        Validate.isTrue(this.resolutionsData.containsKey(resolution), "Unknown expire resolution [%s]", resolution);
        this.defaultExpireResolution = resolution;
    }

    public String getDefaultExpireResolution() {
        return defaultExpireResolution;
    }

    public TaskResolutionDefinition getResolutionsDefinition(String key) {
        return resolutionsData.get(key);
    }

}
