package fintech.bo.spain.alfa.client;

import com.vaadin.spring.annotation.SpringView;
import fintech.bo.components.JooqGridBuilder;
import fintech.bo.components.client.AbstractClientsView;
import fintech.bo.components.client.ClientGridDataProvider;
import org.jooq.Record;

import static fintech.bo.db.jooq.crm.tables.Client.CLIENT;
import static fintech.bo.db.jooq.crm.tables.EmailContact.EMAIL_CONTACT;

@SpringView(name = ClientsView.NAME)
public class ClientsView extends AbstractClientsView {

    @Override
    protected void addColumns(JooqGridBuilder<Record> builder) {
        builder.addNavigationColumn("Open", r -> "client/" + r.get(CLIENT.ID));
        builder.addColumn(CLIENT.CLIENT_NUMBER).setWidth(120);
        builder.addColumn(CLIENT.FIRST_NAME);
        builder.addColumn(CLIENT.LAST_NAME);
        builder.addColumn(ClientGridDataProvider.FIELD_NEXT_ACTIVITY).setWidth(200);
        builder.addColumn(ClientGridDataProvider.FIELD_LAST_CLOSE_REASON).setWidth(200);
        builder.addColumn(EMAIL_CONTACT.EMAIL).setWidth(250);
        builder.addColumn(CLIENT.PHONE);
        builder.addColumn(CLIENT.DOCUMENT_NUMBER);
        builder.addColumn(CLIENT.ACCOUNT_NUMBER);
        builder.addColumn(CLIENT.DATE_OF_BIRTH);
        builder.addAuditColumns(CLIENT);
        builder.addColumn(CLIENT.ID);
    }
}
