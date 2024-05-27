package fintech.bo.components.workflow;

import com.vaadin.data.provider.GridSortOrderBuilder;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.spring.annotation.SpringView;
import com.vaadin.ui.Component;
import com.vaadin.ui.Grid;
import com.vaadin.ui.MenuBar;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.renderers.LocalDateTimeRenderer;
import fintech.bo.api.client.WorkflowApiClient;
import fintech.bo.api.model.permissions.BackofficePermissions;
import fintech.bo.api.model.workflow.TerminateWorkflowRequest;
import fintech.bo.components.Formats;
import fintech.bo.components.GridHelper;
import fintech.bo.components.JooqGridBuilder;
import fintech.bo.components.PropertyLayout;
import fintech.bo.components.application.LoanApplicationComponents;
import fintech.bo.components.application.LoanApplicationQueries;
import fintech.bo.components.background.BackgroundOperations;
import fintech.bo.components.client.ClientComponents;
import fintech.bo.components.client.ClientQueries;
import fintech.bo.components.client.dto.ClientDTO;
import fintech.bo.components.client.repository.ClientRepository;
import fintech.bo.components.dialogs.TextInputDialog;
import fintech.bo.components.layouts.BusinessObjectLayout;
import fintech.bo.components.loan.LoanComponents;
import fintech.bo.components.loan.LoanQueries;
import fintech.bo.components.notifications.Notifications;
import fintech.bo.components.security.LoginService;
import fintech.bo.components.task.TaskComponents;
import fintech.bo.components.task.TaskDataProvider;
import fintech.bo.components.utils.UrlUtils;
import fintech.bo.db.jooq.lending.tables.records.LoanApplicationRecord;
import fintech.bo.db.jooq.lending.tables.records.LoanRecord;
import fintech.bo.db.jooq.workflow.tables.records.ActivityRecord;
import fintech.bo.db.jooq.workflow.tables.records.TriggerRecord;
import fintech.bo.db.jooq.workflow.tables.records.WorkflowAttributeRecord;
import fintech.bo.db.jooq.workflow.tables.records.WorkflowRecord;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.jooq.Result;
import org.springframework.beans.factory.annotation.Autowired;
import retrofit2.Call;

import static fintech.bo.db.jooq.workflow.Tables.ACTIVITY;
import static fintech.bo.db.jooq.workflow.Tables.TRIGGER;

@Slf4j
@SpringView(name = WorkflowView.NAME)
public class WorkflowView extends VerticalLayout implements View {

    public static final String NAME = "workflow";
    private long workflowId;

    @Autowired
    private WorkflowQueries workflowQueries;

    @Autowired
    private ClientQueries clientQueries;

    @Autowired
    private LoanApplicationQueries loanApplicationQueries;

    @Autowired
    private LoanQueries loanQueries;

    @Autowired
    private WorkflowComponents workflowComponents;

    @Autowired
    private TaskComponents taskComponents;

    @Autowired
    private WorkflowApiClient workflowApiClient;

    @Autowired
    private ClientRepository clientRepository;

    @Override
    public void enter(ViewChangeListener.ViewChangeEvent event) {
        workflowId = Long.parseLong(UrlUtils.getParam(event.getParameters(), UrlUtils.ID));
        refresh();
    }

    private void refresh() {
        removeAllComponents();
        setSpacing(false);
        setMargin(false);

        WorkflowRecord workflow = workflowQueries.findById(workflowId);
        if (workflow == null) {
            Notifications.errorNotification("Workflow not found");
            return;
        }
        setCaption(String.format("%s %s", workflow.getName(), workflow.getId()));


        BusinessObjectLayout layout = new BusinessObjectLayout();
        layout.setTitle(String.format("%s", workflow.getName()));
        buildLeft(workflow, layout);
        buildTabs(workflow, layout);
        buildActions(workflow, layout);
        addComponentsAndExpand(layout);
    }

    private void buildActions(WorkflowRecord workflow, BusinessObjectLayout layout) {
        layout.setRefreshAction(this::refresh);
        if (LoginService.hasPermission(BackofficePermissions.WORKFLOW_TERMINATE)) {
            MenuBar.MenuItem menuItem = layout.addActionMenuItem("Terminate workflow", e -> terminate(workflow));
            menuItem.setEnabled(WorkflowConstants.STATUS_ACTIVE.equals(workflow.getStatus()));
        }
    }

    private void terminate(WorkflowRecord workflow) {
        TextInputDialog prompt = new TextInputDialog("Terminate workflow", "Enter termination reason", "Terminate", reason -> {
            TerminateWorkflowRequest request = new TerminateWorkflowRequest();
            request.setWorkflowId(workflow.getId());
            request.setReason(reason);
            Call<Void> call = workflowApiClient.terminate(request);
            BackgroundOperations.callApi("Terminating workflow", call, t -> {
                Notifications.trayNotification("Workflow terminated");
                refresh();
            }, Notifications::errorNotification);
        });
        getUI().addWindow(prompt);
    }

    private void buildTabs(WorkflowRecord workflow, BusinessObjectLayout layout) {
        layout.addTab("Activities", () -> activities(workflow));
        layout.addTab("Tasks", () -> tasks(workflow));
        layout.addTab("Triggers", () -> triggers(workflow));
    }

    private Component tasks(WorkflowRecord workflow) {
        TaskDataProvider dataProvider = taskComponents.taskDataProvider();
        dataProvider.setWorkflowId(workflow.getId());
        return taskComponents.taskGrid(dataProvider);
    }

    private Component activities(WorkflowRecord workflow) {
        ActivitiesDataProvider dataProvider = workflowComponents.activitiesDataProvider();
        dataProvider.setWorkflowId(workflow.getId());

        Grid<ActivityRecord> grid = new Grid<>();
        Grid.Column<ActivityRecord, Long> id = grid.addColumn(ActivityRecord::getId).setCaption("ID").setId(ACTIVITY.ID.getName()).setWidth(100);
        grid.addColumn(ActivityRecord::getName).setCaption("Name").setId(ACTIVITY.NAME.getName()).setWidth(200);
        grid.addColumn(ActivityRecord::getActor).setCaption("Actor").setId(ACTIVITY.ACTOR.getName()).setWidth(100);
        grid.addColumn(ActivityRecord::getStatus).setCaption("Status").setId(ACTIVITY.STATUS.getName()).setWidth(200).setStyleGenerator(WorkflowComponents.activityStatusStyle());
        grid.addColumn(ActivityRecord::getResolution).setCaption("Resolution").setId(ACTIVITY.RESOLUTION.getName()).setWidth(200);
        grid.addColumn(ActivityRecord::getResolutionDetail).setCaption("Resolution detail").setId(ACTIVITY.RESOLUTION_DETAIL.getName()).setWidth(200);
        grid.addColumn(ActivityRecord::getAttempts).setCaption("Attempts").setId(ACTIVITY.ATTEMPTS.getName()).setWidth(80);
        grid.addColumn(ActivityRecord::getNextAttemptAt).setCaption("Next attempt at").setId(ACTIVITY.NEXT_ATTEMPT_AT.getName()).setRenderer(new LocalDateTimeRenderer(Formats.dateTimeFormatter())).setWidth(200);
        grid.addColumn(ActivityRecord::getCompletedAt).setCaption("Completed At").setId(ACTIVITY.COMPLETED_AT.getName()).setRenderer(new LocalDateTimeRenderer(Formats.dateTimeFormatter())).setWidth(200);
        grid.setSortOrder(new GridSortOrderBuilder<ActivityRecord>().thenAsc(id));
        GridHelper.addTotalCountAsCaption(grid, dataProvider);
        grid.setDataProvider(dataProvider);
        grid.setSizeFull();
        return grid;
    }

    private Component triggers(WorkflowRecord workflow) {
        TriggersDataProvider dataProvider = workflowComponents.triggersDataProvider();
        dataProvider.setWorkflowId(workflow.getId());

        JooqGridBuilder<TriggerRecord> builder = new JooqGridBuilder<>();
        builder.addColumn(TRIGGER.ID);
        builder.addColumn(TRIGGER.NAME);
        builder.addColumn(TRIGGER.STATUS);
        builder.addColumn(TRIGGER.NEXT_ATTEMPT_AT);
        builder.addColumn(TRIGGER.ACTIVITY_ID);
        builder.addAuditColumns(TRIGGER);
        builder.sortDesc(TRIGGER.ID);
        return builder.build(dataProvider);
    }

    private void buildLeft(WorkflowRecord workflow, BusinessObjectLayout layout) {
        PropertyLayout info = workflowInfo(workflow);
        PropertyLayout params = params(workflow);

        layout.addLeftComponent(info);
        layout.addLeftComponent(params);
    }

    private PropertyLayout params(WorkflowRecord workflow) {
        Result<WorkflowAttributeRecord> params = workflowQueries.findAttributes(workflow.getId());
        PropertyLayout layout = new PropertyLayout("Params");
        params.forEach(param -> layout.add(camelCaseToHuman(param.getKey()), param.getValue()));
        return layout;
    }

    private static String camelCaseToHuman(String value) {
        return StringUtils.join(StringUtils.splitByCharacterTypeCamelCase(value), ' ');
    }

    private PropertyLayout workflowInfo(WorkflowRecord workflow) {
        ClientDTO client = clientRepository.getRequired(workflow.getClientId());
        LoanApplicationRecord application = loanApplicationQueries.findById(workflow.getApplicationId());
        LoanRecord loan = loanQueries.findById(workflow.getLoanId());

        PropertyLayout layout = new PropertyLayout("Workflow");
        layout.addLink("ID", "" + workflow.getId(), WorkflowComponents.workflowLink(workflow.getId()));
        layout.add("Name", workflow.getName());
        layout.add("Status", workflow.getStatus());
        layout.add("Created at", workflow.getCreatedAt());
        layout.add("Created by", workflow.getCreatedBy());
        layout.add("Completed at", workflow.getCompletedAt());
        layout.add("Terminate reason", workflow.getTerminateReason());
        if (client != null) {
            layout.addLink("Client", client.getClientNumber(), ClientComponents.clientLink(client.getId()));
            layout.add("Client name", ClientComponents.firstAndLastName(client));
        }
        if (application != null) {
            layout.addLink("Application", application.getApplicationNumber(), LoanApplicationComponents.applicationLink(application.getId()));
        }
        if (loan != null) {
            layout.addLink("Loan", loan.getLoanNumber(), LoanComponents.loanLink(loan.getId()));
        }
        return layout;
    }

}
