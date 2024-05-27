package fintech.bo.components.affiliate;

import com.vaadin.data.provider.Query;
import fintech.bo.components.JooqDataProvider;
import org.jooq.DSLContext;
import org.jooq.Record;
import org.jooq.SelectWhereStep;

import static fintech.bo.db.jooq.affiliate.Tables.PARTNER;

public class AffiliatePartnerDataProvider extends JooqDataProvider<Record> {

    public AffiliatePartnerDataProvider(DSLContext db) {
        super(db);
    }

    @Override
    protected SelectWhereStep<Record> buildSelect(Query<Record, String> query) {
        SelectWhereStep<Record> select = db.select(fields(PARTNER.fields())).from(PARTNER);
        return select;
    }

    @Override
    protected Object id(Record item) {
        return item.get(PARTNER.ID);
    }

}
