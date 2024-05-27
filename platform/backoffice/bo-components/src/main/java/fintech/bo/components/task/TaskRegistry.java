package fintech.bo.components.task;

import com.vaadin.spring.annotation.SpringComponent;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Supplier;

@SpringComponent
public class TaskRegistry {

    private final Map<String, Supplier<TaskView>> views = new HashMap<>();

    public void registerTask(String taskType, Supplier<TaskView> taskView) {
        views.put(taskType, taskView);
    }

    public Optional<Supplier<TaskView>> getView(String taskType) {
        return Optional.ofNullable(views.get(taskType));
    }
}
