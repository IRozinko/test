package fintech.bo.components.sms;

import com.vaadin.data.provider.Query;
import fintech.bo.components.JooqDataProvider;
import fintech.bo.db.jooq.sms.Tables;
import fintech.bo.db.jooq.sms.tables.records.IncomingRecord;
import lombok.extern.slf4j.Slf4j;
import org.jooq.DSLContext;
import org.jooq.SelectWhereStep;


@Slf4j
public class IncomingSmsDataProvider extends JooqDataProvider<IncomingRecord> {

    public IncomingSmsDataProvider(DSLContext db) {
        super(db);
    }

    @Override
    protected SelectWhereStep<IncomingRecord> buildSelect(Query<IncomingRecord, String> query) {
        SelectWhereStep<IncomingRecord> select = db.selectFrom(Tables.INCOMING);
        return select;
    }

    @Override
    protected Object id(IncomingRecord item) {
        return item.getId();
    }


}
