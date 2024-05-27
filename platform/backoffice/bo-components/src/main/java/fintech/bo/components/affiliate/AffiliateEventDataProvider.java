package fintech.bo.components.affiliate;

import com.vaadin.data.provider.Query;
import fintech.bo.components.JooqDataProvider;
import fintech.bo.components.views.BoComponentContext;
import fintech.bo.components.views.StandardScopes;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.jooq.DSLContext;
import org.jooq.Record;
import org.jooq.SelectWhereStep;

import static fintech.bo.db.jooq.affiliate.Tables.EVENT;
import static fintech.bo.db.jooq.affiliate.Tables.PARTNER;

@Setter
@Accessors(chain = true)
public class AffiliateEventDataProvider extends JooqDataProvider<Record> {

    private BoComponentContext componentContext = new BoComponentContext();

    public AffiliateEventDataProvider(DSLContext db) {
        super(db);
    }

    @Override
    protected SelectWhereStep<Record> buildSelect(Query<Record, String> query) {
        SelectWhereStep<Record> select = db.select(fields(
            EVENT.fields(),
            PARTNER.NAME
        )).from(EVENT).join(PARTNER).on(EVENT.PARTNER_ID.eq(PARTNER.ID));
        componentContext.scope(StandardScopes.SCOPE_CLIENT).ifPresent(id -> select.where(EVENT.CLIENT_ID.eq(id)));
        return select;
    }

    @Override
    protected Object id(Record item) {
        return item.get(EVENT.ID);
    }
}
