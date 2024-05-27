package fintech.task;

import fintech.task.model.Task;
import fintech.task.model.TaskCount;

import java.time.LocalDateTime;
import java.util.Optional;

public interface TaskQueueService {

    Optional<Task> assignNextTask(String agentEmail, LocalDateTime when);

    TaskCount count(String agentEmail, LocalDateTime when);
}
