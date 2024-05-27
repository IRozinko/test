package fintech.bo.components.loan;

import com.vaadin.data.provider.Query;
import fintech.bo.components.JooqDataProvider;
import lombok.extern.slf4j.Slf4j;
import org.jooq.DSLContext;
import org.jooq.Record;
import org.jooq.SelectWhereStep;

import static fintech.bo.db.jooq.lending.Tables.SCHEDULE;


@Slf4j
public class ScheduleDataProvider extends JooqDataProvider<Record> {

    private Long loanId;

    public ScheduleDataProvider(DSLContext db) {
        super(db);
    }

    @Override
    protected SelectWhereStep<Record> buildSelect(Query<Record, String> query) {
        SelectWhereStep<Record> select = db
            .select(fields(SCHEDULE.fields()))
            .from(SCHEDULE);

        if (loanId != null) {
            select.where(SCHEDULE.LOAN_ID.eq(loanId));
        }
        return select;
    }


    @Override
    protected Object id(Record item) {
        return item.get(SCHEDULE.ID);
    }


    public void setLoanId(Long loanId) {
        this.loanId = loanId;
    }
}
