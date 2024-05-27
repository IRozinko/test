package fintech.bo.spain.payxpert;

import com.vaadin.data.provider.Query;
import fintech.bo.components.JooqDataProvider;
import fintech.bo.components.views.BoComponentContext;
import fintech.bo.components.views.StandardScopes;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.jooq.DSLContext;
import org.jooq.Record;
import org.jooq.SelectWhereStep;

import static fintech.bo.spain.db.jooq.payxpert.Tables.CREDIT_CARD;

@Setter
@Accessors(chain = true)
public class PayxpertCreditCardDataProvider extends JooqDataProvider<Record> {

    private BoComponentContext componentContext = new BoComponentContext();

    public PayxpertCreditCardDataProvider(DSLContext db) {
        super(db);
    }

    @Override
    protected SelectWhereStep<Record> buildSelect(Query<Record, String> query) {
        SelectWhereStep<Record> select = db
            .select(fields(CREDIT_CARD.fields()))
            .from(CREDIT_CARD);
        componentContext.scope(StandardScopes.SCOPE_CLIENT).ifPresent(id -> select.where(CREDIT_CARD.CLIENT_ID.eq(id)));
        return select;
    }

    @Override
    protected Object id(Record item) {
        return item.get(CREDIT_CARD.ID);
    }
}
