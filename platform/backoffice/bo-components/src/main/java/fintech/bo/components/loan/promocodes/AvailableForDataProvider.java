package fintech.bo.components.loan.promocodes;

import com.vaadin.data.provider.Query;
import fintech.bo.components.JooqDataProvider;
import lombok.Setter;
import org.jooq.DSLContext;
import org.jooq.Field;
import org.jooq.Record;
import org.jooq.SelectWhereStep;

import static fintech.bo.db.jooq.crm.Tables.CLIENT;
import static fintech.bo.db.jooq.lending.Tables.PROMO_CODE_CLIENT;

@Setter
public class AvailableForDataProvider extends JooqDataProvider<Record> {

    private Long promoCodeId;

    AvailableForDataProvider(DSLContext db) {
        super(db);
    }

    @Override
    protected SelectWhereStep<Record> buildSelect(Query<Record, String> query) {
        Field[] clientFields = {CLIENT.ID, CLIENT.CLIENT_NUMBER, CLIENT.FIRST_NAME, CLIENT.LAST_NAME};
        SelectWhereStep<Record> select = db.select(fields(clientFields))
            .from(PROMO_CODE_CLIENT).join(CLIENT).on(PROMO_CODE_CLIENT.CLIENT_NUMBER.eq(CLIENT.CLIENT_NUMBER));

        select.where(PROMO_CODE_CLIENT.PROMO_CODE_ID.eq(promoCodeId));

        select.orderBy(PROMO_CODE_CLIENT.CREATED_AT);
        return select;
    }

    @Override
    protected Object id(Record item) {
        return item.get(PROMO_CODE_CLIENT.ID);
    }
}
