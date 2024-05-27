package fintech.bo.components.payments;

import com.vaadin.data.provider.Query;
import fintech.bo.components.JooqDataProvider;
import org.jooq.DSLContext;
import org.jooq.Record;
import org.jooq.SelectWhereStep;

import static fintech.bo.db.jooq.payment.tables.Institution.INSTITUTION;
import static fintech.bo.db.jooq.payment.tables.InstitutionAccount.INSTITUTION_ACCOUNT;
import static java.util.Arrays.asList;

public class InstitutionAccountDataProvider extends JooqDataProvider<Record> {

    public InstitutionAccountDataProvider(DSLContext db) {
        super(db);
    }

    @Override
    protected SelectWhereStep<Record> buildSelect(Query<Record, String> query) {
        SelectWhereStep<Record> select = db
            .select(asList(
                INSTITUTION.NAME,
                INSTITUTION_ACCOUNT.ACCOUNT_NUMBER,
                INSTITUTION_ACCOUNT.ID
            ))
            .from(INSTITUTION_ACCOUNT)
            .join(INSTITUTION).on(INSTITUTION.ID.eq(INSTITUTION_ACCOUNT.INSTITUTION_ID));

        return select;
    }

    @Override
    protected Object id(Record item) {
        return item.get( INSTITUTION_ACCOUNT.ID);
    }
}
