package fintech.bo.components.callcenter;

import com.vaadin.data.provider.Query;
import fintech.bo.components.JooqDataProvider;
import fintech.bo.components.views.BoComponentContext;
import fintech.bo.components.views.StandardScopes;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.jooq.DSLContext;
import org.jooq.Record;
import org.jooq.SelectWhereStep;

import static fintech.bo.db.jooq.callcenter.tables.Call.CALL;
import static fintech.bo.db.jooq.crm.Tables.CLIENT;
import static fintech.bo.db.jooq.presence.Tables.OUTBOUND_LOAD_RECORD;

@Setter
@Accessors(chain = true)
public class CallCenterLoadsDataProvider extends JooqDataProvider<Record> {

    private BoComponentContext componentContext = new BoComponentContext();

    public CallCenterLoadsDataProvider(DSLContext db) {
        super(db);
    }

    @Override
    protected SelectWhereStep<Record> buildSelect(Query<Record, String> query) {
        SelectWhereStep<Record> select = db.select(fields(
            OUTBOUND_LOAD_RECORD.fields(),
            CALL.ID.as("call_id"),
            CALL.CLIENT_ID,
            CALL.STATUS.as("call_status"),
            CALL.CREATED_AT.as("call_created_at"),
            CALL.CREATED_BY.as("call_created_by"),
            CALL.UPDATED_AT.as("call_updated_at"),
            CALL.UPDATED_BY.as("call_updated_by"),
            CLIENT.CLIENT_NUMBER
        ))
            .from(OUTBOUND_LOAD_RECORD)
            .leftJoin(CALL).on(CALL.PROVIDER_ID.eq(OUTBOUND_LOAD_RECORD.ID))
            .leftJoin(CLIENT).on(CALL.CLIENT_ID.eq(CLIENT.ID));
        componentContext.scope(StandardScopes.SCOPE_CLIENT).ifPresent(id -> select.where(CALL.CLIENT_ID.eq(id)));
        return select;
    }

    @Override
    protected Object id(Record item) {
        return item.get(CALL.ID);
    }
}
