package fintech.bo.components.callcenter;

import com.vaadin.data.provider.Query;
import fintech.bo.components.JooqDataProvider;
import fintech.bo.db.jooq.presence.tables.records.OutboundLoadRecordRecord;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.jooq.DSLContext;
import org.jooq.SelectWhereStep;

import static fintech.bo.db.jooq.presence.Tables.OUTBOUND_LOAD_RECORD;

@Setter
@Accessors(chain = true)
public class PresenceOutboundLoadRecordsDataProvider extends JooqDataProvider<OutboundLoadRecordRecord> {

    public PresenceOutboundLoadRecordsDataProvider(DSLContext db) {
        super(db);
    }

    @Override
    protected SelectWhereStep<OutboundLoadRecordRecord> buildSelect(Query<OutboundLoadRecordRecord, String> query) {
        return db.selectFrom(OUTBOUND_LOAD_RECORD);
    }

    @Override
    protected Object id(OutboundLoadRecordRecord item) {
        return item.getId();
    }
}
