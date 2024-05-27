package fintech.bo.components.payments;

import com.vaadin.data.provider.Query;
import fintech.bo.components.JooqDataProvider;
import fintech.bo.db.jooq.payment.tables.records.InstitutionRecord;
import org.jooq.DSLContext;
import org.jooq.SelectWhereStep;

import static fintech.bo.db.jooq.payment.tables.Institution.INSTITUTION;

public class InstitutionDataProvider extends JooqDataProvider<InstitutionRecord> {

    public InstitutionDataProvider(DSLContext db) {
        super(db);
    }

    @Override
    protected SelectWhereStep<InstitutionRecord> buildSelect(Query<InstitutionRecord, String> query) {
        return db.selectFrom(INSTITUTION);
    }

    @Override
    protected Object id(InstitutionRecord item) {
        return item.get(INSTITUTION.ID);
    }
}
