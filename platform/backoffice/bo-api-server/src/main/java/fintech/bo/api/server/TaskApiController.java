package fintech.bo.api.server;

import com.google.common.collect.Maps;
import fintech.TimeMachine;
import fintech.Validate;
import fintech.bo.api.model.StringRequest;
import fintech.bo.api.model.permissions.BackofficePermissions;
import fintech.bo.api.model.task.AssignTaskRequest;
import fintech.bo.api.model.task.CompleteTaskRequest;
import fintech.bo.api.model.task.TakeNextTaskResponse;
import fintech.bo.api.model.task.TaskCountResponse;
import fintech.bo.api.model.task.TaskDefinitionResponse;
import fintech.bo.api.model.task.TaskDefinitionResponse.TaskResolutionData;
import fintech.bo.api.model.task.TaskTypesResponse;
import fintech.bo.api.server.security.BackofficeUser;
import fintech.calendar.spi.BusinessCalendarService;
import fintech.task.TaskQueueService;
import fintech.task.TaskService;
import fintech.task.command.AssignTaskCommand;
import fintech.task.command.CompleteTaskCommand;
import fintech.task.command.PostponeTaskCommand;
import fintech.task.model.Task;
import fintech.task.spi.TaskDefinition;
import fintech.task.spi.TaskDefinition.TaskResolutionDefinition;
import fintech.task.spi.TaskRegistry;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@RestController
public class TaskApiController {

    private final TaskQueueService taskQueue;
    private final TaskRegistry taskRegistry;
    private final TaskService taskService;
    private final BusinessCalendarService businessCalendarService;

    @Secured({BackofficePermissions.ADMIN, BackofficePermissions.TASK_PROCESS})
    @PostMapping("/api/bo/tasks/count")
    TaskCountResponse count(@AuthenticationPrincipal BackofficeUser user) {
        return new TaskCountResponse(taskQueue.count(user.getUsername(), TimeMachine.now()).getTasksDue());
    }

    @Secured({BackofficePermissions.ADMIN, BackofficePermissions.TASK_PROCESS})
    @PostMapping("/api/bo/tasks/take-next")
    TakeNextTaskResponse takeNext(@AuthenticationPrincipal BackofficeUser user) {
        Optional<Task> task = taskQueue.assignNextTask(user.getUsername(), TimeMachine.now());
        TakeNextTaskResponse response = new TakeNextTaskResponse();
        task.ifPresent(t -> response.setTaskId(t.getId()));
        return response;
    }

    @GetMapping("/api/bo/tasks/task-types")
    TaskTypesResponse taskTypes() {
        List<String> types = taskRegistry.getAll().stream().map(TaskDefinition::getType).collect(Collectors.toList());
        TaskTypesResponse response = new TaskTypesResponse();
        response.setTaskTypes(types);
        return response;
    }

    @PostMapping("/api/bo/tasks/task-definition")
    TaskDefinitionResponse taskDefinition(@RequestBody @Valid StringRequest task) {
        TaskDefinitionResponse response = new TaskDefinitionResponse();
        response.setSingle(true);
        TaskDefinition taskDefinition = taskRegistry.getDefinition(task.getString());

        Map<String, Integer> possibleSubTasksWithOrder = Maps.newHashMap();
        taskRegistry.getAll().forEach(d -> {
            Pair<String, Integer> dependedTaskWithOrder = d.getDependsOnTaskWithOrder();
            if (dependedTaskWithOrder != null && task.getString().equals(dependedTaskWithOrder.getLeft())) {
                response.setSingle(false);
                possibleSubTasksWithOrder.put(d.getType(), dependedTaskWithOrder.getValue());
            }
        });

        response.setPossibleSubTasks(possibleSubTasksWithOrder.entrySet()
            .stream()
            .sorted(Comparator.comparingInt(Map.Entry::getValue))
            .map(Map.Entry::getKey)
            .collect(Collectors.toList())
        );
        response.setResolutions(
            taskDefinition.getResolutions().stream()
                .filter(r -> !taskDefinition.getDefaultExpireResolution().equalsIgnoreCase(r))
                .map(r ->
                    {
                        TaskResolutionData trd = new TaskResolutionData();
                        TaskResolutionDefinition d = taskDefinition.getResolutionsDefinition(r);
                        trd.setCommentRequired(d.isCommentRequired());
                        trd.setPostpone(d.isPostpone());
                        trd.setPostponeHours(d.getPostponeHours());
                        trd.setResolution(r);
                        trd.setResolutionDetails(d.getResolutionDetails());
                        return trd;
                    }
                )
                .collect(Collectors.toList())
        );
        response.setDescription(taskDefinition.getDescription());
        response.setGroup(taskDefinition.getGroup());
        return response;
    }

    @Secured({BackofficePermissions.ADMIN, BackofficePermissions.TASK_PROCESS})
    @PostMapping("/api/bo/tasks/assign")
    void complete(@RequestBody @Valid AssignTaskRequest request) {
        AssignTaskCommand command = new AssignTaskCommand();
        command.setAgent(request.getAgent());
        command.setTaskId(request.getTaskId());
        command.setWhen(TimeMachine.now());
        command.setComment(request.getComment());
        taskService.assignTask(command);
    }

    @Secured({BackofficePermissions.ADMIN, BackofficePermissions.TASK_PROCESS})
    @PostMapping("/api/bo/tasks/complete")
    TaskCountResponse complete(@AuthenticationPrincipal BackofficeUser user, @RequestBody @Valid CompleteTaskRequest request) {
        Task task = taskService.get(request.getTaskId());
        Validate.notBlank(task.getAgent(), "Task not assigned");
        Validate.isTrue(StringUtils.equalsIgnoreCase(user.getUsername(), task.getAgent()), "Task assigned to another agent: [%s]", task.getAgent());
        if (!request.isPostpone()) {
            CompleteTaskCommand command = new CompleteTaskCommand();
            command.setTaskId(request.getTaskId());
            command.setResolution(request.getResolution());
            command.setResolutionDetail(request.getResolutionDetail());
            command.setResolutionSubDetail(request.getResolutionSubDetail());
            command.setComment(request.getComment());
            taskService.completeTask(command);
        } else {
            Validate.notNull(request.getPostponeByHours(), "Invalid postpone value");
            LocalDateTime postponeTo = businessCalendarService.resolveWorkingHours(request.getPostponeByHours().intValue(), ChronoUnit.HOURS);
            PostponeTaskCommand command = new PostponeTaskCommand();
            command.setTaskId(request.getTaskId());
            command.setPostponeTo(postponeTo);
            command.setResolution(request.getResolution());
            command.setResolutionDetail(request.getResolutionDetail());
            command.setResolutionSubDetail(request.getResolutionSubDetail());
            command.setComment(request.getComment());
            taskService.postponeTask(command);
        }
        return new TaskCountResponse(taskQueue.count(user.getUsername(), TimeMachine.now()).getTasksDue());
    }

}
