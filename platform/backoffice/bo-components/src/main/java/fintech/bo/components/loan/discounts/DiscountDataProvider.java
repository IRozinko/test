package fintech.bo.components.loan.discounts;

import com.vaadin.data.provider.Query;
import fintech.bo.components.client.JooqClientDataProvider;
import fintech.bo.components.client.JooqClientDataService;
import org.jooq.DSLContext;
import org.jooq.Record;
import org.jooq.SelectWhereStep;

import java.util.Optional;

import static fintech.bo.components.client.ClientDataProviderUtils.FIELD_CLIENT_NAME;
import static fintech.bo.db.jooq.crm.tables.Client.CLIENT;
import static fintech.bo.db.jooq.lending.tables.Discount.DISCOUNT;

public class DiscountDataProvider extends JooqClientDataProvider<Record> {

    private Long clientId;

    public DiscountDataProvider(DSLContext db, JooqClientDataService jooqClientDataService) {
        super(db, jooqClientDataService);
    }

    @Override
    protected SelectWhereStep<Record> buildSelect(Query<Record, String> query) {
        SelectWhereStep<Record> select = db
            .select(fields(DISCOUNT.fields(), FIELD_CLIENT_NAME))
            .from(DISCOUNT)
            .join(CLIENT).on(DISCOUNT.CLIENT_ID.eq(CLIENT.ID));

        Optional.ofNullable(clientId).ifPresent(c -> select.where(DISCOUNT.CLIENT_ID.eq(c)));

        return select;
    }

    @Override
    protected Object id(Record item) {
        return item.get(DISCOUNT.ID);
    }

    public void setClientId(Long clientId) {
        this.clientId = clientId;
    }
}
