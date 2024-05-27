package fintech.bo.components.invoice;

import com.vaadin.data.provider.Query;
import fintech.bo.components.JooqDataProvider;
import lombok.extern.slf4j.Slf4j;
import org.jooq.DSLContext;
import org.jooq.Record;
import org.jooq.SelectWhereStep;

import java.time.LocalDate;

import static fintech.bo.db.jooq.crm.tables.Client.CLIENT;
import static fintech.bo.db.jooq.lending.tables.Invoice.INVOICE;

@Slf4j
public class InvoiceDataProvider extends JooqDataProvider<Record> {

    private Long loanId;

    private Long clientId;

    private String status;

    private String statusDetail;

    private LocalDate dueDateFrom;

    private LocalDate dueDateTo;

    private LocalDate closeDateFrom;

    private LocalDate closeDateTo;

    public InvoiceDataProvider(DSLContext db) {
        super(db);
    }

    @Override
    protected SelectWhereStep<Record> buildSelect(Query<Record, String> query) {
        SelectWhereStep<Record> select = db
            .select(fields(
                INVOICE.fields(),
                CLIENT.DELETED
            ))
            .from(INVOICE).join(CLIENT).on(INVOICE.CLIENT_ID.eq(CLIENT.ID));
        if (loanId != null) {
            select.where(INVOICE.LOAN_ID.eq(loanId));
        }
        if (clientId != null) {
            select.where(INVOICE.CLIENT_ID.eq(clientId));
        }
        if (statusDetail != null) {
            select.where(INVOICE.STATUS_DETAIL.eq(statusDetail));
        }
        if (status != null) {
            select.where(INVOICE.STATUS.eq(status));
        }
        if (dueDateFrom != null) {
            select.where(INVOICE.DUE_DATE.ge(dueDateFrom));
        }
        if (dueDateTo != null) {
            select.where(INVOICE.DUE_DATE.le(dueDateTo));
        }
        if (closeDateFrom != null) {
            select.where(INVOICE.CLOSE_DATE.ge(closeDateFrom.atStartOfDay()));
        }
        if (closeDateTo != null) {
            select.where(INVOICE.CLOSE_DATE.lt(closeDateTo.plusDays(1).atStartOfDay()));
        }

        return select;
    }

    @Override
    protected Object id(Record item) {
        return item.get(INVOICE.ID);
    }

    public Long getLoanId() {
        return loanId;
    }

    public void setLoanId(Long loanId) {
        this.loanId = loanId;
    }

    public void setClientId(Long clientId) {
        this.clientId = clientId;
    }

    public void setStatusDetail(String status) {
        this.statusDetail = status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setDueDateFrom(LocalDate dueDateFrom) {
        this.dueDateFrom = dueDateFrom;
    }

    public void setDueDateTo(LocalDate dueDateTo) {
        this.dueDateTo = dueDateTo;
    }

    public void setCloseDateFrom(LocalDate closeDateFrom) {
        this.closeDateFrom = closeDateFrom;
    }

    public void setCloseDateTo(LocalDate closeDateTo) {
        this.closeDateTo = closeDateTo;
    }
}
