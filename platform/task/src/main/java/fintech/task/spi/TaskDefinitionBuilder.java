package fintech.task.spi;


import fintech.Validate;
import fintech.task.spi.TaskDefinition.TaskResolutionDefinition;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

import static fintech.task.spi.TaskDefinition.DEFAULT_POSTPONE_HOURS;

public class TaskDefinitionBuilder {

    private final TaskDefinition definition;

    public TaskDefinitionBuilder(String type) {
        this.definition = new TaskDefinition(type);
    }

    public TaskDefinitionBuilder group(String group) {
        this.definition.setGroup(group);
        return this;
    }

    public TaskDefinitionBuilder description(String description) {
        this.definition.setDescription(description);
        return this;
    }

    public TaskDefinitionBuilder dependsOnTask(String task) {
        this.definition.setDependsOnTask(task, 0);
        return this;
    }

    public TaskDefinitionBuilder dependsOnTask(String task, int order) {
        this.definition.setDependsOnTask(task, order);
        return this;
    }

    public TaskDefinitionBuilder priority(long priority) {
        this.definition.setPriority(priority);
        return this;
    }

    public TaskDefinitionBuilder priorityAfterPostpone(long priority) {
        this.definition.setPriorityAfterPostpone(priority);
        return this;
    }

    public ResolutionBuilder resolution(String resolution) {
        return new ResolutionBuilder(this, definition, resolution);
    }

    public TaskDefinitionBuilder defaultExpireResolution(String resolution) {
        definition.defaultExpireResolution(resolution);
        return this;
    }

    public static class ResolutionBuilder {
        private final TaskDefinitionBuilder taskBuilder;
        private final TaskDefinition definition;
        private final String resolution;
        private boolean postpone;
        private List<Long> postponeHours;
        private boolean commentRequired;
        private List<String> resolutionDetails = new ArrayList<>();
        private final List<TaskDefinition.TaskListenerMeta> onCompletedListeners = new ArrayList<>();
        private final List<TaskDefinition.TaskListenerMeta> onPostponedListeners = new ArrayList<>();

        public ResolutionBuilder(TaskDefinitionBuilder taskBuilder, TaskDefinition definition, String resolution) {
            this.taskBuilder = taskBuilder;
            this.definition = definition;
            this.resolution = resolution;
        }

        public ResolutionBuilder asPostpone() {
            this.postpone = true;
            return this;
        }

        public ResolutionBuilder withPostponeHours(List<Long> hours) {
            this.postponeHours = hours;
            return this;
        }

        public ResolutionBuilder onCompleted(Class<? extends TaskListener> listenerClass, Object... args) {
            TaskDefinition.TaskListenerMeta listenerMeta = new TaskDefinition.TaskListenerMeta(listenerClass, args);
            validateListener(listenerMeta);
            this.onCompletedListeners.add(listenerMeta);
            return this;
        }

        public ResolutionBuilder withDetails(List<String> resolutionDetails){
            this.resolutionDetails = resolutionDetails;
            return this;
        }

        public ResolutionBuilder onPostponed(Class<? extends TaskListener> listenerClass, Object... args) {
            this.postpone = true;
            TaskDefinition.TaskListenerMeta listenerMeta = new TaskDefinition.TaskListenerMeta(listenerClass, args);
            validateListener(listenerMeta);
            this.onPostponedListeners.add(listenerMeta);
            return this;
        }

        public TaskDefinitionBuilder add() {
            if (this.postpone) {
                Validate.isTrue(this.onCompletedListeners.isEmpty(), "Postpone resolutions should not have completed listeners, but [%s] found!", this.onCompletedListeners.size());
            } else {
                Validate.isTrue(this.onPostponedListeners.isEmpty(), "Complete resolutions should not have postponed listeners, but [%s] found!", this.onPostponedListeners.size());
            }

            this.definition.addResolution(resolution, new TaskResolutionDefinition.TaskResolutionDefinitionBuilder()
                .commentRequired(commentRequired)
                .isPostpone(this.postpone)
                .onCompletedListeners(this.postpone ? this.onPostponedListeners : this.onCompletedListeners)
                .resolutionDetails(resolutionDetails)
                .postponeHours(postponeHours == null ? DEFAULT_POSTPONE_HOURS : postponeHours)
                .build()
            );
            return taskBuilder;
        }

        private void validateListener(TaskDefinition.TaskListenerMeta onCompletedListener) {
            Class<? extends TaskListener> listenerClass = onCompletedListener.getListenerClass();
            Validate.notNull(AnnotationUtils.findAnnotation(listenerClass, Component.class), "Task listener class must be Spring @Component");
            Scope scope = AnnotationUtils.findAnnotation(listenerClass, Scope.class);
            Validate.isTrue(scope != null && BeanDefinition.SCOPE_PROTOTYPE.equals(scope.value()), "Task listener class must have Spring prototype scope");
        }
    }

    public TaskDefinition build() {
        Validate.notEmpty(definition.getResolutions(), "Task has no resolutions: [%s]", this.definition.getType());
        Validate.notNull(definition.getDefaultExpireResolution(), "Task has no default expire resolution: [%s]", this.definition.getType());
        return this.definition;
    }
}
