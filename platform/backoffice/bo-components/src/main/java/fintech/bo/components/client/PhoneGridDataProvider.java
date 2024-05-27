package fintech.bo.components.client;

import com.vaadin.data.provider.Query;
import fintech.bo.components.JooqDataProvider;
import fintech.bo.db.jooq.crm.tables.records.PhoneContactRecord;
import org.jooq.DSLContext;
import org.jooq.SelectWhereStep;

import static fintech.bo.db.jooq.crm.Tables.PHONE_CONTACT;

public class PhoneGridDataProvider extends JooqDataProvider<PhoneContactRecord> {

    private final long clientId;

    public PhoneGridDataProvider(long clientId, DSLContext db) {
        super(db);
        this.clientId = clientId;
    }

    @Override
    protected SelectWhereStep<PhoneContactRecord> buildSelect(Query<PhoneContactRecord, String> query) {
        SelectWhereStep<PhoneContactRecord> select = db
            .selectFrom(PHONE_CONTACT);

        select.where(PHONE_CONTACT.CLIENT_ID.eq(clientId));
        return select;
    }

    @Override
    protected Object id(PhoneContactRecord item) {
        return item.getId();
    }
}
