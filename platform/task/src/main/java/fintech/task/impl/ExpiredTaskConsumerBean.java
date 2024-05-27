package fintech.task.impl;


import fintech.task.TaskService;
import fintech.task.command.ExpireTaskCommand;
import fintech.task.db.TaskEntity;
import fintech.task.db.TaskRepository;
import fintech.task.model.TaskStatus;
import fintech.task.spi.ExpiredTaskConsumer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.querydsl.QPageRequest;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

import static fintech.task.db.Entities.task;

@Slf4j
@Component
public class ExpiredTaskConsumerBean implements ExpiredTaskConsumer {

    @Autowired
    private TaskRepository repository;

    @Autowired
    private TaskService taskService;

    @Override
    public int consume(LocalDateTime when) {
        Page<TaskEntity> page = repository.findAll(task.status.eq(TaskStatus.OPEN)
            .and(task.expiresAt.before(when)), new QPageRequest(0, 5_000, task.parentTaskId.asc().nullsLast(), task.expiresAt.asc()));
        List<TaskEntity> tasks = page.getContent();
        if (tasks.isEmpty()) {
            return 0;
        }
        log.info("Found [{}] tasks to expire", tasks.size());
        for (TaskEntity task : tasks) {
            //child task could be completed automatically if parent expires
            if (repository.getRequired(task.getId()).getStatus() != TaskStatus.OPEN) {
                continue;
            }
            taskService.expireTask(new ExpireTaskCommand(task.getId()));
        }
        return tasks.size();
    }
}
