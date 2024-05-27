package fintech.bo.components.dc;

import com.vaadin.data.provider.Query;
import fintech.bo.components.JooqDataProvider;
import org.jooq.DSLContext;
import org.jooq.Record;
import org.jooq.SelectJoinStep;
import org.jooq.SelectWhereStep;

import static fintech.bo.db.jooq.dc.Tables.ACTION;

public class DebtActionDataProvider extends JooqDataProvider<Record> {

    private Long debtId;

    public DebtActionDataProvider(DSLContext db) {
        super(db);
    }

    @Override
    protected SelectWhereStep<Record> buildSelect(Query<Record, String> query) {
        SelectJoinStep<Record> select = db.select(fields(ACTION.fields()))
            .from(ACTION);
        if (debtId != null) {
            select.where(ACTION.DEBT_ID.eq(debtId));
        }
        return select;
    }

    @Override
    protected Object id(Record item) {
        return item.get(ACTION.ID);
    }

    public void setDebtId(Long debtId) {
        this.debtId = debtId;
    }
}
