package fintech.bo.components.dc;

import com.vaadin.data.provider.Query;
import fintech.bo.components.JooqDataProvider;
import org.jooq.DSLContext;
import org.jooq.Record;
import org.jooq.SelectWhereStep;

import static fintech.bo.db.jooq.dc.Tables.DEBT_IMPORT;


public class DebtImportDataProvider extends JooqDataProvider<Record> {



    public DebtImportDataProvider(DSLContext db) {
        super(db);
    }

    @Override
    protected SelectWhereStep<Record> buildSelect(Query<Record, String> query) {
        return db
            .select(fields(
                DEBT_IMPORT.fields()))
            .from(DEBT_IMPORT);
    }

    @Override
    protected Object id(Record item) {
        return item.get(DEBT_IMPORT.ID);
    }

}
