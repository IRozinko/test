package fintech.bo.spain.unnax;

import com.vaadin.data.provider.Query;
import fintech.bo.components.client.JooqClientDataProvider;
import fintech.bo.components.client.JooqClientDataService;
import org.jooq.DSLContext;
import org.jooq.Record;
import org.jooq.SelectWhereStep;

import java.util.Optional;

import static fintech.bo.db.jooq.crm.tables.Client.CLIENT;
import static fintech.spain.unnax.db.jooq.tables.CreditCard.CREDIT_CARD;

public class CreditCardDataProvider extends JooqClientDataProvider<Record> {

    private String clientNumber;

    public CreditCardDataProvider(DSLContext db, JooqClientDataService jooqClientDataService) {
        super(db, jooqClientDataService);
    }

    @Override
    protected SelectWhereStep<Record> buildSelect(Query<Record, String> query) {
        SelectWhereStep<Record> select = db
            .select(fields(CREDIT_CARD.fields()))
            .from(CREDIT_CARD)
            .join(CLIENT).on(CREDIT_CARD.CLIENT_NUMBER.eq(CLIENT.CLIENT_NUMBER));

        Optional.ofNullable(clientNumber).ifPresent(c -> select.where(CREDIT_CARD.CLIENT_NUMBER.eq(c)));

        return select;
    }

    @Override
    protected Object id(Record item) {
        return item.get(CREDIT_CARD.ID);
    }

    public void setClientId(String clientNumber) {
        this.clientNumber = clientNumber;
    }
}
