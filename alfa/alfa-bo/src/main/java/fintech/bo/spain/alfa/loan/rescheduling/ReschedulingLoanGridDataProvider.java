package fintech.bo.spain.alfa.loan.rescheduling;

import com.vaadin.data.provider.Query;
import fintech.bo.components.JooqDataProvider;
import fintech.bo.spain.alfa.db.jooq.alfa.tables.records.LoanReschedulingRecord;
import org.jooq.DSLContext;
import org.jooq.SelectWhereStep;

import static fintech.bo.spain.alfa.db.jooq.alfa.tables.LoanRescheduling.LOAN_RESCHEDULING;


public class ReschedulingLoanGridDataProvider extends JooqDataProvider<LoanReschedulingRecord> {

    private final long loanId;

    public ReschedulingLoanGridDataProvider(long loanId, DSLContext db) {
        super(db);
        this.loanId = loanId;
    }

    @Override
    protected SelectWhereStep<LoanReschedulingRecord> buildSelect(Query<LoanReschedulingRecord, String> query) {
        SelectWhereStep<LoanReschedulingRecord> select = db
            .selectFrom(LOAN_RESCHEDULING);
        select.where(LOAN_RESCHEDULING.LOAN_ID.eq(loanId));
        select.orderBy(LOAN_RESCHEDULING.CREATED_AT.desc());
        return select;
    }

    @Override
    protected Object id(LoanReschedulingRecord item) {
        return item.getId();
    }
}
