package fintech.bo.components.payments;

import fintech.bo.db.jooq.payment.tables.records.PaymentRecord;
import org.jooq.DSLContext;
import org.jooq.Field;
import org.jooq.Record;
import org.jooq.SelectOnConditionStep;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static fintech.bo.db.jooq.payment.Payment.PAYMENT;
import static fintech.bo.db.jooq.payment.tables.Institution.INSTITUTION;
import static fintech.bo.db.jooq.payment.tables.InstitutionAccount.INSTITUTION_ACCOUNT;
import static fintech.bo.db.jooq.payment.tables.Payment.PAYMENT_;

@Component
public class PaymentQueries {

    public static final Field<String> INSTITUTION_ACCOUNT_NUMBER = INSTITUTION_ACCOUNT.ACCOUNT_NUMBER.as("institution_account_number");
    public static final Field<String> INSTITUTION_NAME = INSTITUTION.NAME.as("institution_name");
    public static final Field<Long> INSTITUTION_ID = INSTITUTION.ID.as("institution_id");

    @Autowired
    private DSLContext db;

    public PaymentRecord findById(Long id) {
        return db.selectFrom(PAYMENT.PAYMENT_).where(PAYMENT.PAYMENT_.ID.eq(id)).fetchOne();
    }

    public PaymentSummary findSummaryById(Long id) {
        return summaryQuery(db).where(PAYMENT.PAYMENT_.ID.eq(id)).fetchOneInto(PaymentSummary.class);
    }

    public long countOpenPaymentsUntilDate(LocalDate dateTill) {
        return db.fetchCount(PAYMENT.PAYMENT_,
            PAYMENT.PAYMENT_.STATUS.eq(PaymentConstants.STATUS_OPEN)
                .and(PAYMENT.PAYMENT_.VALUE_DATE.lessOrEqual(dateTill)));
    }

    public long countClosedPaymentsOnDate(LocalDate date) {
        return db.fetchCount(PAYMENT.PAYMENT_,
            PAYMENT.PAYMENT_.STATUS.eq(PaymentConstants.STATUS_CLOSED)
                .and(PAYMENT.PAYMENT_.VALUE_DATE.eq(date)));
    }

    public static SelectOnConditionStep<Record> summaryQuery(DSLContext db) {
        List<Field<?>> fields = new ArrayList<>();
        fields.addAll(Arrays.asList(PAYMENT_.fields()));
        fields.add(INSTITUTION_ACCOUNT_NUMBER);
        fields.add(INSTITUTION_NAME);
        fields.add(INSTITUTION_ID);
        return db.select(fields)
            .from(PAYMENT.PAYMENT_)
            .leftJoin(INSTITUTION_ACCOUNT).on(INSTITUTION_ACCOUNT.ID.eq(PAYMENT.PAYMENT_.ACCOUNT_ID))
            .leftJoin(INSTITUTION).on(INSTITUTION.ID.eq(INSTITUTION_ACCOUNT.INSTITUTION_ID));
    }
}
