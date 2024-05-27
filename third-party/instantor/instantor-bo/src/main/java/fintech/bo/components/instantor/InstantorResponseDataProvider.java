package fintech.bo.components.instantor;

import com.vaadin.data.provider.Query;
import fintech.bo.components.JooqDataProvider;
import fintech.bo.components.views.BoComponentContext;
import fintech.bo.components.views.StandardScopes;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.jooq.DSLContext;
import org.jooq.Record;
import org.jooq.SelectWhereStep;

import static fintech.bo.db.jooq.crm.tables.Client.CLIENT;
import static fintech.bo.db.jooq.instantor.Tables.RESPONSE;

@Setter
@Accessors(chain = true)
public class InstantorResponseDataProvider extends JooqDataProvider<Record> {

    private BoComponentContext componentContext = new BoComponentContext();

    public InstantorResponseDataProvider(DSLContext db) {
        super(db);
    }

    @Override
    protected SelectWhereStep<Record> buildSelect(Query<Record, String> query) {
        SelectWhereStep<Record> select = db.select(RESPONSE.fields()).select(CLIENT.CLIENT_NUMBER, CLIENT.DELETED)
            .from(RESPONSE)
            .join(CLIENT).on(RESPONSE.CLIENT_ID.eq(CLIENT.ID));
        componentContext.scope(StandardScopes.SCOPE_CLIENT).ifPresent(id -> select.where(RESPONSE.CLIENT_ID.eq(id)));
        return select;
    }

    @Override
    protected Object id(Record item) {
        return item.get(RESPONSE.ID);
    }
}
