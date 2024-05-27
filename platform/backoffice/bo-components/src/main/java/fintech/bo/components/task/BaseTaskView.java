package fintech.bo.components.task;

import com.google.common.collect.Maps;
import com.google.common.eventbus.Subscribe;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.shared.ui.MarginInfo;
import com.vaadin.spring.annotation.SpringView;
import com.vaadin.ui.*;
import com.vaadin.ui.themes.ValoTheme;
import fintech.bo.api.client.TaskApiClient;
import fintech.bo.api.model.StringRequest;
import fintech.bo.api.model.task.AssignTaskRequest;
import fintech.bo.api.model.task.TaskDefinitionResponse;
import fintech.bo.components.AbstractBackofficeUI;
import fintech.bo.components.BackofficeTheme;
import fintech.bo.components.PollingScheduler;
import fintech.bo.components.PropertyLayout;
import fintech.bo.components.Refreshable;
import fintech.bo.components.activity.ActivityComponents;
import fintech.bo.components.activity.AddActivityComponent;
import fintech.bo.components.api.TaskHelper;
import fintech.bo.components.application.LoanApplicationQueries;
import fintech.bo.components.application.info.ApplicationInfo;
import fintech.bo.components.background.BackgroundOperations;
import fintech.bo.components.client.ClientComponents;
import fintech.bo.components.client.dto.ClientDTO;
import fintech.bo.components.client.repository.ClientRepository;
import fintech.bo.components.layouts.BusinessObjectLayout;
import fintech.bo.components.loan.LoanComponents;
import fintech.bo.components.loan.LoanQueries;
import fintech.bo.components.notifications.Notifications;
import fintech.bo.components.security.LoginService;
import fintech.bo.components.tabs.LazyTabSheet;
import fintech.bo.components.utils.UrlUtils;
import fintech.bo.db.jooq.lending.tables.records.LoanApplicationRecord;
import fintech.bo.db.jooq.lending.tables.records.LoanRecord;
import fintech.bo.db.jooq.task.tables.records.LogRecord;
import fintech.bo.db.jooq.task.tables.records.TaskRecord;
import fintech.retrofit.RetrofitHelper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import retrofit2.Call;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import static java.time.temporal.ChronoUnit.SECONDS;

@Slf4j
@SpringView(name = BaseTaskView.NAME)
public class BaseTaskView extends VerticalLayout implements View, Refreshable {

    public static final String NAME = "task";

    @Autowired
    private TaskQueries taskQueries;

    @Autowired
    private TaskHelper taskHelper;

    @Autowired
    private TaskRegistry taskRegistry;

    @Autowired
    private TaskApiClient taskApiClient;

    @Autowired
    private TaskComponents taskComponents;

    @Autowired
    private ClientComponents clientComponents;

    @Autowired
    private LoanQueries loanQueries;

    @Autowired
    private LoanComponents loanComponents;

    @Autowired
    private LoanApplicationQueries loanApplicationQueries;

    @Autowired
    private ActivityComponents activityComponents;

    @Autowired
    private ClientRepository clientRepository;

    @Autowired
    private PollingScheduler pollingScheduler;

    private long taskId;
    private BusinessObjectLayout layout;
    boolean taskIsOpen;
    boolean taskAssignedToMe;
    private TaskRecord task;
    private TaskDefinitionResponse definition;

    private volatile LocalDateTime lastRefreshed = LocalDateTime.now().minusSeconds(REQUEST_INTERVAL_SEC);

    private static final int REQUEST_INTERVAL_SEC = 5;

    private final Map<String, TaskMetadata> subTasks = Maps.newHashMap();

    @Override
    public void enter(ViewChangeListener.ViewChangeEvent event) {
        taskId = Long.parseLong(UrlUtils.getParam(event.getParameters(), UrlUtils.ID));
        setMargin(false);
        refresh();
    }

    @Override
    public void refresh() {
        removeAllComponents();
        task = taskQueries.findById(taskId);
        if (task == null) {
            Notifications.errorNotification("Task not found");
            return;
        }
        setCaption(String.format("%s %s", task.getTaskType(), task.getId()));

        taskIsOpen = TaskConstants.STATUS_OPEN.equals(task.getStatus());
        taskAssignedToMe = StringUtils.equalsIgnoreCase(task.getAgent(), LoginService.getLoginData().getUser());
        layout = new BusinessObjectLayout();

        layout.setTitle(String.format("%s - %s", task.getTaskType(), task.getStatus()));
        layout.setRefreshAction(this::refresh);

        if (taskIsOpen) {
            layout.addActionMenuItem("Assign to me", e -> assignToMe());
            layout.addActionMenuItem("Reassign", e -> reassign());
            layout.addLeftComponent(new VerticalLayout(taskAssignedToMe ? taskAssignedToMeLabel() : taskNotAssignedLabel(task)));
        } else if (TaskConstants.STATUS_COMPLETED.equals(task.getStatus())) {
            layout.addLeftComponent(new VerticalLayout(taskCompletedLabel(task)));
        }
        if (!StringUtils.isBlank(task.getComment())) {
            layout.addLeftComponent(commentLabel(task));
        }
        subTasks.clear();
        definition = RetrofitHelper.syncCall(taskApiClient.taskDefinition(new StringRequest(task.getTaskType()))).get();
        if (definition.isSingle()) {
            Optional<Supplier<TaskView>> supplier = taskRegistry.getView(task.getTaskType());
            if (!supplier.isPresent()) {
                Notifications.errorNotification(String.format("View not found for task type %s", task.getTaskType()));
                return;
            }
            populateLayoutAsSingleTask(supplier.get().get());
        } else {
            populateLayoutAsMultitask();
        }

        addComponentsAndExpand(layout);
    }

    private Component clientTab(TaskRecord task) {
        VerticalLayout layout = new VerticalLayout();
        layout.setMargin(false);

        ClientDTO client = clientRepository.getRequired(task.getClientId());
        layout.addComponent(new Panel(clientComponents.clientInfo(client)));
        return layout;
    }

    private Component activitiesTab(TaskRecord task) {
        Panel activities = new Panel("Latest activities");
        activities.addStyleName(ValoTheme.PANEL_BORDERLESS);
        activities.setSizeFull();

        Runnable updateActivitiesList = () -> activities.setContent(activityComponents.latestActivities(task.getClientId()));

        Panel panel = new Panel("Add activity");
        panel.addStyleName(ValoTheme.PANEL_BORDERLESS);
        panel.setSizeFull();
        AddActivityComponent activityComponent = activityComponents.addActivityComponent(task.getClientId());
        activityComponent.setOnSavedCallback(updateActivitiesList);
        panel.setContent(activityComponent);

        updateActivitiesList.run();
        return new VerticalLayout(panel, activities);
    }

    private Component loanApplicationTab(TaskRecord task) {
        LoanApplicationRecord application = loanApplicationQueries.findById(task.getApplicationId());
        return new Panel(ApplicationInfo.fromApplication(application));
    }

    private Component loanTab(TaskRecord task) {
        VerticalLayout layout = new VerticalLayout();
        layout.setMargin(false);
        LoanRecord loan = loanQueries.findById(task.getLoanId());
        if (loan != null) {
            layout.addComponent(loanComponents.loanInfo(loan));
        }
        return new Panel(layout);
    }

    private Component taskTab(TaskRecord task) {
        VerticalLayout layout = new VerticalLayout();
        layout.setMargin(false);
        layout.setSpacing(true);
        PropertyLayout info = taskComponents.taskInfo(task);
        layout.addComponent(new Panel(info));

        TaskLogDataProvider provider = taskComponents.taskLogDataProvider();
        provider.setTaskId(task.getId());
        Grid<LogRecord> grid = taskComponents.taskLogGrid(provider);
        grid.setCaption("Task log");
        layout.addComponentsAndExpand(grid);
        return layout;
    }

    private Label taskAssignedToMeLabel() {
        Label label = new Label("Task is assigned to you");
        label.addStyleName(BackofficeTheme.TEXT_SUCCESS);
        label.addStyleName(ValoTheme.LABEL_BOLD);
        label.setWidth(400, Unit.PIXELS);
        return label;
    }

    private Label taskNotAssignedLabel(TaskRecord task) {
        boolean taskAssigned = !StringUtils.isBlank(task.getAgent());
        Label label = new Label(taskAssigned ? String.format("Task is assigned to %s", task.getAgent()) : "Task is not assigned");
        label.addStyleName(BackofficeTheme.TEXT_DANGER);
        label.addStyleName(ValoTheme.LABEL_BOLD);
        label.setWidth(400, Unit.PIXELS);
        return label;
    }

    private Label taskCompletedLabel(TaskRecord task) {
        Label label = new Label(String.format("Completed as \"%s\" by %s", task.getResolution(), task.getAgent()));
        label.addStyleName(BackofficeTheme.TEXT_SUCCESS);
        label.addStyleName(ValoTheme.LABEL_BOLD);
        label.setWidth(400, Unit.PIXELS);
        return label;
    }

    private Component commentLabel(TaskRecord task) {
        TextArea comment = new TextArea();
        comment.setValue(task.getComment());
        comment.setReadOnly(true);
        comment.setRows(3);
        comment.setWidth(100, Unit.PERCENTAGE);
        VerticalLayout commentLayout = new VerticalLayout(comment);
        commentLayout.setMargin(new MarginInfo(false, true, false, true));
        return commentLayout;
    }

    private void assignToMe() {
        AssignTaskRequest request = new AssignTaskRequest();
        request.setTaskId(taskId);
        request.setAgent(LoginService.getLoginData().getUser());
        Call<Void> call = taskApiClient.assign(request);
        BackgroundOperations.callApi("Assigning task", call, t -> {
            Notifications.trayNotification("Task assigned");
            refresh();
        }, Notifications::errorNotification);
    }

    private void reassign() {
        ReassignTaskDialog dialog = taskComponents.reassignTaskDialog(taskId);
        dialog.addCloseListener(e -> refresh());
        getUI().addWindow(dialog);
    }

    private void populateLayoutAsMultitask() {
        layout.setSplitPosition(570);
        TabSheet infoTabs = new TabSheet();
        if (task.getApplicationId() != null) {
            infoTabs.addTab(loanApplicationTab(task), "Loan application");
        }
        if (task.getLoanId() != null) {
            infoTabs.addTab(loanTab(task), "Loan");
        }
        infoTabs.addTab(clientTab(task), "Client");
        infoTabs.addTab(taskTab(task), "Task");
        infoTabs.addTab(activitiesTab(task), "Client activities");

        layout.addLeftComponent(infoTabs);
        refreshSubTasks(true);
    }

    private void refreshSubTasks(boolean autoSelectLastOnly) {
        Map<String, TaskRecord> generatedSubtasksByType = taskQueries.findSubtasksByParentId(taskId).stream()
            .collect(Collectors.toMap(TaskRecord::getTaskType, Function.identity()));
        List<String> possibleSubTasks = definition.getPossibleSubTasks();

        String lastOpenTask = null;

        for (String task : possibleSubTasks) {

            TaskMetadata taskMetadata = subTasks.computeIfAbsent(task, t -> {
                TabSheet.Tab tab = layout.addTab(task, Panel::new);
                tab.setEnabled(false);
                return new TaskMetadata(tab);
            });

            TaskRecord taskRecord = generatedSubtasksByType.get(task);
            if (taskRecord != null) {
                boolean taskCreated = !taskMetadata.isEnabled();
                boolean taskStatusChanged = !taskRecord.getStatus().equals(taskMetadata.status);
                LazyTabSheet.LazyTab lazyTab = (LazyTabSheet.LazyTab) taskMetadata.tab.getComponent();
                lastOpenTask = task;

                if (taskCreated) {
                    Optional<Supplier<TaskView>> supplier = taskRegistry.getView(task);
                    if (!supplier.isPresent()) {
                        Notifications.errorNotification(String.format("View not found for task type %s", task));
                        return;
                    }
                    TaskView view = supplier.get().get();

                    lazyTab.setComponentSupplier(() -> {
                        BusinessObjectLayout taskLayout = new BusinessObjectLayout(new BusinessObjectLayout.Params().setAutoSelectFromUriFragment(false));
                        Component component = view.build(taskLayout, generatedSubtasksByType.get(task));
                        component.setEnabled(taskIsOpen && taskAssignedToMe);

                        if (TaskConstants.STATUS_COMPLETED.equals(taskRecord.getStatus())) {
                            taskLayout.addLeftComponent(new VerticalLayout(taskCompletedLabel(taskRecord)));
                        }
                        if (!StringUtils.isBlank(taskRecord.getComment())) {
                            taskLayout.addLeftComponent(commentLabel(taskRecord));
                        }
                        taskLayout.addLeftComponent(component);
                        taskLayout.setRefreshAction(lazyTab::refresh);
                        taskLayout.setSizeFull();
                        return taskLayout;
                    });
                    taskMetadata.setEnabled(true);
                    taskMetadata.tab.getComponent().setHeight(100, Unit.PERCENTAGE);
                    if (!autoSelectLastOnly) {
                        layout.selectTab(task);
                    }
                    taskMetadata.tab.setCaption(String.format("%s %s", task, generatedSubtasksByType.get(task).getId()));
                }
                if (taskCreated || taskStatusChanged) {
                    lazyTab.refresh();
                }
                taskMetadata.status = taskRecord.getStatus();
            }
        }

        //select last not completed task tab
        if (autoSelectLastOnly && lastOpenTask != null && !layout.isAutoSelectedFromUriFragment()) {
            layout.selectTab(lastOpenTask);
        }

        //task updates by workflow
        TaskRecord maybeUpdated = taskQueries.findById(taskId);
        boolean autoClosed = TaskConstants.STATUS_OPEN.equals(task.getStatus()) && !maybeUpdated.getStatus().equals(task.getStatus());
        boolean postponed = maybeUpdated.getTimesPostponed() > task.getTimesPostponed();
        if (autoClosed || postponed) {
            refresh();
            checkNextTasks();
        }
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
    private void onEvent(String tick) {
        if (lastRefreshedInSeconds() > REQUEST_INTERVAL_SEC && this.isAttached()) {
            lastRefreshed = LocalDateTime.now();
            getUI().access(() -> refreshSubTasks(false));
        }
    }

    private void checkNextTasks() {
        BackgroundOperations.callApi("Task postponed or completed", taskApiClient.count(), t -> {
            if (t.getTasksDue() > 0) {
                ((AbstractBackofficeUI) UI.getCurrent()).getTabSheetNavigator().closeCurrentTab();
                taskHelper.takeNextTask();
            }
        }, Notifications::errorNotification);
    }

    private void populateLayoutAsSingleTask(TaskView view) {
        layout.setSplitPosition(500);
        Component component = view.build(layout, task);
        component.setEnabled(taskIsOpen && taskAssignedToMe);
        layout.addLeftComponent(component);

        if (task.getApplicationId() != null) {
            layout.addTab("Loan application", () -> loanApplicationTab(task));
        }
        if (task.getLoanId() != null) {
            layout.addTab("Loan", () -> loanTab(task));
        }
        layout.addTab("Client", () -> clientTab(task));
        layout.addTab("Task", () -> taskTab(task));
        layout.addTab("Client activities", () -> activitiesTab(task));
    }

    private long lastRefreshedInSeconds() {
        return SECONDS.between(lastRefreshed, LocalDateTime.now());
    }

    private static class TaskMetadata {
        private final TabSheet.Tab tab;
        private String status;
        private boolean selected;

        private TaskMetadata(TabSheet.Tab tab) {
            this.tab = tab;
        }

        public boolean isSelected() {
            return selected;
        }

        public void setSelected(boolean selected) {
            this.selected = selected;
        }

        private boolean isEnabled() {
            return tab.isEnabled();
        }

        public void setEnabled(boolean b) {
            tab.setEnabled(b);
        }
    }

}
