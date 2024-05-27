package fintech.bo.components.dowjones;

import com.vaadin.data.provider.Query;
import fintech.bo.components.JooqDataProvider;
import fintech.bo.components.views.BoComponentContext;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.jooq.DSLContext;
import org.jooq.Record;
import org.jooq.SelectWhereStep;

import static fintech.bo.components.views.StandardScopes.SCOPE_CLIENT;
import static fintech.bo.db.jooq.dowjones.tables.Match.MATCH;

@Setter
@Accessors(chain = true)
public class DowJonesMatchResultDataProvider extends JooqDataProvider<Record> {

    private BoComponentContext componentContext = new BoComponentContext();

    public DowJonesMatchResultDataProvider(DSLContext db) {
        super(db);
    }

    @Override
    protected SelectWhereStep<Record> buildSelect(Query<Record, String> query) {
        SelectWhereStep<Record> select = db.select(fields(MATCH.fields())).from(MATCH);
        componentContext.scope(SCOPE_CLIENT).ifPresent(id -> select.where(MATCH.SEARCH_RESULT_ID.eq(id)));
        return select;
    }

    @Override
    protected Object id(Record item) {
        return item.get(MATCH.ID);
    }
}
