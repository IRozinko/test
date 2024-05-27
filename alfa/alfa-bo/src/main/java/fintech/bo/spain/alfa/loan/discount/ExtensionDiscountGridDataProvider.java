package fintech.bo.spain.alfa.loan.discount;

import com.vaadin.data.provider.Query;
import fintech.bo.components.JooqDataProvider;
import fintech.bo.spain.alfa.db.jooq.alfa.tables.records.ExtensionDiscountRecord;
import org.jooq.DSLContext;
import org.jooq.SelectWhereStep;

import static fintech.bo.spain.alfa.db.jooq.alfa.Tables.EXTENSION_DISCOUNT;


public class ExtensionDiscountGridDataProvider extends JooqDataProvider<ExtensionDiscountRecord> {

    private final long loanId;

    public ExtensionDiscountGridDataProvider(long loanId, DSLContext db) {
        super(db);
        this.loanId = loanId;
    }

    @Override
    protected SelectWhereStep<ExtensionDiscountRecord> buildSelect(Query<ExtensionDiscountRecord, String> query) {
        SelectWhereStep<ExtensionDiscountRecord> select = db
            .selectFrom(EXTENSION_DISCOUNT);
        select.where(EXTENSION_DISCOUNT.LOAN_ID.eq(loanId));
        return select;
    }

    @Override
    protected Object id(ExtensionDiscountRecord item) {
        return item.getId();
    }
}
