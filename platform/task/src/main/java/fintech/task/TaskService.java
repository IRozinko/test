package fintech.task;

import fintech.task.command.AddTaskAttributesCommand;
import fintech.task.command.AddTaskCommand;
import fintech.task.command.AssignTaskCommand;
import fintech.task.command.CancelTaskCommand;
import fintech.task.command.CompleteTaskCommand;
import fintech.task.command.ExpireTaskCommand;
import fintech.task.command.OpenTaskCommand;
import fintech.task.command.PostponeTaskCommand;
import fintech.task.command.ReopenTaskCommand;
import fintech.task.model.Task;
import fintech.task.model.TaskQuery;

import java.util.List;

public interface TaskService {

    Task get(Long id);

    Long addTask(AddTaskCommand command);

    void assignTask(AssignTaskCommand command);

    List<Task> findTasks(TaskQuery query);

    void postponeTask(PostponeTaskCommand command);

    void completeTask(CompleteTaskCommand command);

    void cancelTask(CancelTaskCommand command);

    void reopenTask(ReopenTaskCommand command);

    void expireTask(ExpireTaskCommand command);

    void addTaskAttributes(AddTaskAttributesCommand command);
}
