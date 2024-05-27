package fintech.bo.components.loan.promocodes;

import fintech.TimeMachine;
import org.jooq.DSLContext;
import org.jooq.Field;
import org.jooq.impl.DSL;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

import static fintech.bo.components.JooqDataProvider.fields;
import static fintech.bo.db.jooq.crm.Tables.CLIENT;
import static fintech.bo.db.jooq.lending.Tables.LOAN;
import static fintech.bo.db.jooq.lending.Tables.PROMO_CODE;
import static fintech.bo.db.jooq.lending.Tables.PROMO_CODE_SOURCE;
import static org.jooq.impl.DSL.listAgg;
import static org.jooq.impl.DSL.selectCount;

@Component
public class PromoCodeQueries {

    @Autowired
    private DSLContext db;

    PromoCodeDetails fetchDetails(Long promoCodeId) {
        return db.select(fields(PROMO_CODE.fields(), timesUsedField(), sourcesField()))
            .from(PROMO_CODE)
            .where(PROMO_CODE.ID.eq(promoCodeId))
            .fetchOptional()
            .map(record -> new PromoCodeDetails()
                .setId(record.getValue(PROMO_CODE.ID))
                .setCode(record.getValue(PROMO_CODE.CODE))
                .setDescription(record.getValue(PROMO_CODE.DESCRIPTION))
                .setType(Boolean.TRUE.equals(record.getValue(PROMO_CODE.NEW_CLIENTS_ONLY)) ? PromoCodeType.TYPE_NEW_CLIENTS : PromoCodeType.TYPE_REPEATING_CLIENTS)
                .setEffectiveFrom(record.getValue(PROMO_CODE.EFFECTIVE_FROM))
                .setEffectiveTo(record.getValue(PROMO_CODE.EFFECTIVE_TO))
                .setRateInPercent(record.getValue(PROMO_CODE.RATE_IN_PERCENT))
                .setMaxTimesToApply(record.getValue(PROMO_CODE.MAX_TIMES_TO_APPLY))
                .setActive(record.getValue(PROMO_CODE.ACTIVE))
                .setSource(record.getValue(sourcesField(), String.class))
                .setTimesUsed(record.getValue(timesUsedField(), Long.class))
            )
            .orElseThrow(() -> new RuntimeException("Promo code not found"));
    }

    public List<PromoCodeDetails> findActual() {
        return db.select(fields(PROMO_CODE.fields(), timesUsedField(), sourcesField()))
            .from(PROMO_CODE)
            .where(
                PROMO_CODE.ACTIVE.eq(true)
                    .and(PROMO_CODE.EFFECTIVE_FROM.le(TimeMachine.today()))
                    .and(PROMO_CODE.EFFECTIVE_TO.ge(TimeMachine.today()))
            ).stream()
            .map(record -> new PromoCodeDetails()
                .setId(record.getValue(PROMO_CODE.ID))
                .setCode(record.getValue(PROMO_CODE.CODE))
                .setDescription(record.getValue(PROMO_CODE.DESCRIPTION))
                .setType(Boolean.TRUE.equals(record.getValue(PROMO_CODE.NEW_CLIENTS_ONLY)) ? PromoCodeType.TYPE_NEW_CLIENTS : PromoCodeType.TYPE_REPEATING_CLIENTS)
                .setEffectiveFrom(record.getValue(PROMO_CODE.EFFECTIVE_FROM))
                .setEffectiveTo(record.getValue(PROMO_CODE.EFFECTIVE_TO))
                .setRateInPercent(record.getValue(PROMO_CODE.RATE_IN_PERCENT))
                .setMaxTimesToApply(record.getValue(PROMO_CODE.MAX_TIMES_TO_APPLY))
                .setActive(record.getValue(PROMO_CODE.ACTIVE))
                .setSource(record.getValue(sourcesField(), String.class))
                .setTimesUsed(record.getValue(timesUsedField(), Long.class))
            ).collect(Collectors.toList());
    }

    Field<Long> timesUsedField() {
        return selectCount()
            .from(LOAN)
            .where(LOAN.PROMO_CODE_ID.eq(PROMO_CODE.ID))
            .and(LOAN.STATUS.isDistinctFrom("VOIDED"))
            .asField("times_used");
    }

    Field<String> sourcesField() {
        return db.select(listAgg(PROMO_CODE_SOURCE.SOURCE, ", ")
            .withinGroupOrderBy(PROMO_CODE_SOURCE.SOURCE))
            .from(PROMO_CODE_SOURCE)
            .where(PROMO_CODE_SOURCE.PROMO_CODE_ID.eq(PROMO_CODE.ID))
            .asField("affiliates");
    }

    Field<Boolean> redeemedField() {
        Field<Object> timesUsed = selectCount()
            .from(LOAN)
            .where(LOAN.CLIENT_ID.eq(CLIENT.ID))
            .and((LOAN.PROMO_CODE_ID.eq(PROMO_CODE.ID)))
            .and(LOAN.STATUS.isDistinctFrom("VOIDED"))
            .asField();
        return DSL.when(timesUsed.ge(1), true)
            .otherwise(false)
            .as("redeemed");
    }

}
