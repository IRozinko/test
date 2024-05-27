package fintech.bo.spain.unnax;

import com.vaadin.data.provider.Query;
import fintech.bo.components.JooqDataProvider;
import fintech.spain.unnax.db.jooq.tables.records.CallbackRecord;
import lombok.Getter;
import lombok.Setter;
import org.jooq.DSLContext;
import org.jooq.SelectWhereStep;

import java.time.LocalDate;

import static fintech.spain.unnax.db.jooq.tables.Callback.CALLBACK;

@Setter
@Getter
public class UnnaxLogDataProvider extends JooqDataProvider<CallbackRecord> {

    private String searchQuery;
    private String callbackType;
    private LocalDate from;
    private LocalDate to;

    public UnnaxLogDataProvider(DSLContext db) {
        super(db);
    }

    @Override
    protected SelectWhereStep<CallbackRecord> buildSelect(Query<CallbackRecord, String> query) {
        SelectWhereStep<CallbackRecord> select = db.selectFrom(CALLBACK);

        if (searchQuery != null) {
            select.where(CALLBACK.DATA.likeIgnoreCase("%" + searchQuery + "%"));
        }

        if (from != null) {
            select.where(CALLBACK.DATE.greaterOrEqual(from.atStartOfDay()));
        }

        if (to != null) {
            select.where(CALLBACK.DATE.lessOrEqual(to.atStartOfDay()));
        }

        if (callbackType != null) {
            select.where(CALLBACK.EVENT.eq(callbackType));
        }

        select.orderBy(CALLBACK.CREATED_AT.desc());
        return select;
    }

    @Override
    protected Object id(CallbackRecord item) {
        return item.get(CALLBACK.ID);
    }
}
