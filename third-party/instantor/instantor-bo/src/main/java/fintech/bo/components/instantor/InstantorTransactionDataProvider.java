package fintech.bo.components.instantor;

import com.vaadin.data.provider.Query;
import fintech.bo.components.JooqDataProvider;
import fintech.bo.components.views.BoComponentContext;
import fintech.bo.components.views.StandardScopes;
import fintech.bo.db.jooq.instantor.tables.records.TransactionRecord;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.jooq.DSLContext;
import org.jooq.SelectWhereStep;

import static fintech.bo.db.jooq.instantor.Tables.TRANSACTION;

@Setter
@Accessors(chain = true)
public class InstantorTransactionDataProvider extends JooqDataProvider<TransactionRecord> {

    private BoComponentContext componentContext = new BoComponentContext();

    private Long responseId;

    public InstantorTransactionDataProvider(DSLContext db) {
        super(db);
    }

    @Override
    protected SelectWhereStep<TransactionRecord> buildSelect(Query<TransactionRecord, String> query) {
        SelectWhereStep<TransactionRecord> select = db.selectFrom(TRANSACTION);
        componentContext.scope(StandardScopes.SCOPE_CLIENT).ifPresent(id -> select.where(TRANSACTION.CLIENT_ID.eq(id)));
        if (responseId != null) {
            select.where(TRANSACTION.RESPONSE_ID.eq(responseId));
        }
        return select;
    }

    @Override
    protected Object id(TransactionRecord item) {
        return item.getId();
    }
}
