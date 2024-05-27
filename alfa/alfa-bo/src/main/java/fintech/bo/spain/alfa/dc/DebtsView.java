package fintech.bo.spain.alfa.dc;

import com.vaadin.spring.annotation.SpringView;
import fintech.bo.components.JooqGridBuilder;
import fintech.bo.components.client.ClientComponents;
import fintech.bo.components.dc.AbstractDebtsView;
import fintech.bo.components.dc.DcComponents;
import fintech.bo.components.loan.LoanComponents;
import org.jooq.Record;

import static fintech.bo.components.client.ClientDataProviderUtils.FIELD_CLIENT_NAME;
import static fintech.bo.db.jooq.crm.Tables.CLIENT;
import static fintech.bo.db.jooq.dc.Tables.DEBT;

@SpringView(name = DebtsView.NAME)
public class DebtsView extends AbstractDebtsView {

    @Override
    protected void addColumns(JooqGridBuilder<Record> builder) {
        builder.addNavigationColumn("Open", r -> DcComponents.debtLinkWithBackNavigation(r.get(DEBT.ID), NAME));
        builder.addLinkColumn(DEBT.LOAN_NUMBER, r -> LoanComponents.loanLink(r.get(DEBT.LOAN_ID)));
        builder.addLinkColumn(FIELD_CLIENT_NAME, r -> ClientComponents.clientLink(r.get(DEBT.CLIENT_ID)));
        builder.addColumn(CLIENT.DOCUMENT_NUMBER);
        builder.addColumn(CLIENT.PHONE);
        builder.addColumn(DEBT.PORTFOLIO);
        builder.addColumn(DEBT.AGING_BUCKET);
        builder.addColumn(DEBT.PRIORITY);
        builder.addColumn(DEBT.STATUS);
        builder.addColumn(DEBT.SUB_STATUS);
        builder.addColumn(DEBT.LOAN_STATUS_DETAIL);
        builder.addColumn(DEBT.LAST_ACTION);
        builder.addColumn(DEBT.NEXT_ACTION);
        builder.addColumn(DEBT.NEXT_ACTION_AT);
        builder.addColumn(DEBT.DPD);
        builder.addColumn(DEBT.PERIOD_COUNT).setCaption("Term (days)");
        builder.addColumn(DEBT.TOTAL_DUE);
        builder.addColumn(DEBT.TOTAL_OUTSTANDING);
        builder.addColumn(DEBT.TOTAL_PAID);
        builder.addColumn(DEBT.ID);
        builder.addColumn(DEBT.MANAGING_COMPANY);
        builder.addColumn(DEBT.OWNING_COMPANY);
        builder.addAuditColumns(DEBT);
    }

}
