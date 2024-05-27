package fintech.bo.components.emails;

import com.vaadin.data.provider.Query;
import fintech.bo.components.client.JooqClientDataProvider;
import fintech.bo.components.client.JooqClientDataService;
import fintech.bo.components.views.BoComponentContext;
import fintech.bo.components.views.StandardScopes;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.jooq.DSLContext;
import org.jooq.Record;
import org.jooq.SelectJoinStep;
import org.jooq.SelectWhereStep;

import static fintech.bo.db.jooq.crm.tables.Client.CLIENT;
import static fintech.bo.db.jooq.email.Tables.LOG;
import static fintech.bo.db.jooq.notification.Tables.NOTIFICATION_;

@Setter
@Accessors(chain = true)
public class EmailLogDataProvider extends JooqClientDataProvider<Record> {

    private BoComponentContext componentContext = new BoComponentContext();

    private Long clientId;

    public EmailLogDataProvider(DSLContext db, JooqClientDataService jooqClientDataService) {
        super(db, jooqClientDataService);
    }

    @Override
    protected SelectWhereStep<Record> buildSelect(Query<Record, String> query) {
        SelectJoinStep<Record> select = db.select(
            fields(
                LOG.fields(),
                NOTIFICATION_.CMS_KEY,
                NOTIFICATION_.CLIENT_ID,
                CLIENT.CLIENT_NUMBER,
                CLIENT.DELETED
            )
        )
            .from(LOG).join(NOTIFICATION_).on(LOG.ID.eq(NOTIFICATION_.EMAIL_LOG_ID))
            .join(CLIENT).on(NOTIFICATION_.CLIENT_ID.eq(CLIENT.ID));

        componentContext.scope(StandardScopes.SCOPE_CLIENT).ifPresent(id -> select.where(NOTIFICATION_.CLIENT_ID.eq(id)));

        if (clientId != null) {
            select.where(NOTIFICATION_.CLIENT_ID.eq(clientId));
        }
        return select;
    }

    @Override
    protected Object id(Record item) {
        return item.get(LOG.ID);
    }
}
