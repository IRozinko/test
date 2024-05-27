package fintech.bo.components.client;

import com.vaadin.data.provider.Query;
import fintech.bo.components.JooqDataProvider;
import fintech.bo.db.jooq.crm.tables.records.ClientAttributeRecord;
import lombok.extern.slf4j.Slf4j;
import org.jooq.DSLContext;
import org.jooq.SelectWhereStep;

import static fintech.bo.db.jooq.crm.Tables.CLIENT_ATTRIBUTE;


@Slf4j
public class ClientAttributeDataProvider extends JooqDataProvider<ClientAttributeRecord> {


    public ClientAttributeDataProvider(DSLContext db) {
        super(db);
    }

    private Long clientId;

    @Override
    protected SelectWhereStep<ClientAttributeRecord> buildSelect(Query<ClientAttributeRecord, String> query) {
        SelectWhereStep<ClientAttributeRecord> select = db.selectFrom(CLIENT_ATTRIBUTE);
        if (clientId != null) {
            select.where(CLIENT_ATTRIBUTE.CLIENT_ID.eq(clientId));
        }
        return select;
    }

    @Override
    protected Object id(ClientAttributeRecord item) {
        return item.getClientId() + " " + item.getKey();
    }


    public void setClientId(Long clientId) {
        this.clientId = clientId;
    }
}
