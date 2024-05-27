package fintech.bo.spain.alfa.viventor;

import com.vaadin.ui.Grid;
import fintech.bo.components.JooqGridBuilder;
import fintech.bo.components.dialogs.Dialogs;
import fintech.bo.components.loan.LoanComponents;
import fintech.bo.db.jooq.viventor.tables.records.LogRecord;
import fintech.bo.spain.alfa.db.jooq.alfa.tables.records.ViventorLoanDataRecord;
import org.jooq.DSLContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import static fintech.bo.db.jooq.viventor.Tables.LOG;
import static fintech.bo.spain.alfa.db.jooq.alfa.tables.ViventorLoanData.VIVENTOR_LOAN_DATA;

@Component
public class ViventorComponents {

    @Autowired
    private DSLContext db;

    public ViventorLoanDataProvider viventorLoanDataProvider() {
        return new ViventorLoanDataProvider(db);
    }

    public ViventorLogDataProvider viventorLogDataProvider() {
        return new ViventorLogDataProvider(db);
    }


    public Grid<LogRecord> logGrid(ViventorLogDataProvider dataProvider) {
        JooqGridBuilder<LogRecord> builder = new JooqGridBuilder<>();
        builder.addActionColumn("Request", this::showRequestDialog);
        builder.addActionColumn("Response", this::showResponseDialog);
        builder.addLinkColumn(LOG.LOAN_ID, r -> LoanComponents.loanLink(r.getLoanId())).setCaption("Loan Id");
        builder.addColumn(LOG.VIVENTOR_LOAN_ID);
        builder.addColumn(LOG.REQUEST_TYPE);
        builder.addColumn(LOG.STATUS);
        builder.addColumn(LOG.RESPONSE_STATUS_CODE);
        builder.addColumn(LOG.REQUEST_URL);
        builder.addColumn(LOG.ID);
        builder.addAuditColumns(LOG);
        builder.sortDesc(LOG.ID);
        return builder.build(dataProvider);
    }

    private void showRequestDialog(LogRecord item) {
        Dialogs.showText("Request", item.getRequestBody());
    }

    private void showResponseDialog(LogRecord item) {
        Dialogs.showText("Response", item.getResponseBody());
    }

    public Grid<ViventorLoanDataRecord> loansGrid(ViventorLoanDataProvider dataProvider) {
        JooqGridBuilder<ViventorLoanDataRecord> builder = new JooqGridBuilder<>();
        builder.addNavigationColumn("Open", r -> "viventor-loan/" + r.get(VIVENTOR_LOAN_DATA.ID));
        builder.addColumn(VIVENTOR_LOAN_DATA.VIVENTOR_LOAN_ID).setCaption("Viventor Loan Id");
        builder.addLinkColumn(VIVENTOR_LOAN_DATA.LOAN_ID, r -> LoanComponents.loanLink(r.getLoanId())).setCaption("Test Loan Id");
        builder.addColumn(VIVENTOR_LOAN_DATA.START_DATE);
        builder.addColumn(VIVENTOR_LOAN_DATA.STATUS);
        builder.addColumn(VIVENTOR_LOAN_DATA.STATUS_DETAIL);
        builder.addColumn(VIVENTOR_LOAN_DATA.PRINCIPAL);
        builder.addColumn(VIVENTOR_LOAN_DATA.VIVENTOR_LOAN_EXTENSION);
        builder.addColumn(VIVENTOR_LOAN_DATA.LAST_SYNCED_AT);
        builder.addColumn(VIVENTOR_LOAN_DATA.ID);
        builder.addAuditColumns(VIVENTOR_LOAN_DATA);
        builder.sortDesc(VIVENTOR_LOAN_DATA.ID);
        return builder.build(dataProvider);
    }

}
