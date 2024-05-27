package fintech.bo.components.dc;

import com.vaadin.data.provider.Query;
import com.vaadin.data.provider.QuerySortOrder;
import fintech.bo.components.client.JooqClientDataProvider;
import fintech.bo.components.client.JooqClientDataService;
import lombok.Setter;
import org.jooq.DSLContext;
import org.jooq.Record;
import org.jooq.SelectOnConditionStep;
import org.jooq.SelectWhereStep;

import java.util.List;

import static fintech.bo.db.jooq.crm.tables.Client.CLIENT;
import static fintech.bo.db.jooq.dc.Tables.ACTION;

public class ActionDataProvider extends JooqClientDataProvider<Record> {

    @Setter
    private Long debtId;

    public ActionDataProvider(DSLContext db, JooqClientDataService jooqClientDataService) {
        super(db, jooqClientDataService);
    }

    @Override
    protected SelectWhereStep<Record> buildSelect(Query<Record, String> query) {
        SelectOnConditionStep<Record> select = db.select(ACTION.fields())
            .from(ACTION).join(CLIENT).on(ACTION.CLIENT_ID.eq(CLIENT.ID));
        select.where(ACTION.AGENT.ne("SYSTEM"));
        if (debtId != null) {
            select.where(ACTION.DEBT_ID.eq(debtId));
        }
        return select;
    }

    @Override
    protected Object id(Record item) {
        return item.get(ACTION.ID);
    }

    @Override
    protected List<QuerySortOrder> getDefaultSortOrders() {
        return QuerySortOrder.desc(ACTION.CREATED_AT.getName()).build();
    }
}
