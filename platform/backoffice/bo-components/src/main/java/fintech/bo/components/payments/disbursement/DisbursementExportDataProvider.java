package fintech.bo.components.payments.disbursement;

import com.vaadin.data.provider.Query;
import fintech.bo.components.JooqDataProvider;
import org.jooq.DSLContext;
import org.jooq.Field;
import org.jooq.Record;
import org.jooq.SelectWhereStep;
import org.jooq.impl.DSL;

import java.math.BigDecimal;

import static fintech.bo.components.utils.BigDecimalUtils.amount;
import static fintech.bo.db.jooq.payment.tables.Disbursement.DISBURSEMENT;
import static fintech.bo.db.jooq.payment.tables.Institution.INSTITUTION;
import static fintech.bo.db.jooq.payment.tables.InstitutionAccount.INSTITUTION_ACCOUNT;
import static java.util.Arrays.asList;
import static org.jooq.impl.DSL.coalesce;
import static org.jooq.impl.DSL.sum;

public class DisbursementExportDataProvider extends JooqDataProvider<Record> {

    public static final Field<Integer> PENDING_DISBURSEMENT_COUNT =
        DSL.selectCount()
            .from(DISBURSEMENT)
            .where(DISBURSEMENT.INSTITUTION_ID.equal(INSTITUTION.ID)
                .and(DISBURSEMENT.INSTITUTION_ACCOUNT_ID.eq(INSTITUTION_ACCOUNT.ID))
                .and(DISBURSEMENT.STATUS_DETAIL.eq(DisbursementConstants.STATUS_DETAIL_PENDING)))
            .asField("pending_disbursement_count");

    public static final Field<BigDecimal> PENDING_DISBURSEMENT_AMOUNT =
        DSL.select(coalesce(sum(DISBURSEMENT.AMOUNT), amount(0)))
            .from(DISBURSEMENT)
            .where(DISBURSEMENT.INSTITUTION_ID.equal(INSTITUTION.ID)
                .and(DISBURSEMENT.INSTITUTION_ACCOUNT_ID.eq(INSTITUTION_ACCOUNT.ID))
                .and(DISBURSEMENT.STATUS_DETAIL.eq(DisbursementConstants.STATUS_DETAIL_PENDING)))
            .asField("pending_disbursement_amount");

    public DisbursementExportDataProvider(DSLContext db) {
        super(db);
    }


    @Override
    protected SelectWhereStep<Record> buildSelect(Query<Record, String> query) {
        SelectWhereStep<Record> select = db.select(asList(
            INSTITUTION_ACCOUNT.INSTITUTION_ID,
            INSTITUTION_ACCOUNT.ID,
            INSTITUTION.NAME,
            INSTITUTION.STATEMENT_API_EXPORTER,
            INSTITUTION_ACCOUNT.ACCOUNT_NUMBER,
            PENDING_DISBURSEMENT_COUNT,
            PENDING_DISBURSEMENT_AMOUNT))
            .from(INSTITUTION)
            .join(INSTITUTION_ACCOUNT).on(INSTITUTION_ACCOUNT.INSTITUTION_ID.eq(INSTITUTION.ID));

        select.where(INSTITUTION.STATEMENT_EXPORT_FORMAT.isNotNull().or(INSTITUTION.STATEMENT_API_EXPORTER.isNotNull()));

        return select;
    }


    @Override
    protected Object id(Record item) {
        return item.get(INSTITUTION_ACCOUNT.ID);
    }

}
