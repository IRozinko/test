package fintech.bo.components.transaction;

import com.vaadin.data.provider.Query;
import fintech.bo.components.client.JooqClientDataProvider;
import fintech.bo.components.client.JooqClientDataService;
import lombok.extern.slf4j.Slf4j;
import org.jooq.DSLContext;
import org.jooq.Field;
import org.jooq.Record;
import org.jooq.SelectWhereStep;

import java.time.LocalDate;

import static fintech.bo.components.client.ClientDataProviderUtils.FIELD_CLIENT_NAME;
import static fintech.bo.db.jooq.crm.tables.Client.CLIENT;
import static fintech.bo.db.jooq.lending.tables.Invoice.INVOICE;
import static fintech.bo.db.jooq.lending.tables.Loan.LOAN;
import static fintech.bo.db.jooq.transaction.tables.Transaction.TRANSACTION_;

@Slf4j
public class TransactionDataProvider extends JooqClientDataProvider<Record> {

    public static final Field<String> FIELD_LOAN = LOAN.LOAN_NUMBER.as("loan");
    public static final Field<String> FIELD_INVOICE = INVOICE.NUMBER.as("invoice");

    private String transactionType;
    private Long clientId;
    private Long loanId;
    private Long paymentId;
    private Long invoiceId;
    private LocalDate valueDate;
    private Boolean voided;

    public TransactionDataProvider(DSLContext db, JooqClientDataService jooqClientDataService) {
        super(db, jooqClientDataService);
    }

    @Override
    protected SelectWhereStep<Record> buildSelect(Query<Record, String> query) {
        SelectWhereStep<Record> select = db
            .select(fields(
                TRANSACTION_.fields(),
                FIELD_CLIENT_NAME,
                FIELD_LOAN,
                FIELD_INVOICE
            ))
            .from(TRANSACTION_
                .leftJoin(CLIENT).on(TRANSACTION_.CLIENT_ID.eq(CLIENT.ID))
                .leftJoin(LOAN).on(TRANSACTION_.LOAN_ID.eq(LOAN.ID)))
            .leftJoin(INVOICE).on(TRANSACTION_.INVOICE_ID.eq(INVOICE.ID));

        if (clientId != null) {
            select.where(TRANSACTION_.CLIENT_ID.eq(clientId));
        }
        if (loanId != null) {
            select.where(TRANSACTION_.LOAN_ID.eq(loanId));
        }
        if (paymentId != null) {
            select.where(TRANSACTION_.PAYMENT_ID.eq(paymentId));
        }
        if (invoiceId != null) {
            select.where(TRANSACTION_.INVOICE_ID.eq(invoiceId));
        }
        if (transactionType != null) {
            select.where(TRANSACTION_.TRANSACTION_TYPE.eq(transactionType));
        }
        if (valueDate != null) {
            select.where(TRANSACTION_.VALUE_DATE.eq(valueDate));
        }
        if (voided != null) {
            select.where(TRANSACTION_.VOIDED.eq(voided));
        }
        return select;
    }

    @Override
    protected Object id(Record item) {
        return item.get(TRANSACTION_.ID);
    }

    public void setTransactionType(String transactionType) {
        this.transactionType = transactionType;
    }

    public void setClientId(Long clientId) {
        this.clientId = clientId;
    }

    public void setLoanId(Long loanId) {
        this.loanId = loanId;
    }

    public void setInvoiceId(Long invoiceId) {
        this.invoiceId = invoiceId;
    }

    public void setPaymentId(Long paymentId) {
        this.paymentId = paymentId;
    }

    public void setValueDate(LocalDate valueDate) {
        this.valueDate = valueDate;
    }

    public void setVoided(Boolean voided) {
        this.voided = voided;
    }
}
