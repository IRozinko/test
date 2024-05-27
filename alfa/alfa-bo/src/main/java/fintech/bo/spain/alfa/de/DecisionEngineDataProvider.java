package fintech.bo.spain.alfa.de;

import com.vaadin.data.provider.Query;
import fintech.bo.components.JooqDataProvider;
import fintech.bo.components.views.BoComponentContext;
import fintech.bo.components.views.StandardScopes;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.jooq.DSLContext;
import org.jooq.Record;
import org.jooq.SelectWhereStep;

import static fintech.bo.db.jooq.decision_engine.DecisionEngine.DECISION_ENGINE;

@Setter
@Accessors(chain = true)
public class DecisionEngineDataProvider extends JooqDataProvider<Record> {

    private BoComponentContext componentContext = new BoComponentContext();

    public DecisionEngineDataProvider(DSLContext db) {
        super(db);
    }

    @Override
    protected SelectWhereStep<Record> buildSelect(Query<Record, String> query) {
        SelectWhereStep<Record> select = db.select(fields(DECISION_ENGINE.REQUEST.fields())).from(DECISION_ENGINE.REQUEST);
        componentContext.scope(StandardScopes.SCOPE_CLIENT).ifPresent(id -> select.where(DECISION_ENGINE.REQUEST.CLIENT_ID.eq(id)));
        return select;
    }

    @Override
    protected Object id(Record item) {
        return item.get(DECISION_ENGINE.REQUEST.ID);
    }
}
