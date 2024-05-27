package fintech.bo.spain.alfa.loc;

import com.vaadin.data.provider.Query;
import fintech.bo.components.JooqDataProvider;
import org.jooq.DSLContext;
import org.jooq.Record;
import org.jooq.SelectJoinStep;
import org.jooq.SelectWhereStep;

import static fintech.bo.db.jooq.alfa.tables.LocBatch.LOC_BATCH;

public class LocClientsBatchDataProvider extends JooqDataProvider<Record> {

    private Long batchNumber;

    public LocClientsBatchDataProvider(DSLContext db) {
        super(db);
    }

    @Override
    protected SelectWhereStep<Record> buildSelect(Query<Record, String> query) {
        SelectJoinStep<Record> select = db.select()
            .from(LOC_BATCH);

        if (batchNumber != null)
            select.where(LOC_BATCH.BATCH_NUMBER.eq(batchNumber));

        return select;
    }

    @Override
    protected Object id(Record item) {
        return item.get(LOC_BATCH.ID);
    }

    public void setBatchNumber(Long batchNumber) {
        this.batchNumber = batchNumber;
    }
}
