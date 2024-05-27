package fintech.bo.components.activity;

import com.vaadin.data.provider.Query;
import com.vaadin.data.provider.QuerySortOrder;
import fintech.bo.components.client.JooqClientDataProvider;
import fintech.bo.components.client.JooqClientDataService;
import lombok.Setter;
import org.jooq.DSLContext;
import org.jooq.Record;
import org.jooq.SelectJoinStep;
import org.jooq.SelectWhereStep;

import java.util.List;

import static fintech.bo.db.jooq.activity.tables.Activity.ACTIVITY_;
import static fintech.bo.db.jooq.crm.tables.Client.CLIENT;

public class ActivityDataProvider extends JooqClientDataProvider<Record> {

    @Setter
    private Long clientId;

    public ActivityDataProvider(DSLContext db, JooqClientDataService jooqClientDataService) {
        super(db, jooqClientDataService);
    }

    @Override
    protected SelectWhereStep<Record> buildSelect(Query<Record, String> query) {
        SelectJoinStep<Record> select = db.select(ACTIVITY_.fields())
            .from(ACTIVITY_)
            .join(CLIENT).on(ACTIVITY_.CLIENT_ID.eq(CLIENT.ID));
        if (clientId != null) {
            select.where(ACTIVITY_.CLIENT_ID.eq(clientId));
        }
        return select;
    }

    @Override
    protected Object id(Record item) {
        return item.get(ACTIVITY_.ID);
    }

    @Override
    protected List<QuerySortOrder> getDefaultSortOrders() {
        return QuerySortOrder.desc(ACTIVITY_.CREATED_AT.getName()).build();
    }
}
