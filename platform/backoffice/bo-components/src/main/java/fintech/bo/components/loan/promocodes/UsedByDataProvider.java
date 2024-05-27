package fintech.bo.components.loan.promocodes;

import com.vaadin.data.provider.Query;
import fintech.bo.components.JooqDataProvider;
import lombok.Setter;
import org.jooq.DSLContext;
import org.jooq.Field;
import org.jooq.Record;
import org.jooq.SelectWhereStep;

import static fintech.bo.db.jooq.crm.Tables.CLIENT;
import static fintech.bo.db.jooq.lending.Tables.LOAN;

@Setter
public class UsedByDataProvider extends JooqDataProvider<Record> {

    private Long promoCodeId;

    UsedByDataProvider(DSLContext db) {
        super(db);
    }

    @Override
    protected SelectWhereStep<Record> buildSelect(Query<Record, String> query) {
        Field[] clientFields = {CLIENT.CLIENT_NUMBER, CLIENT.FIRST_NAME, CLIENT.LAST_NAME};
        SelectWhereStep<Record> select = db.select(fields(
            clientFields,
            LOAN.ID,
            LOAN.CLIENT_ID,
            LOAN.LOAN_NUMBER,
            LOAN.ISSUE_DATE,
            LOAN.STATUS,
            LOAN.STATUS_DETAIL,
            LOAN.OVERDUE_DAYS
        ))
            .from(LOAN)
            .join(CLIENT).on(LOAN.CLIENT_ID.eq(CLIENT.ID));

        select.where(LOAN.PROMO_CODE_ID.eq(promoCodeId));

        select.orderBy(LOAN.CREATED_AT);
        return select;
    }

    @Override
    protected Object id(Record item) {
        return item.get(LOAN.ID);
    }
}
