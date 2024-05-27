package fintech.bo.components.iovation;

import com.vaadin.data.provider.Query;
import fintech.bo.components.JooqDataProvider;
import fintech.bo.components.views.BoComponentContext;
import fintech.bo.components.views.StandardScopes;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.jooq.DSLContext;
import org.jooq.Record;
import org.jooq.SelectWhereStep;

import static fintech.bo.db.jooq.iovation.Tables.BLACKBOX;

@Setter
@Accessors(chain = true)
public class IovationBlackBoxDataProvider extends JooqDataProvider<Record> {

    private BoComponentContext componentContext = new BoComponentContext();

    public IovationBlackBoxDataProvider(DSLContext db) {
        super(db);
    }

    @Override
    protected SelectWhereStep<Record> buildSelect(Query<Record, String> query) {
        SelectWhereStep<Record> select = db.select(fields(BLACKBOX.fields())).from(BLACKBOX);
        componentContext.scope(StandardScopes.SCOPE_CLIENT).ifPresent(id -> select.where(BLACKBOX.CLIENT_ID.eq(id)));
        return select;
    }

    @Override
    protected Object id(Record item) {
        return item.get(BLACKBOX.ID);
    }
}
