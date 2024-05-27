package fintech.bo.components.loan.promocodes;

import com.vaadin.data.provider.Query;
import fintech.bo.components.JooqDataProvider;
import fintech.bo.components.client.dto.ClientDTO;
import lombok.Setter;
import org.jooq.DSLContext;
import org.jooq.Record;
import org.jooq.SelectWhereStep;

import java.time.LocalDate;

import static fintech.bo.db.jooq.lending.Tables.PROMO_CODE_CLIENT;
import static fintech.bo.db.jooq.lending.Tables.PROMO_CODE_SOURCE;
import static fintech.bo.db.jooq.lending.tables.PromoCode.PROMO_CODE;
import static org.jooq.impl.DSL.selectFrom;

@Setter
public class PromoCodesDataProvider extends JooqDataProvider<Record> {

    private final PromoCodeQueries promoCodeQueries;

    private String code;
    private LocalDate effectiveFrom;
    private LocalDate effectiveTo;
    private String codeType;
    private String source;
    private ClientDTO client;

    PromoCodesDataProvider(DSLContext db, PromoCodeQueries promoCodeQueries) {
        super(db);
        this.promoCodeQueries = promoCodeQueries;
    }

    @Override
    protected SelectWhereStep<Record> buildSelect(Query<Record, String> query) {
        SelectWhereStep<Record> select = db.select(fields(PROMO_CODE.fields(), promoCodeQueries.timesUsedField(), promoCodeQueries.sourcesField()))
            .from(PROMO_CODE);

        if (code != null && !code.isEmpty()) {
            select.where(PROMO_CODE.CODE.likeIgnoreCase("%" + code + "%"));
        }

        if (effectiveFrom != null) {
            select.where(PROMO_CODE.EFFECTIVE_FROM.greaterOrEqual(effectiveFrom));
        }

        if (effectiveTo != null) {
            select.where(PROMO_CODE.EFFECTIVE_TO.greaterOrEqual(effectiveTo));
        }

        if (PromoCodeType.TYPE_NEW_CLIENTS.equals(codeType)) {
            select.where(PROMO_CODE.NEW_CLIENTS_ONLY.eq(Boolean.TRUE));
        } else if (PromoCodeType.TYPE_REPEATING_CLIENTS.equals(codeType)) {
            select.where(PROMO_CODE.NEW_CLIENTS_ONLY.eq(Boolean.FALSE));
        }

        if (source != null) {
            select.whereExists(db.selectFrom(PROMO_CODE_SOURCE)
                .where(PROMO_CODE_SOURCE.PROMO_CODE_ID.eq(PROMO_CODE.ID).and(PROMO_CODE_SOURCE.SOURCE.eq(source))));
        }

        if (client != null) {
            select.whereExists(selectFrom(PROMO_CODE_CLIENT)
                .where(PROMO_CODE_CLIENT.CLIENT_NUMBER.eq(client.getClientNumber()))
                .and(PROMO_CODE_CLIENT.PROMO_CODE_ID.eq(PROMO_CODE.ID)));
        }

        return select;
    }

    @Override
    protected Object id(Record item) {
        return item.get(PROMO_CODE.ID);
    }
}
