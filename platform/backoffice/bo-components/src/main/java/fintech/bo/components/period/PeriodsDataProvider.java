package fintech.bo.components.period;

import com.vaadin.data.provider.Query;
import fintech.TimeMachine;
import fintech.bo.components.JooqDataProvider;
import lombok.extern.slf4j.Slf4j;
import org.jooq.DSLContext;
import org.jooq.Record;
import org.jooq.SelectWhereStep;

import static fintech.bo.db.jooq.lending.tables.Period.PERIOD;
import static java.util.Arrays.asList;


@Slf4j
public class PeriodsDataProvider extends JooqDataProvider<Record> {

    public PeriodsDataProvider(DSLContext db) {
        super(db);
    }

    @Override
    protected Object id(Record item) {
        return item.get(PERIOD.ID);
    }

    @Override
    protected SelectWhereStep<Record> buildSelect(Query query) {
        SelectWhereStep<Record> select = db
            .select(asList(
                PERIOD.ID,
                PERIOD.PERIOD_DATE,
                PERIOD.CLOSE_DATE,
                PERIOD.STATUS,
                PERIOD.STATUS_DETAIL,
                PERIOD.RESULT_LOG,
                PERIOD.CLOSING_STARTED_AT,
                PERIOD.CLOSING_ENDED_AT,
                PERIOD.CREATED_AT,
                PERIOD.CREATED_BY
            )).from(PERIOD);

        select.where(PERIOD.PERIOD_DATE.lessThan(TimeMachine.today()));

        return select;
    }

}

