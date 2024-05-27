package fintech.bo.components.task;

import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Grid;
import com.vaadin.ui.StyleGenerator;
import com.vaadin.ui.UI;
import fintech.bo.api.client.TaskApiClient;
import fintech.bo.components.BackofficeTheme;
import fintech.bo.components.DateUtils;
import fintech.bo.components.JooqGridBuilder;
import fintech.bo.components.PollingScheduler;
import fintech.bo.components.PropertyLayout;
import fintech.bo.components.client.ClientComponents;
import fintech.bo.components.client.JooqClientDataService;
import fintech.bo.db.jooq.task.tables.Log;
import fintech.bo.db.jooq.task.tables.records.LogRecord;
import fintech.bo.db.jooq.task.tables.records.TaskRecord;
import org.jooq.DSLContext;
import org.jooq.Record;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Objects;

import static fintech.bo.components.client.ClientDataProviderUtils.FIELD_CLIENT_NAME;
import static fintech.bo.db.jooq.task.Task.TASK;

@Component
public class TaskComponents {

    @Autowired
    private DSLContext db;

    @Autowired
    private TaskApiClient taskApiClient;

    @Autowired
    private TaskQueries taskQueries;

    @Autowired
    private PollingScheduler pollingScheduler;

    @Autowired
    private TaskQueueCache taskQueueCache;

    @Autowired
    private JooqClientDataService jooqClientDataService;

    public TaskQueueComponent taskQueueComponent() {
        return new TaskQueueComponent(UI.getCurrent(), taskQueueCache, taskApiClient, pollingScheduler);
    }

    public TaskDataProvider taskDataProvider() {
        return new TaskDataProvider(db, jooqClientDataService);
    }

    public TaskLogDataProvider taskLogDataProvider() {
        return new TaskLogDataProvider(db);
    }

    public ComboBox<String> taskStatusComboBox() {
        ComboBox<String> comboBox = new ComboBox<>();
        comboBox.setPlaceholder("Task status");
        comboBox.setItems(TaskConstants.ALL_STATUSES);
        comboBox.setTextInputAllowed(false);
        return comboBox;
    }

    public ComboBox<String> taskTypeComboBox() {
        ComboBox<String> comboBox = new ComboBox<>();
        comboBox.setPlaceholder("Task type");
        comboBox.setItems(taskQueries.getTaskTypes());
        comboBox.setPageLength(20);
        comboBox.setTextInputAllowed(true);
        return comboBox;
    }

    public Grid<LogRecord> taskLogGrid(TaskLogDataProvider dataProvider) {
        JooqGridBuilder<LogRecord> builder = new JooqGridBuilder<>();
        builder.addColumn(Log.LOG.ID);
        builder.addColumn(Log.LOG.CREATED_AT);
        builder.addColumn(Log.LOG.CREATED_BY);
        builder.addColumn(Log.LOG.OPERATION);
        builder.addColumn(Log.LOG.AGENT);
        builder.addColumn(Log.LOG.REASON);
        builder.addColumn(Log.LOG.RESOLUTION);
        builder.addColumn(Log.LOG.RESOLUTION_DETAIL);
        builder.addColumn(Log.LOG.COMMENT);
        builder.sortDesc(Log.LOG.ID);
        return builder.build(dataProvider);
    }

    public Grid<Record> taskGrid(TaskDataProvider dataProvider) {
        JooqGridBuilder<Record> builder = new JooqGridBuilder<>();
        builder.addNavigationColumn("Open", r -> "task/" + r.get(TASK.TASK_.ID));
        builder.addColumn(TASK.TASK_.ID);
        builder.addColumn(TASK.TASK_.STATUS).setStyleGenerator(taskStatusStyle());
        builder.addColumn(TASK.TASK_.TASK_TYPE).setWidth(200);
        builder.addColumn(TASK.TASK_.AGENT);
        builder.addColumn(TaskDataProvider.FIELD_LAST_COMMENT).setWidth(250).setStyleGenerator(item -> BackofficeTheme.TEXT_ACTIVE);
        builder.addColumn(TASK.TASK_.RESOLUTION);
        builder.addColumn(TASK.TASK_.RESOLUTION_DETAIL);
        builder.addColumn(TaskDataProvider.FIELD_HOURS_DUE);
        builder.addLinkColumn(FIELD_CLIENT_NAME, r -> ClientComponents.clientLink(r.get(TASK.TASK_.CLIENT_ID)));
        builder.addColumn(TASK.TASK_.TIMES_POSTPONED);
        builder.addColumn(TASK.TASK_.DUE_AT);
        builder.addColumn(TASK.TASK_.EXPIRES_AT);
        builder.addAuditColumns(TASK.TASK_);
        builder.sortDesc(TASK.TASK_.CREATED_AT);
        return builder.build(dataProvider);
    }

    private static StyleGenerator<Record> taskStatusStyle() {
        return item -> {
            String status = item.get(TASK.TASK_.STATUS);
            if (TaskConstants.STATUS_OPEN.equals(status)) {
                return BackofficeTheme.TEXT_ACTIVE;
            } else if (TaskConstants.STATUS_CANCELLED.equals(status)) {
                return BackofficeTheme.TEXT_GRAY;
            } else if (TaskConstants.STATUS_COMPLETED.equals(status)) {
                return BackofficeTheme.TEXT_SUCCESS;
            } else {
                return "";
            }
        };
    }

    public static String taskLink(Long id) {
        return BaseTaskView.NAME + "/" + id;
    }

    public PropertyLayout taskInfo(TaskRecord task) {
        PropertyLayout layout = new PropertyLayout("Task");
        layout.add("Type", task.getTaskType());
        layout.add("Status", task.getStatus());
        layout.add("Agent", task.getAgent());
        layout.add("Due at", task.getDueAt());
        layout.add("Due hours", DateUtils.hoursFromNow(task.getDueAt()));
        layout.add("Expires at", task.getExpiresAt());
        layout.add("Resolution", task.getResolution());
        layout.add("Resolution detail", String.format("%s, %s", Objects.toString(task.getResolutionDetail(), "-"), Objects.toString(task.getResolutionSubDetail(), "-")));
        layout.add("Comment", task.getComment());
        layout.add("Times postponed", task.getTimesPostponed());
        layout.add("Times reopened", task.getTimesReopened());
        layout.add("Created At", task.getCreatedAt());
        return layout;
    }

    public ReassignTaskDialog reassignTaskDialog(Long taskId) {
        return new ReassignTaskDialog(taskId, db, taskApiClient);
    }
}
