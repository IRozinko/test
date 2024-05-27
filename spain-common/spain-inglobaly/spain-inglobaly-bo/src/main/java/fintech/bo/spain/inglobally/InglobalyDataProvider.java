package fintech.bo.spain.inglobally;

import com.vaadin.data.provider.Query;
import fintech.bo.components.JooqDataProvider;
import fintech.bo.components.views.BoComponentContext;
import fintech.bo.components.views.StandardScopes;
import fintech.bo.spain.db.jooq.inglobaly.Tables;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.jooq.DSLContext;
import org.jooq.Record;
import org.jooq.SelectWhereStep;

import java.time.LocalDate;


@Setter
@Accessors(chain = true)
public class InglobalyDataProvider extends JooqDataProvider<Record> {


    private BoComponentContext componentContext = new BoComponentContext();

    private String status;
    private LocalDate createdFrom;
    private LocalDate createdTo;

    public InglobalyDataProvider(DSLContext db) {
        super(db);
    }

    @Override
    protected SelectWhereStep<Record> buildSelect(Query<Record, String> query) {
        SelectWhereStep<Record> select = db.select(fields(Tables.RESPONSE.fields())).from(Tables.RESPONSE);
        componentContext.scope(StandardScopes.SCOPE_CLIENT).ifPresent(id -> select.where(Tables.RESPONSE.CLIENT_ID.eq(id)));
        if (status != null) {
            select.where(Tables.RESPONSE.STATUS.eq(status));
        }
        if (createdFrom != null) {
            select.where(Tables.RESPONSE.CREATED_AT.greaterOrEqual(createdFrom.atStartOfDay()));
        }
        if (createdTo != null) {
            select.where(Tables.RESPONSE.CREATED_AT.lt(createdTo.atStartOfDay().plusDays(1)));
        }
        return select;
    }

    @Override
    protected Object id(Record item) {
        return item.get(Tables.RESPONSE.ID);
    }

}
