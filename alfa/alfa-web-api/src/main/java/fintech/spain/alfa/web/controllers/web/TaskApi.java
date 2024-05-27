package fintech.spain.alfa.web.controllers.web;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import fintech.TimeMachine;
import fintech.spain.alfa.web.config.security.WebApiUser;
import fintech.spain.alfa.product.workflow.undewrtiting.UnderwritingTasks;
import fintech.task.TaskService;
import fintech.task.command.AddTaskCommand;
import fintech.task.command.CancelTaskCommand;
import fintech.task.model.TaskQuery;
import fintech.task.model.TaskStatus;
import fintech.web.api.models.OkResponse;
import fintech.workflow.Activity;
import fintech.workflow.ActivityStatus;
import fintech.workflow.Workflow;
import fintech.workflow.WorkflowService;
import fintech.workflow.WorkflowStatus;
import lombok.Data;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static fintech.spain.alfa.product.workflow.undewrtiting.UnderwritingWorkflows.FIRST_LOAN;
import static fintech.spain.alfa.product.workflow.undewrtiting.UnderwritingWorkflows.FIRST_LOAN_AFFILIATE;
import static fintech.workflow.WorkflowQuery.byClientId;

@Slf4j
@RestController
public class TaskApi {

    private final WorkflowService workflowService;

    private final TaskService taskService;

    @Autowired
    public TaskApi(WorkflowService workflowService, TaskService taskService) {
        this.workflowService = workflowService;
        this.taskService = taskService;
    }

    @GetMapping("/api/web/task")
    public TaskResponse getTasks(@AuthenticationPrincipal WebApiUser user) {
        Optional<Workflow> workflowOptional = workflowService.findWorkflows(byClientId(user.getClientId(), WorkflowStatus.ACTIVE)).stream().findFirst();

        if (!workflowOptional.isPresent()) {
            return new TaskResponse();
        }

        List<Task> tasks = taskService.findTasks(TaskQuery.byWorkflowId(workflowOptional.get().getId())).stream()
            .map(Task::from).collect(Collectors.toList());

        return new TaskResponse().setTasks(tasks);
    }

    @PostMapping("/api/web/task")
    public OkResponse createTask(@AuthenticationPrincipal WebApiUser user, @RequestBody CreateTaskRequest request) {
        Preconditions.checkState(UnderwritingTasks.InstantorHelpCall.TYPE.equals(request.getType()));

        Workflow workflow = workflowService.findWorkflows(byClientId(user.getClientId(), WorkflowStatus.ACTIVE)).stream().findFirst()
            .orElseThrow(() -> new IllegalStateException("Workflow not found"));

        Activity activity;

        activity = workflowService.findActivity(user.getClientId(), FIRST_LOAN, request.getActivity(), ActivityStatus.ACTIVE)
            .orElseGet(
                () -> workflowService.findActivity(user.getClientId(), FIRST_LOAN_AFFILIATE, request.getActivity(), ActivityStatus.ACTIVE)
                    .orElseThrow(() -> new IllegalStateException("Activity with status ACTIVE not found"))
            );


        Optional<fintech.task.model.Task> taskOptional = taskService.findTasks(TaskQuery.byActivityId(activity.getId())).stream()
            .filter(t -> StringUtils.equals(t.getTaskType(), request.getType()))
            .filter(t -> TaskStatus.OPEN.equals(t.getStatus()))
            .findFirst();

        if (taskOptional.isPresent()) {
            log.info("Task {} already exists", request.getType());
            return OkResponse.OK;
        }

        Optional<Long> activityId = workflow.getActivities().stream().filter(a -> a.getName().equals(request.getActivity())).map(Activity::getId).findFirst();
        Preconditions.checkState(activityId.isPresent(), "Activity not found");

        AddTaskCommand command = new AddTaskCommand();
        command.setClientId(workflow.getClientId());
        command.setApplicationId(workflow.getApplicationId());
        command.setLoanId(workflow.getLoanId());
        command.setWorkflowId(workflow.getId());
        command.setType(request.getType());
        command.setDueAt(TimeMachine.now());
        command.setExpiresAt(TimeMachine.now().plusDays(2));
        command.setAttributes(ImmutableMap.copyOf(workflow.getAttributes()));
        command.setActivityId(activityId.get());

        taskService.addTask(command);
        return OkResponse.OK;
    }

    @PostMapping("/api/web/cancel-task")
    public OkResponse cancelTask(@AuthenticationPrincipal WebApiUser user, @RequestBody DeleteTaskRequest request) {
        Preconditions.checkState(UnderwritingTasks.InstantorHelpCall.TYPE.equals(request.getType()));
        Optional<Workflow> workflowOptional = workflowService.findWorkflows(byClientId(user.getClientId(), WorkflowStatus.ACTIVE)).stream().findFirst();

        if (!workflowOptional.isPresent()) {
            return OkResponse.OK;
        }

        Optional<fintech.task.model.Task> taskOptional = taskService.findTasks(TaskQuery.byWorkflowId(workflowOptional.get().getId())).stream()
            .filter(t -> StringUtils.equals(t.getTaskType(), request.getType()))
            .filter(t -> TaskStatus.OPEN.equals(t.getStatus()))
            .findFirst();

        if (!taskOptional.isPresent()) {
            return OkResponse.OK;
        }

        Preconditions.checkState(workflowService.getActivity(taskOptional.get().getActivityId()).getStatus() == ActivityStatus.ACTIVE, "The activity the task belongs to is not active");
        CancelTaskCommand command = new CancelTaskCommand();
        command.setReason("CancelledByAPI");
        command.setTaskId(taskOptional.get().getId());

        taskService.cancelTask(command);

        return OkResponse.OK;
    }

    @Data
    @Accessors(chain = true)
    public static class TaskResponse {

        private List<Task> tasks = Lists.newArrayList();
    }

    @Data
    @Accessors(chain = true)
    public static class Task {

        private String type;

        private TaskStatus status;

        public static Task from(fintech.task.model.Task task) {
            return new Task().setType(task.getTaskType()).setStatus(task.getStatus());
        }
    }

    @Data
    @Accessors(chain = true)
    public static class CreateTaskRequest {

        private String type;
        private String activity;
    }

    @Data
    @Accessors(chain = true)
    public static class DeleteTaskRequest {

        private String type;
    }
}
