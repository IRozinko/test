package fintech.bo.spain.alfa.applications;

import com.vaadin.ui.Grid;
import com.vaadin.ui.Label;
import fintech.bo.components.JooqGridBuilder;
import fintech.bo.components.ProductResolver;
import fintech.bo.components.application.LoanApplicationComponents;
import fintech.bo.components.application.LoanApplicationDataProvider;
import fintech.bo.components.application.LoanApplicationQueries;
import fintech.bo.components.client.ClientComponents;
import fintech.bo.components.client.JooqClientDataService;
import fintech.bo.components.loan.LoanComponents;
import fintech.bo.components.workflow.WorkflowQueries;
import org.jooq.DSLContext;
import org.jooq.Record;
import org.springframework.stereotype.Component;

import static fintech.bo.components.client.ClientDataProviderUtils.FIELD_CLIENT_NAME;
import static fintech.bo.db.jooq.crm.tables.Client.CLIENT;
import static fintech.bo.db.jooq.lending.Tables.LOAN_APPLICATION;
import static fintech.bo.db.jooq.lending.tables.Loan.LOAN;
import static org.jooq.impl.DSL.field;
import static org.jooq.impl.DSL.name;

@Component
public class AlfaLoanApplicationComponents extends LoanApplicationComponents {

    protected AlfaLoanApplicationComponents(DSLContext db,
                                            WorkflowQueries workflowQueries,
                                            LoanApplicationQueries applicationQueries,
                                            JooqClientDataService jooqClientDataService) {
        super(db, workflowQueries, applicationQueries, jooqClientDataService);
    }


    public Grid<Record> mainGrid(LoanApplicationDataProvider dataProvider) {
        JooqGridBuilder<Record> builder = new JooqGridBuilder<>();
        builder.addNavigationColumn("Open", r -> "loan-application/" + r.get(LOAN_APPLICATION.ID));
        builder.addColumn(LOAN_APPLICATION.APPLICATION_NUMBER);
        builder.addColumn(LOAN_APPLICATION.STATUS);
        builder.addColumn(LOAN_APPLICATION.STATUS_DETAIL).setStyleGenerator(statusDetailStyle());
        builder.addComponentColumn(record -> new Label(String.join(", ", record.get(LoanApplicationDataProvider.FIELD_ACTIVE_WORKFLOW_STEPS)))).setSortable(false).setWidth(150).setCaption("Workflow step");
        builder.addLinkColumn(FIELD_CLIENT_NAME, r -> ClientComponents.clientLink(r.get(LOAN_APPLICATION.CLIENT_ID)));
        builder.addColumn(LOAN_APPLICATION.REQUESTED_PRINCIPAL);
        if (ProductResolver.isPayday()) {
            builder.addColumn(LOAN_APPLICATION.REQUESTED_PERIOD_COUNT).setCaption("Requested term");
        }
        builder.addColumn(LOAN_APPLICATION.OFFERED_PRINCIPAL);
        builder.addColumn(LOAN_APPLICATION.SUBMITTED_AT);
        builder.addColumn(LOAN_APPLICATION.CLOSE_DATE);
        builder.addColumn(LOAN_APPLICATION.CLOSE_REASON).setWidth(200);
        builder.addLinkColumn(LOAN.LOAN_NUMBER, r -> LoanComponents.loanLink(r.get(LOAN_APPLICATION.LOAN_ID)));
        builder.addColumn(CLIENT.DOCUMENT_NUMBER);
        builder.addComponentColumn(record -> LoanApplicationComponents.scoreBar(record.get(LOAN_APPLICATION.SCORE_BUCKET), record.get(LOAN_APPLICATION.SCORE))).setSortable(false).setWidth(120).setCaption("Score");
        builder.addColumn(LOAN_APPLICATION.SCORE);
        builder.addColumn(LOAN_APPLICATION.SCORE_BUCKET);
        builder.addColumn(LOAN_APPLICATION.SOURCE_TYPE);
        builder.addColumn(LOAN_APPLICATION.SOURCE_NAME);
        builder.addColumn(field(name("discount_in_percent")));
        builder.addAuditColumns(LOAN_APPLICATION);
        builder.addColumn(LOAN_APPLICATION.ID);
        return builder.build(dataProvider);
    }

    @Override
    protected void addGridColumns(JooqGridBuilder<Record> builder) {
        builder.addNavigationColumn("Open", r -> "loan-application/" + r.get(LOAN_APPLICATION.ID));
        builder.addColumn(LOAN_APPLICATION.APPLICATION_NUMBER);
        builder.addColumn(LOAN_APPLICATION.STATUS);
        builder.addColumn(LOAN_APPLICATION.STATUS_DETAIL).setStyleGenerator(statusDetailStyle());
        builder.addComponentColumn(record -> new Label(String.join(", ", record.get(LoanApplicationDataProvider.FIELD_ACTIVE_WORKFLOW_STEPS)))).setSortable(false).setWidth(150).setCaption("Workflow step");
        builder.addColumn(LOAN_APPLICATION.SUBMITTED_AT);
        builder.addColumn(LOAN_APPLICATION.REQUESTED_PRINCIPAL);
        if (ProductResolver.isPayday()) {
            builder.addColumn(LOAN_APPLICATION.REQUESTED_PERIOD_COUNT).setCaption("Requested term");
        }
        builder.addColumn(LOAN_APPLICATION.OFFERED_PRINCIPAL);
        builder.addColumn(field(name("discount_in_percent")));
        builder.addColumn(LOAN_APPLICATION.CLOSE_DATE);
        builder.addColumn(LOAN_APPLICATION.CLOSE_REASON).setWidth(200);
        builder.addComponentColumn(record -> LoanApplicationComponents.scoreBar(record.get(LOAN_APPLICATION.SCORE_BUCKET), record.get(LOAN_APPLICATION.SCORE))).setSortable(false).setWidth(120).setCaption("Score");
        builder.addColumn(LOAN_APPLICATION.SCORE);
        builder.addColumn(LOAN_APPLICATION.SCORE_BUCKET);
        builder.addColumn(LOAN_APPLICATION.SOURCE_TYPE);
        builder.addColumn(LOAN_APPLICATION.SOURCE_NAME);
        builder.addAuditColumns(LOAN_APPLICATION);
        builder.addColumn(LOAN_APPLICATION.ID);
    }
}
