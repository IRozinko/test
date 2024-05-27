package fintech.bo.components.workflow;

import com.vaadin.ui.Grid;
import com.vaadin.ui.StyleGenerator;
import fintech.bo.components.BackofficeTheme;
import fintech.bo.components.JooqGridBuilder;
import fintech.bo.components.application.LoanApplicationComponents;
import fintech.bo.components.client.ClientComponents;
import fintech.bo.components.client.JooqClientDataService;
import fintech.bo.db.jooq.workflow.tables.records.ActivityRecord;
import org.jooq.DSLContext;
import org.jooq.Record;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import static fintech.bo.db.jooq.crm.tables.Client.CLIENT;
import static fintech.bo.db.jooq.lending.tables.Loan.LOAN;
import static fintech.bo.db.jooq.lending.tables.LoanApplication.LOAN_APPLICATION;
import static fintech.bo.db.jooq.workflow.Tables.ACTIVITY;
import static fintech.bo.db.jooq.workflow.Tables.WORKFLOW_;

@Component
public class WorkflowComponents {

    @Autowired
    private DSLContext db;

    @Autowired
    private JooqClientDataService jooqClientDataService;

    public WorkflowDataProvider workflowDataProvider() {
        return new WorkflowDataProvider(db, jooqClientDataService);
    }

    public ActivitiesDataProvider activitiesDataProvider() {
        return new ActivitiesDataProvider(db);
    }

    public TriggersDataProvider triggersDataProvider() {
        return new TriggersDataProvider(db);
    }

    public Grid<Record> workflowGrid(WorkflowDataProvider dataProvider) {
        JooqGridBuilder<Record> builder = new JooqGridBuilder<>();
        builder.addNavigationColumn("Open", r -> "workflow/" + r.get(WORKFLOW_.ID));
        builder.addColumn(WORKFLOW_.NAME).setWidth(300);
        builder.addColumn(WORKFLOW_.STATUS).setStyleGenerator(workflowStatusStyle());
        builder.addLinkColumn(CLIENT.CLIENT_NUMBER, r -> ClientComponents.clientLink(r.get(WORKFLOW_.CLIENT_ID)));
        builder.addLinkColumn(LOAN_APPLICATION.APPLICATION_NUMBER, r -> LoanApplicationComponents.applicationLink(r.get(WORKFLOW_.APPLICATION_ID)));
        builder.addLinkColumn(LOAN.LOAN_NUMBER, r -> LoanApplicationComponents.applicationLink(r.get(WORKFLOW_.LOAN_ID)));
        builder.addColumn(WORKFLOW_.TERMINATE_REASON);
        builder.addColumn(WORKFLOW_.COMPLETED_AT);
        builder.addAuditColumns(WORKFLOW_);
        builder.addColumn(WORKFLOW_.ID);
        builder.sortDesc(WORKFLOW_.ID);
        return builder.build(dataProvider);
    }


    public static String workflowLink(Long workflowId) {
        return WorkflowView.NAME + "/" + workflowId;
    }

    public static StyleGenerator<Record> workflowStatusStyle() {
        return item -> {
            String status = item.get(WORKFLOW_.STATUS);
            if (WorkflowConstants.STATUS_ACTIVE.equals(status)) {
                return BackofficeTheme.TEXT_ACTIVE;
            } else if (WorkflowConstants.STATUS_TERMINATED.equals(status)) {
                return BackofficeTheme.TEXT_DANGER;
            } else if (WorkflowConstants.STATUS_COMPLETED.equals(status)) {
                return BackofficeTheme.TEXT_SUCCESS;
            } else {
                return "";
            }
        };
    }

    public static StyleGenerator<ActivityRecord> activityStatusStyle() {
        return item -> {
            String status = item.getStatus();
            return activityStatusStyle(status);
        };
    }

    private static String activityStatusStyle(String status) {
        if (WorkflowConstants.ACTIVITY_STATUS_ACTIVE.equals(status)) {
            return BackofficeTheme.TEXT_ACTIVE;
        } else if (WorkflowConstants.ACTIVITY_STATUS_COMPLETED.equals(status)) {
            return BackofficeTheme.TEXT_SUCCESS;
        } else if (WorkflowConstants.ACTIVITY_STATUS_CANCELLED.equals(status)) {
            return BackofficeTheme.TEXT_GRAY;
        } else if (WorkflowConstants.ACTIVITY_STATUS_FAILED.equals(status)) {
            return BackofficeTheme.TEXT_DANGER;
        } else {
            return "";
        }
    }

    public static StyleGenerator<Record> activityStatusStyleForRecord() {
        return item -> {
            String status = item.get(ACTIVITY.STATUS);
            return activityStatusStyle(status);
        };
    }

}
