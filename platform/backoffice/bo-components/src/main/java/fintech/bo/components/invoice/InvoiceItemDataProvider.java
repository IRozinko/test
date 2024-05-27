package fintech.bo.components.invoice;

import com.vaadin.data.provider.Query;
import fintech.bo.components.JooqDataProvider;
import lombok.extern.slf4j.Slf4j;
import org.jooq.DSLContext;
import org.jooq.Record;
import org.jooq.SelectWhereStep;

import static fintech.bo.db.jooq.lending.tables.Invoice.INVOICE;
import static fintech.bo.db.jooq.lending.tables.InvoiceItem.INVOICE_ITEM;
import static java.util.Arrays.asList;


@Slf4j
public class InvoiceItemDataProvider extends JooqDataProvider<Record> {

    private Long invoiceId;

    public InvoiceItemDataProvider(DSLContext db) {
        super(db);
    }

    @Override
    protected SelectWhereStep<Record> buildSelect(Query<Record, String> query) {
        SelectWhereStep<Record> select = db
            .select(asList(
                INVOICE_ITEM.ID,
                INVOICE_ITEM.INVOICE_ID,
                INVOICE_ITEM.LOAN_ID,
                INVOICE_ITEM.AMOUNT,
                INVOICE_ITEM.AMOUNT_PAID,
                INVOICE_ITEM.TYPE,
                INVOICE_ITEM.CORRECTION,
                INVOICE_ITEM.SUB_TYPE,
                INVOICE_ITEM.CREATED_AT
            ))
            .from(INVOICE_ITEM);

        if (invoiceId != null) {
            select.where(INVOICE_ITEM.INVOICE_ID.eq(invoiceId));
        }

        return select;
    }

    @Override
    protected Object id(Record item) {
        return item.get(INVOICE.ID);
    }

    public Long getInvoiceId() {
        return invoiceId;
    }

    public void setInvoiceId(Long invoiceId) {
        this.invoiceId = invoiceId;
    }
}
