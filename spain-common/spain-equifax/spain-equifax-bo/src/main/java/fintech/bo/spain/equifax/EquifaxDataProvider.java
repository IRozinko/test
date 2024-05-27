package fintech.bo.spain.equifax;

import com.vaadin.data.provider.Query;
import fintech.bo.components.JooqDataProvider;
import fintech.bo.components.views.BoComponentContext;
import fintech.bo.components.views.StandardScopes;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.jooq.DSLContext;
import org.jooq.Record;
import org.jooq.SelectWhereStep;

import java.time.LocalDate;

import static fintech.bo.db.jooq.crm.tables.Client.CLIENT;
import static fintech.bo.spain.db.jooq.equifax.tables.Equifax.EQUIFAX;

@Setter
@Accessors(chain = true)
public class EquifaxDataProvider extends JooqDataProvider<Record> {

    private BoComponentContext componentContext = new BoComponentContext();

    private String status;
    private LocalDate createdFrom;
    private LocalDate createdTo;

    public EquifaxDataProvider(DSLContext db) {
        super(db);
    }

    @Override
    protected SelectWhereStep<Record> buildSelect(Query<Record, String> query) {
        SelectWhereStep<Record> select = db.select(EQUIFAX.fields()).select(CLIENT.CLIENT_NUMBER, CLIENT.DELETED)
            .from(EQUIFAX).join(CLIENT).on(EQUIFAX.CLIENT_ID.eq(CLIENT.ID));
        componentContext.scope(StandardScopes.SCOPE_CLIENT).ifPresent(id -> select.where(EQUIFAX.CLIENT_ID.eq(id)));
        if (status != null) {
            select.where(EQUIFAX.STATUS.eq(status));
        }
        if (createdFrom != null) {
            select.where(EQUIFAX.CREATED_AT.greaterOrEqual(createdFrom.atStartOfDay()));
        }
        if (createdTo != null) {
            select.where(EQUIFAX.CREATED_AT.lt(createdTo.atStartOfDay().plusDays(1)));
        }
        return select;
    }

    @Override
    protected Object id(Record item) {
        return item.get(EQUIFAX.ID);
    }

}
