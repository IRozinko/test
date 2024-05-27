package fintech.bo.components.product;

import fintech.bo.db.jooq.lending.tables.records.ProductRecord;
import org.jooq.DSLContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import static fintech.bo.db.jooq.lending.tables.Product.PRODUCT;

@Component
public class ProductQueries {

    @Autowired
    private DSLContext db;

    public ProductRecord findById(Long id) {
        return db.selectFrom(PRODUCT).where(PRODUCT.ID.eq(id)).fetchOne();
    }

    public ProductRecord findLast() {
        return db.selectFrom(PRODUCT).orderBy(PRODUCT.ID.desc()).fetchOne();
    }
}
