package fintech.bo.components.payments.statement;

import com.vaadin.data.provider.Query;
import fintech.bo.components.JooqDataProvider;
import org.jooq.DSLContext;
import org.jooq.Field;
import org.jooq.Record;
import org.jooq.SelectWhereStep;
import org.jooq.impl.DSL;

import static fintech.bo.db.jooq.payment.tables.Institution.INSTITUTION;
import static fintech.bo.db.jooq.payment.tables.Statement.STATEMENT;
import static fintech.bo.db.jooq.payment.tables.StatementRow.STATEMENT_ROW;
import static java.util.Arrays.asList;

public class StatementDataProvider extends JooqDataProvider<Record> {

    public static final Field<Integer> TOTAL_COUNT = DSL.select(DSL.coalesce(DSL.count(), 0))
        .from(STATEMENT_ROW)
        .where(STATEMENT_ROW.STATEMENT_ID.eq(STATEMENT.ID))
        .groupBy(STATEMENT_ROW.STATEMENT_ID)
        .asField("total_count");

    public static final Field<Integer> PROCESSED_COUNT = DSL.select(DSL.coalesce(DSL.count(), 0))
        .from(STATEMENT_ROW)
        .where(STATEMENT_ROW.STATEMENT_ID.eq(STATEMENT.ID).and(STATEMENT_ROW.STATUS.eq(StatementConstants.ROW_STATUS_PROCESSED)))
        .groupBy(STATEMENT_ROW.STATEMENT_ID)
        .asField("processed_count");

    public static final Field<Integer> IGNORED_COUNT = DSL.select(DSL.coalesce(DSL.count(), 0))
        .from(STATEMENT_ROW)
        .where(STATEMENT_ROW.STATEMENT_ID.eq(STATEMENT.ID).and(STATEMENT_ROW.STATUS.eq(StatementConstants.ROW_STATUS_IGNORED)))
        .groupBy(STATEMENT_ROW.STATEMENT_ID)
        .asField("ignored_count");

    public static final Field<Integer> ERROR_COUNT = DSL.select(DSL.coalesce(DSL.count(), 0))
        .from(STATEMENT_ROW)
        .where(STATEMENT_ROW.STATEMENT_ID.eq(STATEMENT.ID).and(STATEMENT_ROW.STATUS.eq(StatementConstants.ROW_STATUS_ERROR)))
        .groupBy(STATEMENT_ROW.STATEMENT_ID)
        .asField("error_count");

    public StatementDataProvider(DSLContext db) {
        super(db);
    }

    @Override
    protected SelectWhereStep<Record> buildSelect(Query<Record, String> query) {
        SelectWhereStep<Record> select = db
            .select(fields(
                STATEMENT.fields(),
                INSTITUTION.NAME,
                TOTAL_COUNT,
                PROCESSED_COUNT,
                IGNORED_COUNT,
                ERROR_COUNT))
            .from(STATEMENT)
            .join(INSTITUTION).on(STATEMENT.INSTITUTION_ID.eq(INSTITUTION.ID));

        return select;
    }

    @Override
    protected Object id(Record item) {
        return item.get(STATEMENT.ID);
    }

}
