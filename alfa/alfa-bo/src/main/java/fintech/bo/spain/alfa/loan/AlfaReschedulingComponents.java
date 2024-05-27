package fintech.bo.spain.alfa.loan;

import fintech.bo.components.JooqGridBuilder;
import fintech.bo.components.client.JooqClientDataService;
import fintech.bo.components.loan.ReschedulingComponents;
import fintech.bo.components.loan.ReschedulingQueries;
import fintech.bo.spain.alfa.api.AlfaApiClient;
import org.jooq.DSLContext;
import org.jooq.Record;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import static fintech.bo.db.jooq.lending.tables.Loan.LOAN;
import static fintech.bo.db.jooq.alfa.tables.LoanRescheduling.LOAN_RESCHEDULING;

@Component
public class AlfaReschedulingComponents extends ReschedulingComponents {

    protected AlfaReschedulingComponents(DSLContext db, ReschedulingQueries reschedulingQueries, JooqClientDataService jooqClientDataService) {
        super(db, reschedulingQueries, jooqClientDataService);
    }

    @Autowired
    protected AlfaApiClient alfaApiClient;

    @Override
    protected void addGridColumns(JooqGridBuilder<Record> builder) {
        builder.addNavigationColumn("Open", r -> "loan/" + r.get(LOAN_RESCHEDULING.LOAN_ID));
        builder.addColumn(LOAN_RESCHEDULING.ID);
        builder.addColumn(LOAN.LOAN_NUMBER);
        builder.addColumn(LOAN_RESCHEDULING.STATUS).setStyleGenerator(reschedulingStatusStyle());
        builder.addColumn(LOAN_RESCHEDULING.RESCHEDULE_DATE);
        builder.addColumn(LOAN_RESCHEDULING.EXPIRE_DATE);
        builder.addColumn(LOAN_RESCHEDULING.NUMBER_OF_PAYMENTS);
        builder.addColumn(LOAN_RESCHEDULING.INSTALLMENT_AMOUNT);
        builder.addColumn(LOAN.OVERDUE_DAYS);
        builder.addColumn(LOAN.ISSUE_DATE);
        builder.addColumn(LOAN.PAYMENT_DUE_DATE);
        builder.addColumn(LOAN.CLOSE_DATE);
        builder.addColumn(LOAN.TOTAL_DUE);
        builder.addColumn(LOAN.TOTAL_OUTSTANDING);
    }


}
