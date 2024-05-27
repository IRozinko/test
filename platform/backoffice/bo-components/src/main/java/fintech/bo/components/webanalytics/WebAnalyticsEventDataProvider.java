package fintech.bo.components.webanalytics;

import com.vaadin.data.provider.Query;
import fintech.bo.components.JooqDataProvider;
import fintech.bo.components.views.BoComponentContext;
import fintech.bo.components.views.StandardScopes;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.jooq.DSLContext;
import org.jooq.Record;
import org.jooq.SelectWhereStep;

import static fintech.bo.db.jooq.web_analytics.Tables.EVENT;

@Setter
@Accessors(chain = true)
public class WebAnalyticsEventDataProvider extends JooqDataProvider<Record> {

    private BoComponentContext componentContext = new BoComponentContext();

    public WebAnalyticsEventDataProvider(DSLContext db) {
        super(db);
    }

    @Override
    protected SelectWhereStep<Record> buildSelect(Query<Record, String> query) {
        SelectWhereStep<Record> select = db.select(fields(EVENT.fields())).from(EVENT);
        componentContext.scope(StandardScopes.SCOPE_CLIENT).ifPresent(id -> select.where(EVENT.CLIENT_ID.eq(id)));
        return select;
    }

    @Override
    protected Object id(Record item) {
        return item.get(EVENT.ID);
    }
}
