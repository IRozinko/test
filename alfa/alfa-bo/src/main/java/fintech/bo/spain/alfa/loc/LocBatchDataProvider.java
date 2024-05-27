package fintech.bo.spain.alfa.loc;

import com.vaadin.data.provider.AbstractBackEndDataProvider;
import com.vaadin.data.provider.Query;
import org.jooq.DSLContext;
import org.jooq.Field;
import org.jooq.Record;
import org.jooq.SelectHavingStep;
import org.jooq.impl.DSL;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.stream.Stream;

import static fintech.bo.db.jooq.alfa.tables.LocBatch.LOC_BATCH;
import static org.jooq.impl.DSL.count;

public class LocBatchDataProvider extends AbstractBackEndDataProvider<Record, String> {

    public static final Field<Integer> FIELD_STARTED
        = count(DSL.when(LOC_BATCH.STATUS.eq("STARTED"), 1)).as("started");

    public static final Field<Integer> FIELD_TOTAL
        = count(LOC_BATCH.ID).as("total");

    public static final Field<Integer> FIELD_WAITING
        = count(DSL.when(LOC_BATCH.STATUS.eq("WAITING"), 1) ).as("waiting");

    public static final Field<Integer> FIELD_PENDING
        = count(DSL.when(LOC_BATCH.STATUS.eq("PENDING"), 1) ).as("pending");

    public static final Field<Integer> FIELD_COMPLETED
        = count(DSL.when(LOC_BATCH.STATUS.eq("COMPLETED"), 1) ).as("completed");

    public static final Field<Integer> FIELD_FAILED
        = count(DSL.when(LOC_BATCH.STATUS.eq("FAILED"), 1) ).as("failed");

    public static final Field<LocalDateTime> FIELD_CREATED_AT = DSL.min(LOC_BATCH.CREATED_AT).as("createdAt");

    private final DSLContext db;

    public LocBatchDataProvider(DSLContext db) {
        this.db = db;
    }

    @Override
    protected Stream<Record> fetchFromBackEnd(Query<Record, String> query) {
        return getSelect().fetchStream();
    }

    @Override
    protected int sizeInBackEnd(Query<Record, String> query) {
        return db.fetchCount(getSelect());
    }

    private SelectHavingStep<Record> getSelect() {
        SelectHavingStep<Record> select = db.select(Arrays.asList(LOC_BATCH.BATCH_NUMBER,
            FIELD_STARTED,
            FIELD_TOTAL,
            FIELD_WAITING,
            FIELD_PENDING,
            FIELD_COMPLETED,
            FIELD_FAILED,
            FIELD_CREATED_AT)
        ).from(LOC_BATCH)
            .groupBy(LOC_BATCH.BATCH_NUMBER);

        select.orderBy(FIELD_CREATED_AT.desc());
        return select;
    }
}
