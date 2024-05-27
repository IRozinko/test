package fintech.bo.components.workflow;

import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Grid;
import com.vaadin.ui.ItemCaptionGenerator;
import com.vaadin.ui.VerticalLayout;
import fintech.bo.api.client.WorkflowApiClient;
import fintech.bo.api.model.IdRequest;
import fintech.bo.api.model.permissions.BackofficePermissions;
import fintech.bo.components.JooqGridBuilder;
import fintech.bo.components.background.BackgroundOperations;
import fintech.bo.components.dialogs.Dialogs;
import fintech.bo.components.layouts.GridViewLayout;
import fintech.bo.components.notifications.Notifications;
import fintech.bo.components.security.SecuredView;
import fintech.bo.db.jooq.workflow.tables.ActivityListener;
import fintech.bo.db.jooq.workflow.tables.records.ActivityListenerRecord;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.tuple.Pair;
import org.jooq.DSLContext;
import org.springframework.beans.factory.annotation.Autowired;
import retrofit2.Call;

import java.util.Comparator;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;


@Slf4j
@SecuredView({BackofficePermissions.ADMIN, BackofficePermissions.WORKFLOW_EDIT})
public abstract class ActivityListenersViewAbs extends VerticalLayout implements View {

    @Autowired
    private DSLContext db;

    @Autowired
    private WorkflowApiClient apiClient;
    private Grid<ActivityListenerRecord> grid;

    private String workflowName;
    private Integer version;

    private ActivityListenerDataProvider dataProvider;

    public ActivityListenersViewAbs(String caption) {
        setCaption(caption);
    }

    @Override
    public void enter(ViewChangeListener.ViewChangeEvent event) {
        removeAllComponents();

        GridViewLayout layout = new GridViewLayout();
        buildTop(layout);
        buildGrid(layout);
        addComponentsAndExpand(layout);
    }

    private void buildGrid(GridViewLayout layout) {
        dataProvider = new ActivityListenerDataProvider(db);
        JooqGridBuilder<ActivityListenerRecord> builder = new JooqGridBuilder<>();

        builder.addActionColumn("Edit", this::edit);
        builder.addActionColumn("Delete", this::delete);
        builder.addColumn(ActivityListener.ACTIVITY_LISTENER.NAME);
        builder.addColumn(ActivityListener.ACTIVITY_LISTENER.ACTIVITY_NAME);
        builder.addColumn(ActivityListener.ACTIVITY_LISTENER.ACTIVITY_STATUS);
        builder.addColumn(ActivityListener.ACTIVITY_LISTENER.RESOLUTION);
        builder.addColumn(ActivityListener.ACTIVITY_LISTENER.DELAY_SEC);
        builder.addColumn(ActivityListener.ACTIVITY_LISTENER.CREATED_AT);
        builder.addColumn(ActivityListener.ACTIVITY_LISTENER.CREATED_BY);
        builder.addColumn(ActivityListener.ACTIVITY_LISTENER.UPDATED_AT);

        customGridActions(builder);

        grid = builder.build(dataProvider);
        grid.setSelectionMode(Grid.SelectionMode.SINGLE);
        layout.setContent(grid);
    }

    protected void customGridActions(JooqGridBuilder<ActivityListenerRecord> builder) {

    }

    private void delete(ActivityListenerRecord record) {
        Dialogs.confirm("Are you sure?", e -> {
            Call<Void> call = apiClient.removeListener(new IdRequest(record.getId()));
            BackgroundOperations.callApi("Deleting listener", call, v -> {
                Notifications.trayNotification("Done");
                refresh();
            }, Notifications::errorNotification);
        });
    }

    private void edit(ActivityListenerRecord item) {
        grid.select(item);
        EditActivityListenerDialog dialog = new EditActivityListenerDialog(apiClient, item, workflowName, version, getTriggerName(), getParams());
        dialog.addCloseListener(e -> refresh());
        getUI().addWindow(dialog);
    }

    private void buildTop(GridViewLayout layout) {
        ComboBox<Pair<String, Integer>> workflowSelect = new ComboBox<>();
        workflowSelect.setEmptySelectionAllowed(false);
        workflowSelect.setTextInputAllowed(false);
        workflowSelect.setWidth(300, Unit.PIXELS);

        BackgroundOperations.callApiSilent(apiClient.listWorkflows(),
            response -> {
                List<Pair<String, Integer>> workflows = response.stream()
                    .map(wf -> Pair.of(wf.getWorkflowName(), wf.getWorkflowVersion()))
                    .sorted(Comparator.comparing((Function<Pair<String, Integer>, String>) Pair::getLeft)
                        .thenComparingInt(Pair::getRight))
                    .collect(Collectors.toList());
                workflowSelect.setItems(workflows);
                workflows.stream()
                    .max(Comparator.comparingInt(Pair::getRight))
                    .ifPresent(pair -> {
                        this.workflowName = pair.getLeft();
                        this.version = pair.getRight();
                        workflowSelect.setSelectedItem(Pair.of(this.workflowName, this.version));
                    });
            },
            Notifications::errorNotification);
        workflowSelect.setItemCaptionGenerator((ItemCaptionGenerator<Pair<String, Integer>>) item -> item.getLeft() + " (Ver. " + item.getRight() + ")");
        workflowSelect.addValueChangeListener(event -> {
            this.workflowName = event.getValue().getLeft();
            this.version = event.getValue().getRight();
            refresh();
        });

        layout.addTopComponent(workflowSelect);

        layout.setRefreshAction(e -> refresh());
        layout.addActionMenuItem("Add listener", e -> addItem());
    }

    private void addItem() {
        EditActivityListenerDialog dialog = new EditActivityListenerDialog(apiClient, new ActivityListenerRecord(), workflowName, version, getTriggerName(), getParams());
        dialog.addCloseListener(e -> refresh());
        getUI().addWindow(dialog);
    }

    private void refresh() {
        dataProvider.setVersion(version);
        dataProvider.setWorkflowName(workflowName);
        dataProvider.refreshAll();
    }

    protected abstract String getTriggerName();

    protected abstract List<String[]> getParams();


}
