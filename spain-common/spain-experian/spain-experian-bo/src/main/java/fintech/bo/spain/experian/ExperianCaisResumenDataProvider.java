package fintech.bo.spain.experian;

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
import static fintech.bo.spain.db.jooq.experian.tables.CaisResumen.CAIS_RESUMEN;

@Setter
@Accessors(chain = true)
public class ExperianCaisResumenDataProvider extends JooqDataProvider<Record> {

    private BoComponentContext componentContext = new BoComponentContext();
    private String status;
    private LocalDate createdFrom;
    private LocalDate createdTo;

    public ExperianCaisResumenDataProvider(DSLContext db) {
        super(db);
    }

    @Override
    protected SelectWhereStep<Record> buildSelect(Query<Record, String> query) {
        SelectWhereStep<Record> select = db.select(CAIS_RESUMEN.fields()).select(CLIENT.CLIENT_NUMBER, CLIENT.DELETED)
            .from(CAIS_RESUMEN)
            .join(CLIENT).on(CAIS_RESUMEN.CLIENT_ID.eq(CLIENT.ID));

        componentContext.scope(StandardScopes.SCOPE_CLIENT).ifPresent(id -> select.where(CAIS_RESUMEN.CLIENT_ID.eq(id)));
        if (status != null) {
            select.where(CAIS_RESUMEN.STATUS.eq(status));
        }
        if (createdFrom != null) {
            select.where(CAIS_RESUMEN.CREATED_AT.greaterOrEqual(createdFrom.atStartOfDay()));
        }
        if (createdTo != null) {
            select.where(CAIS_RESUMEN.CREATED_AT.lt(createdTo.atStartOfDay().plusDays(1)));
        }
        return select;
    }

    @Override
    protected Object id(Record item) {
        return item.get(CAIS_RESUMEN.ID);
    }

}
