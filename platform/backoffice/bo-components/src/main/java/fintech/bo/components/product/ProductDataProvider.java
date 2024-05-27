package fintech.bo.components.product;

import com.vaadin.data.provider.Query;
import fintech.bo.components.JooqDataProvider;
import fintech.bo.db.jooq.lending.tables.records.ProductRecord;
import org.jooq.DSLContext;
import org.jooq.SelectWhereStep;

import static fintech.bo.db.jooq.lending.tables.Product.PRODUCT;

public class ProductDataProvider extends JooqDataProvider<ProductRecord> {

    public ProductDataProvider(DSLContext db) {
        super(db);
    }

    @Override
    protected SelectWhereStep<ProductRecord> buildSelect(Query<ProductRecord, String> query) {
        SelectWhereStep<ProductRecord> select = db.selectFrom(PRODUCT);
        return select;
    }

    @Override
    protected Object id(ProductRecord item) {
        return item.getId();
    }

}
