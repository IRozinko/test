package fintech.bo.components.dowjones;

import com.vaadin.data.provider.Query;
import fintech.bo.components.JooqDataProvider;
import fintech.bo.components.views.BoComponentContext;
import fintech.bo.components.views.StandardScopes;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.jooq.DSLContext;
import org.jooq.Record;
import org.jooq.SelectWhereStep;

import static fintech.bo.db.jooq.dowjones.Tables.REQUEST;

@Setter
@Accessors(chain = true)
public class DowJonesDataProvider extends JooqDataProvider<Record> {

    private BoComponentContext componentContext = new BoComponentContext();

    public DowJonesDataProvider(DSLContext db) {
        super(db);
    }

    @Override
    protected SelectWhereStep<Record> buildSelect(Query<Record, String> query) {
        SelectWhereStep<Record> select = db.select(fields(REQUEST.fields())).from(REQUEST);
        componentContext.scope(StandardScopes.SCOPE_CLIENT).ifPresent(id -> select.where(REQUEST.CLIENT_ID.eq(id)));
        return select;
    }

    @Override
    protected Object id(Record item) {
        return item.get(REQUEST.ID);
    }
}
