package fintech.bo.components.invoice;

import fintech.bo.db.jooq.lending.tables.records.InvoiceRecord;
import org.jooq.DSLContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import static fintech.bo.db.jooq.lending.tables.Invoice.INVOICE;

@Component
public class InvoiceQueries {

    @Autowired
    private DSLContext db;

    public InvoiceRecord findById(Long invoiceId) {
        return db.selectFrom(INVOICE).where(INVOICE.ID.eq(invoiceId)).fetchOne();
    }
}
