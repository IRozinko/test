package fintech.bo.components.loan.promocodes;

import com.vaadin.data.provider.Query;
import fintech.bo.components.JooqDataProvider;
import fintech.bo.components.client.dto.ClientDTO;
import lombok.Setter;
import org.jooq.DSLContext;
import org.jooq.Record;
import org.jooq.SelectJoinStep;
import org.jooq.SelectWhereStep;

import static fintech.bo.db.jooq.crm.Tables.CLIENT;
import static fintech.bo.db.jooq.lending.Tables.PROMO_CODE_CLIENT;
import static fintech.bo.db.jooq.lending.tables.PromoCode.PROMO_CODE;

@Setter
public class ClientPromoCodesDataProvider extends JooqDataProvider<Record> {

    private final PromoCodeQueries promoCodeQueries;

    private ClientDTO client;

    ClientPromoCodesDataProvider(DSLContext db, ClientDTO client, PromoCodeQueries promoCodeQueries) {
        super(db);
        this.promoCodeQueries = promoCodeQueries;
        this.client = client;
    }

    @Override
    protected SelectWhereStep<Record> buildSelect(Query<Record, String> query) {
        SelectJoinStep<Record> select = db.select(fields(PROMO_CODE.fields(), promoCodeQueries.redeemedField()))
            .from(PROMO_CODE_CLIENT)
            .join(CLIENT).on(CLIENT.CLIENT_NUMBER.eq(PROMO_CODE_CLIENT.CLIENT_NUMBER))
            .join(PROMO_CODE).on(PROMO_CODE.ID.eq(PROMO_CODE_CLIENT.PROMO_CODE_ID));
        select.where(CLIENT.ID.eq(client.getId()));
        return select;
    }

    @Override
    protected Object id(Record item) {
        return item.get(PROMO_CODE.ID);
    }
}
