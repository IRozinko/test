package fintech.bo.components.institution;

import com.vaadin.data.provider.Query;
import fintech.bo.components.JooqDataProvider;
import org.jooq.DSLContext;
import org.jooq.Record;
import org.jooq.SelectWhereStep;

import static fintech.bo.db.jooq.payment.tables.Institution.INSTITUTION;
import static fintech.bo.db.jooq.payment.tables.InstitutionAccount.INSTITUTION_ACCOUNT;

public class InstitutionDataProvider extends JooqDataProvider<Record> {

    public InstitutionDataProvider(DSLContext db) {
        super(db);
    }

    @Override
    protected SelectWhereStep<Record> buildSelect(Query<Record, String> query) {
        SelectWhereStep<Record> select = db
            .select(fields(
                INSTITUTION.fields(),
                INSTITUTION_ACCOUNT.ACCOUNT_NUMBER))
            .from(INSTITUTION)
            .leftJoin(INSTITUTION_ACCOUNT).on(INSTITUTION_ACCOUNT.INSTITUTION_ID.eq(INSTITUTION.ID))
            ;

        select.where(INSTITUTION_ACCOUNT.IS_PRIMARY.isTrue());

        return select;
    }

    @Override
    protected Object id(Record item) {
        return item.get(INSTITUTION.ID);
    }

}
