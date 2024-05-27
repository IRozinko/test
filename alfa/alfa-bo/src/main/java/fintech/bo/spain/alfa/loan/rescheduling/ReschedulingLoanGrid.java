package fintech.bo.spain.alfa.loan.rescheduling;

import fintech.bo.components.GridHelper;
import fintech.bo.components.jooq.JooqGrid;
import fintech.bo.spain.alfa.db.jooq.alfa.tables.records.LoanReschedulingRecord;

import static fintech.bo.components.jooq.JooqGridSortOrder.desc;
import static fintech.bo.spain.alfa.db.jooq.alfa.tables.LoanRescheduling.LOAN_RESCHEDULING;

public class ReschedulingLoanGrid extends JooqGrid<LoanReschedulingRecord> {

    public ReschedulingLoanGrid(ReschedulingLoanGridDataProvider dataProvider) {
        addColumn(date(LOAN_RESCHEDULING.EXPIRE_DATE));
        addColumn(text(LOAN_RESCHEDULING.STATUS));
        addColumn(text(LOAN_RESCHEDULING.GRACE_PERIOD_DAYS));
        addColumn(text(LOAN_RESCHEDULING.INSTALLMENT_AMOUNT));
        addColumn(text(LOAN_RESCHEDULING.REPAYMENT_DUE_DAYS));
        addColumn(date(LOAN_RESCHEDULING.RESCHEDULE_DATE));
        addColumn(text(LOAN_RESCHEDULING.NUMBER_OF_PAYMENTS));
        addColumn(dateTime(LOAN_RESCHEDULING.CREATED_AT));
        addColumn(text(LOAN_RESCHEDULING.CREATED_BY));
        addColumn(dateTime(LOAN_RESCHEDULING.UPDATED_AT));
        addColumn(text(LOAN_RESCHEDULING.UPDATED_BY));
        addCreatedCols(LOAN_RESCHEDULING);

        setSortOrder(desc(LOAN_RESCHEDULING.CREATED_AT));

        setDataProvider(dataProvider);
        dataProvider.addSizeListener(this::totalCountAsCaption);

        GridHelper.addTotalCountAsCaption(this, dataProvider);

        tuneGrid();
    }

}
