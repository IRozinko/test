package fintech.bo.components.client;

import com.vaadin.data.provider.Query;
import fintech.bo.components.JooqDataProvider;
import fintech.bo.db.jooq.crm.tables.records.ClientBankAccountRecord;
import lombok.extern.slf4j.Slf4j;
import org.jooq.DSLContext;
import org.jooq.SelectWhereStep;

import static fintech.bo.db.jooq.crm.tables.ClientBankAccount.CLIENT_BANK_ACCOUNT;


@Slf4j
public class ClientBankAccountsDataProvider extends JooqDataProvider<ClientBankAccountRecord> {


    public ClientBankAccountsDataProvider(DSLContext db) {
        super(db);
    }

    private Long clientId;

    @Override
    protected SelectWhereStep<ClientBankAccountRecord> buildSelect(Query<ClientBankAccountRecord, String> query) {
        SelectWhereStep<ClientBankAccountRecord> select = db.selectFrom(CLIENT_BANK_ACCOUNT);
        if (clientId != null) {
            select.where(CLIENT_BANK_ACCOUNT.CLIENT_ID.eq(clientId));
        }
        return select;
    }

    @Override
    protected Object id(ClientBankAccountRecord item) {
        return item.getId();
    }


    public void setClientId(Long clientId) {
        this.clientId = clientId;
    }
}
