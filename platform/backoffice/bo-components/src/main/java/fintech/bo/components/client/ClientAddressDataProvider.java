package fintech.bo.components.client;

import com.vaadin.data.provider.Query;
import fintech.bo.components.JooqDataProvider;
import fintech.bo.db.jooq.crm.tables.records.ClientAddressRecord;
import lombok.extern.slf4j.Slf4j;
import org.jooq.DSLContext;
import org.jooq.SelectWhereStep;

import static fintech.bo.db.jooq.crm.Tables.CLIENT_ADDRESS;


@Slf4j
public class ClientAddressDataProvider extends JooqDataProvider<ClientAddressRecord> {


    public ClientAddressDataProvider(DSLContext db) {
        super(db);
    }

    private Long clientId;

    @Override
    protected SelectWhereStep<ClientAddressRecord> buildSelect(Query<ClientAddressRecord, String> query) {
        SelectWhereStep<ClientAddressRecord> select = db.selectFrom(CLIENT_ADDRESS);
        if (clientId != null) {
            select.where(CLIENT_ADDRESS.CLIENT_ID.eq(clientId));
        }
        return select;
    }

    @Override
    protected Object id(ClientAddressRecord item) {
        return item.getId();
    }


    public void setClientId(Long clientId) {
        this.clientId = clientId;
    }
}
