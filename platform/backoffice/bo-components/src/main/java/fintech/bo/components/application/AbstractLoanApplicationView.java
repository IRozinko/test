package fintech.bo.components.application;

import com.google.common.collect.ImmutableList;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.ui.Component;
import com.vaadin.ui.Grid;
import com.vaadin.ui.VerticalLayout;
import fintech.bo.components.JooqGridBuilder;
import fintech.bo.components.application.info.ApplicationInfo;
import fintech.bo.components.client.ClientComponents;
import fintech.bo.components.client.ClientQueries;
import fintech.bo.components.client.dto.ClientDTO;
import fintech.bo.components.client.repository.ClientRepository;
import fintech.bo.components.dialogs.Dialogs;
import fintech.bo.components.layouts.BusinessObjectLayout;
import fintech.bo.components.loan.LoanComponents;
import fintech.bo.components.loan.LoanQueries;
import fintech.bo.components.notifications.Notifications;
import fintech.bo.components.task.TaskComponents;
import fintech.bo.components.task.TaskDataProvider;
import fintech.bo.components.utils.UrlUtils;
import fintech.bo.components.workflow.WorkflowComponents;
import fintech.bo.db.jooq.lending.tables.records.LoanApplicationRecord;
import fintech.bo.db.jooq.lending.tables.records.LoanRecord;
import fintech.bo.db.jooq.rule.tables.records.RuleLogRecord;
import lombok.extern.slf4j.Slf4j;
import org.jooq.DSLContext;
import org.jooq.Record;
import org.jooq.Result;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Objects;

import static fintech.bo.db.jooq.rule.tables.RuleLog.RULE_LOG;
import static fintech.bo.db.jooq.workflow.Tables.WORKFLOW_;
import static fintech.bo.db.jooq.workflow.tables.Activity.ACTIVITY;
import static java.lang.String.format;

@Slf4j
public abstract class AbstractLoanApplicationView extends VerticalLayout implements View {

    public static final String NAME = "loan-application";

    protected final LoanApplicationComponents loanApplicationComponents;

    @Autowired
    protected LoanApplicationQueries loanApplicationQueries;

    @Autowired
    protected LoanComponents loanComponents;

    @Autowired
    protected ClientComponents clientComponents;

    @Autowired
    protected ClientQueries clientQueries;

    @Autowired
    protected LoanQueries loanQueries;

    @Autowired
    protected TaskComponents taskComponents;

    @Autowired
    protected DSLContext db;

    @Autowired
    protected ClientRepository clientRepository;

    protected long applicationId;

    protected AbstractLoanApplicationView(LoanApplicationComponents loanApplicationComponents) {
        this.loanApplicationComponents = Objects.requireNonNull(loanApplicationComponents);
    }

    @Override
    public void enter(ViewChangeListener.ViewChangeEvent event) {
        applicationId = Long.parseLong(UrlUtils.getParam(event.getParameters(), UrlUtils.ID));
        refresh();
    }

    protected abstract void addCustomActions(LoanApplicationRecord application, BusinessObjectLayout layout);

    protected void refresh() {
        removeAllComponents();
        setSpacing(false);
        setMargin(false);

        LoanApplicationRecord application = loanApplicationQueries.findById(applicationId);
        if (application == null) {
            Notifications.errorNotification("Loan application not found");
            return;
        }
        setCaption(format("Loan application %s", application.getApplicationNumber()));

        BusinessObjectLayout layout = new BusinessObjectLayout();
        layout.setTitle(application.getApplicationNumber());
        buildLeft(application, layout);
        buildTabs(application, layout);
        buildActions(application, layout);
        addComponentsAndExpand(layout);
    }

    private void buildActions(LoanApplicationRecord application, BusinessObjectLayout layout) {
        addCustomActions(application, layout);
        layout.setRefreshAction(this::refresh);
    }

    private void buildTabs(LoanApplicationRecord application, BusinessObjectLayout layout) {
        layout.addTab("Activities", () -> activitiesTab(application));
        layout.addTab("Tasks", () -> tasksTab(application));
        layout.addTab("Rules", () -> rulesTab(application));
        addCustomTabs(application, layout);
    }

    protected abstract void addCustomTabs(LoanApplicationRecord application, BusinessObjectLayout layout);

    private Component tasksTab(LoanApplicationRecord application) {
        TaskDataProvider dataProvider = taskComponents.taskDataProvider();
        dataProvider.setApplicationId(application.getId());
        Grid<Record> grid = taskComponents.taskGrid(dataProvider);
        grid.setSizeFull();
        return grid;
    }

    private Component rulesTab(LoanApplicationRecord application) {
        Result<RuleLogRecord> rules = db.selectFrom(RULE_LOG).where(RULE_LOG.APPLICATION_ID.eq(application.getId())).fetch();
        JooqGridBuilder<RuleLogRecord> builder = new JooqGridBuilder<>();
        builder.addActionColumn("Show", r -> Dialogs.showText("Checks", r.getChecksJson()));
        builder.addColumn(RULE_LOG.RULE).setWidthUndefined();
        builder.addColumn(RULE_LOG.DECISION);
        builder.addColumn(RULE_LOG.REASON);
        builder.addColumn(RULE_LOG.REASON_DETAILS);
        return builder.build(rules);
    }

    private Component activitiesTab(LoanApplicationRecord application) {
        Result<Record> records = db.select(ImmutableList.of(
            ACTIVITY.ID,
            ACTIVITY.WORKFLOW_ID,
            ACTIVITY.NAME,
            ACTIVITY.ACTOR,
            ACTIVITY.STATUS,
            ACTIVITY.RESOLUTION,
            ACTIVITY.RESOLUTION_DETAIL,
            ACTIVITY.CREATED_BY,
            ACTIVITY.CREATED_AT,
            ACTIVITY.NEXT_ATTEMPT_AT,
            ACTIVITY.ATTEMPTS,
            ACTIVITY.COMPLETED_AT,
            WORKFLOW_.NAME.as("workflow_name")
        ))
            .from(ACTIVITY.join(WORKFLOW_).onKey()).where(WORKFLOW_.APPLICATION_ID.eq(application.getId())).fetch();
        JooqGridBuilder<Record> builder = new JooqGridBuilder<>();
        builder.addColumn(ACTIVITY.WORKFLOW_ID);
        builder.addLinkColumn(WORKFLOW_.NAME, record -> WorkflowComponents.workflowLink(record.get(ACTIVITY.WORKFLOW_ID)));
        builder.addColumn(ACTIVITY.NAME);
        builder.addColumn(ACTIVITY.ACTOR);
        builder.addColumn(ACTIVITY.STATUS).setStyleGenerator(WorkflowComponents.activityStatusStyleForRecord());
        builder.addColumn(ACTIVITY.RESOLUTION);
        builder.addColumn(ACTIVITY.RESOLUTION_DETAIL);
        builder.addColumn(ACTIVITY.ATTEMPTS);
        builder.addColumn(ACTIVITY.COMPLETED_AT);
        builder.addColumn(ACTIVITY.CREATED_AT);
        builder.addColumn(ACTIVITY.ID);
        builder.sortAsc(ACTIVITY.WORKFLOW_ID, ACTIVITY.ID);
        Grid<Record> grid = builder.build(records);
        grid.setSizeFull();
        return grid;
    }

    private void buildLeft(LoanApplicationRecord application, BusinessObjectLayout layout) {
        layout.addLeftComponent(ApplicationInfo.fromApplication(application));
        ClientDTO client = clientRepository.getRequired(application.getClientId());
        layout.addLeftComponent(clientComponents.clientInfo(client));
        if (application.getLoanId() != null) {
            LoanRecord loan = loanQueries.findById(application.getLoanId());
            layout.addLeftComponent(loanComponents.loanInfo(loan));
        }
        addCustomLeftComponent(application, layout);
    }

    protected abstract void addCustomLeftComponent(LoanApplicationRecord application, BusinessObjectLayout layout);
}
