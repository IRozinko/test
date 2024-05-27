package fintech.spain.alfa.product.testing;

import fintech.Validate;
import fintech.lending.core.application.LoanApplicationService;
import fintech.task.TaskService;
import fintech.task.model.Task;
import fintech.task.model.TaskQuery;
import fintech.workflow.Activity;
import fintech.workflow.ActivityStatus;
import fintech.workflow.Workflow;
import fintech.workflow.WorkflowService;
import fintech.workflow.WorkflowStatus;
import fintech.workflow.spi.ActivityDefinition;
import fintech.workflow.spi.WorkflowRegistry;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.Accessors;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Accessors(chain = true)
public abstract class TestWorkflow<T extends TestWorkflow> {

    protected final TestClient client;

    @Getter
    protected final Long workflowId;

    @Autowired
    protected WorkflowService workflowService;

    @Autowired
    protected LoanApplicationService applicationService;

    @Autowired
    protected TaskService taskService;

    @Autowired
    protected WorkflowRegistry workflowRegistry;

    public TestWorkflow(TestClient client, Long workflowId) {
        this.client = client;
        this.workflowId = workflowId;
    }

    public Workflow getWorkflow() {
        return workflowService.getWorkflow(this.workflowId);
    }

    public boolean isActive() {
        return getWorkflow().getStatus() == WorkflowStatus.ACTIVE;
    }

    public boolean isCompleted() {
        return getWorkflow().getStatus() == WorkflowStatus.COMPLETED;
    }

    public boolean isTerminated() {
        return getWorkflow().getStatus() == WorkflowStatus.TERMINATED;
    }

    public boolean isExpired() {
        return getWorkflow().getStatus() == WorkflowStatus.EXPIRED;
    }

    public T runBeforeActivity(String stopBeforeActivity) {
        return run(stopBeforeActivity, null);
    }

    public T runAfterActivity(String stopAfterActivity) {
        return run(null, stopAfterActivity);
    }

    public T runAll() {
        return run(null, null);
    }

    public T run(String stopBeforeActivity, String stopAftertActivity) {
        List<ActivityRunnable> runnables = new ArrayList<>();
        buildRunnables(runnables);

        for (ActivityRunnable runnable : runnables) {
            if (!runnable.run(stopBeforeActivity, stopAftertActivity)) {
                return (T) this;
            }
        }
        return (T) this;
    }

    public T runSystemActivity(String activity) {
        Workflow workflow = workflowService.getWorkflow(workflowId);
        workflowService.runSystemActivity(workflow.activity(activity).getId());
        return (T) this;
    }

    public T failSystemActivity(String activityName) {
        Workflow workflow = workflowService.getWorkflow(workflowId);
        Activity activity = workflow.activity(activityName);
        ActivityDefinition activityDefinition = workflowRegistry.getActivityDefinition(workflow.getName(), workflow.getVersion(), activity.getName()).get();
        for (int i = 0; i < activityDefinition.getMaxAttempts() - activity.getAttempts(); i++) {
            workflowService.failActivity(activity.getId(), "Test");
        }
        return (T) this;
    }

    public T terminate(String reason) {
        workflowService.terminateWorkflow(workflowId, reason);
        return (T) this;
    }

    public boolean areOnlyActiveActivities(String... activities) { //fixme: shit name!
        List<String> activeActivities = getWorkflow().getActivities().stream()
            .filter(Activity::isActive)
            .map(Activity::getName)
            .collect(Collectors.toList());

        if (activeActivities.size() != activities.length) {
            return false;
        }
        return activeActivities.containsAll(Arrays.asList(activities));
    }

    public boolean isActivityActive(String activity) {
        return getActivity(activity).isActive();
    }

    public boolean isActivityCompleted(String activity) {
        return getActivity(activity).getStatus() == ActivityStatus.COMPLETED;
    }

    public boolean areAllActivitiesCompleted(String... activities) {
        return areAllActivitiesWithStatus(ActivityStatus.COMPLETED);
    }

    public boolean areAllActivitiesCancelled(String... activities) {
        return areAllActivitiesWithStatus(ActivityStatus.CANCELLED);
    }

    public boolean areAllActivitiesWithStatus(ActivityStatus status, String... activities) {
        return Stream.of(activities)
            .map(this::getActivity)
            .map(Activity::getStatus)
            .allMatch(status::equals);
    }

    public ActivityStatus getActivityStatus(String activity) {
        return getActivity(activity).getStatus();
    }

    public String getActivityResolution(String activity) {
        return getActivity(activity).getResolution();
    }

    public String getActivityResolutionDetail(String activity) {
        return getActivity(activity).getResolutionDetail();
    }

    public Activity getActivity(String activity) {
        return getWorkflow().activity(activity);
    }

    public TestApplication toApplication() {
        return TestFactory.application(this.client, getWorkflow().getApplicationId());
    }

    public Long getApplicationId() {
        return getWorkflow().getApplicationId();
    }

    public TestLoan toLoan() {
        return TestFactory.loan(this.client, getWorkflow().getLoanId());
    }

    public TestClient toClient() {
        return this.client;
    }

    public T startActivity(String activity) {
        workflowService.startActivity(getActivity(activity).getId());
        return (T) this;
    }

    public T completeActivity(String activity, String resolution) {
        workflowService.completeActivity(getActivity(activity).getId(), resolution, "test");
        return (T) this;
    }

    public Optional<String> getAttribute(String attribute) {
        return Optional.ofNullable(workflowService.getWorkflow(workflowId).getAttributes().get(attribute));
    }

    public void setAttribute(String attribute, String value) {
        workflowService.setAttribute(workflowId, attribute, value);
    }

    protected abstract void buildRunnables(List<ActivityRunnable> runnables);

    @AllArgsConstructor
    protected class ActivityRunnable {
        private final String activity;
        private final Runnable runnable;

        public boolean run(String stopBefore, String stopAfter) {
            if (activity.equals(stopBefore)) return false;
            if (isActivityActive(activity)) {
                runnable.run();
            }
            return !activity.equals(stopAfter);
        }
    }

    protected ActivityRunnable systemActivityRunnable(String activity) {
        return new ActivityRunnable(activity, () -> runSystemActivity(activity));
    }

    public TestTask taskOfActivity(String activityName) {
        List<Task> tasks = taskService.findTasks(TaskQuery.byActivityId(getActivity(activityName).getId()));
        Validate.isTrue(tasks.size() == 1, "Did not find exactly one task");
        return TestFactory.task(tasks.get(0).getId());
    }

    public Optional<TestTask> taskOfActivity(String activityName, String taskType) {
        List<Task> tasks = taskService.findTasks(TaskQuery.byActivityId(getActivity(activityName).getId()));
        return tasks.stream().filter(t -> taskType.equals(t.getTaskType()))
            .findFirst()
            .map(task -> TestFactory.task(task.getId()));
    }

    public boolean hasTask(String activityName, String taskType) {
        List<Task> tasks = taskService.findTasks(TaskQuery.byActivityId(getActivity(activityName).getId()));
        return tasks.stream().anyMatch(t -> taskType.equals(t.getTaskType()));
    }


    public <T> T print() {
        Workflow wf = getWorkflow();
        System.out.println("-------------------------------------");
        System.out.println(wf.getName() + ": " + wf.getStatus());
        wf.getActivities().forEach(a -> System.out.println(("\t" + a.getStatus() + ":\t" + a.getName()) + (a.getResolution() == null ? "" : " (" + a.getResolution() + ")")));
        System.out.println("-------------------------------------");
        return (T) this;
    }

    public T removeAttribute(String name) {
        workflowService.removeAttribute(workflowId, name);
        return (T)this;
    }

}
