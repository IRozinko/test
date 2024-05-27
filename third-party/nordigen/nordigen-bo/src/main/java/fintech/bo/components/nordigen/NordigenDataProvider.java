package fintech.bo.components.nordigen;

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
import static fintech.bo.db.jooq.nordigen.Tables.LOG;

@Setter
@Accessors(chain = true)
public class NordigenDataProvider extends JooqDataProvider<Record> {

    private BoComponentContext componentContext = new BoComponentContext();

    public NordigenDataProvider(DSLContext db) {
        super(db);
    }

    @Override
    protected SelectWhereStep<Record> buildSelect(Query<Record, String> query) {
        SelectWhereStep<Record> select = db.select(LOG.fields()).select(CLIENT.CLIENT_NUMBER, CLIENT.DELETED)
            .from(LOG).join(CLIENT).on(LOG.CLIENT_ID.eq(CLIENT.ID));
        componentContext.scope(StandardScopes.SCOPE_CLIENT).ifPresent(id -> select.where(LOG.CLIENT_ID.eq(id)));
        return select;
    }

    @Override
    protected Object id(Record item) {
        return item.get(LOG.ID);
    }
}
