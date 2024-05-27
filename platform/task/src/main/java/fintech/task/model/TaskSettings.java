package fintech.task.model;

import com.google.common.collect.Lists;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.time.Duration;
import java.util.List;
import java.util.Optional;

@Data
@Accessors(chain = true)
public class TaskSettings {

    private List<TaskConfig> taskConfigs = Lists.newArrayList();

    public TaskConfig getConfigOrDefault(String taskType) {
        return getConfig(taskType).orElse(TaskConfig.DEFAULT);
    }

    public Optional<TaskConfig> getConfig(String taskType) {
        return taskConfigs.stream()
            .filter(config -> config.getTaskType().equals(taskType))
            .findFirst();
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Accessors(chain = true)
    public static class TaskConfig {

        private static final TaskConfig DEFAULT = new TaskConfig()
            .setEnabled(true)
            .setInitialDelayInMinutes(0)
            .setPriority(20)
            .setPriorityAfterPostpone(5);

        private String taskType;

        private boolean enabled = true;

        private int initialDelayInMinutes;

        private int priority;

        private int priorityAfterPostpone;

        private Long expiresInHours;

        public TaskConfig setInitialDelayInDays(int days) {
            this.initialDelayInMinutes = (int) Duration.ofDays(days).toMinutes();
            return this;
        }
    }

}
