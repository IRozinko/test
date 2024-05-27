package fintech.bo.components.task;


import com.google.common.eventbus.Subscribe;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.UI;
import com.vaadin.ui.themes.ValoTheme;
import fintech.bo.api.client.TaskApiClient;
import fintech.bo.api.model.task.TakeNextTaskResponse;
import fintech.bo.components.PollingScheduler;
import fintech.bo.components.background.BackgroundOperations;
import fintech.bo.components.notifications.Notifications;
import fintech.bo.components.security.LoginService;
import retrofit2.Call;

import java.time.LocalDateTime;

import static java.time.temporal.ChronoUnit.SECONDS;

public class TaskQueueComponent extends CustomComponent {

    private static final int REQUEST_INTERVAL = 5;

    private final UI ui;
    private final TaskQueueCache taskQueueCache;
    private final TaskApiClient taskApiClient;
    private PollingScheduler pollingScheduler;
    private final Button takeNext;
    private LocalDateTime lastRefreshed = LocalDateTime.now().minusSeconds(REQUEST_INTERVAL);

    public TaskQueueComponent(UI ui, TaskQueueCache taskQueueCache, TaskApiClient taskApiClient, PollingScheduler pollingScheduler) {
        this.ui = ui;
        this.taskQueueCache = taskQueueCache;
        this.taskApiClient = taskApiClient;
        this.pollingScheduler = pollingScheduler;


        takeNext = new Button("No tasks");
        takeNext.addStyleName(ValoTheme.BUTTON_SMALL);
        takeNext.addStyleName(ValoTheme.BUTTON_DANGER);
        takeNext.setVisible(false);
        takeNext.addClickListener(e -> takeNextTask());


        HorizontalLayout layout = new HorizontalLayout();
        layout.setDefaultComponentAlignment(Alignment.MIDDLE_RIGHT);
        layout.addComponent(takeNext);
        setCompositionRoot(layout);
    }

    private void takeNextTask() {
        Call<TakeNextTaskResponse> call = taskApiClient.takeNext();
        BackgroundOperations.callApi("Taking next task", call, this::openTask, Notifications::errorNotification);
    }

    private void openTask(TakeNextTaskResponse response) {
        if (response.getTaskId() == null) {
            return;
        }
        ui.getNavigator().navigateTo("task/" + response.getTaskId());
    }

    private void request() {
        String agent = LoginService.getLoginData().getUser();
        BackgroundOperations.runSilent(feedback -> taskQueueCache.count(), response -> {
            long tasksDue = -1;
            if (response != null) {
                if (response.containsKey(agent)) {
                    tasksDue = response.get(agent);
                } else {
                    tasksDue = response.get("NOT_ASSIGNED");
                }
            }
            takeNext.setVisible(tasksDue != 0);
            takeNext.setComponentError(null);
            if (tasksDue < 0) {
                takeNext.setCaption("Task count failed");
                takeNext.setDescription("Failed to retrieve task count");
            } else if (tasksDue > 0) {
                takeNext.setCaption(String.format("Take task (%s)", tasksDue));
                takeNext.setDescription(String.format("%d tasks in queue", tasksDue));
            }
        }, error -> {
        });
    }

    private long lastRefreshedInSeconds() {
        return SECONDS.between(lastRefreshed, LocalDateTime.now());
    }

    @Override
    public void attach() {
        super.attach();
        pollingScheduler.subscribe(this);
    }

    @Override
    public void detach() {
        super.detach();
        pollingScheduler.unsubscribe(this);
    }

    @Subscribe
    public void onEvent(String tick) {
        if (lastRefreshedInSeconds() > REQUEST_INTERVAL) {
            lastRefreshed = LocalDateTime.now();
            getUI().access(this::request);
        }
    }
}
