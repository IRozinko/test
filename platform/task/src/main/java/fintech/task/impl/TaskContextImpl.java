package fintech.task.impl;


import fintech.task.model.Task;
import fintech.task.spi.TaskContext;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class TaskContextImpl implements TaskContext {

    private final Task task;
}
