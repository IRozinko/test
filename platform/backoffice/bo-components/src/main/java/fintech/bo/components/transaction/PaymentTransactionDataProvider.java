package fintech.bo.components.transaction;

import com.vaadin.data.provider.Query;
import fintech.bo.components.client.JooqClientDataProvider;
import fintech.bo.components.client.JooqClientDataService;
import lombok.extern.slf4j.Slf4j;
import org.jooq.DSLContext;
import org.jooq.Field;
import org.jooq.Record;
import org.jooq.SelectWhereStep;

import static fintech.bo.components.client.ClientDataProviderUtils.FIELD_CLIENT_NAME;
import static fintech.bo.db.jooq.crm.tables.Client.CLIENT;
import static fintech.bo.db.jooq.lending.tables.Invoice.INVOICE;
import static fintech.bo.db.jooq.lending.tables.Loan.LOAN;
import static fintech.bo.db.jooq.payment.Payment.PAYMENT;
import static fintech.bo.db.jooq.transaction.tables.Transaction.TRANSACTION_;

@Slf4j
public class PaymentTransactionDataProvider extends JooqClientDataProvider<Record> {

    public static final Field<String> FIELD_LOAN = LOAN.LOAN_NUMBER.as("loan");
    public static final Field<String> FIELD_INVOICE = INVOICE.NUMBER.as("invoice");

    private Long clientId;
    private Long loanId;
    private Boolean voided;

    public PaymentTransactionDataProvider(DSLContext db, JooqClientDataService jooqClientDataService) {
        super(db, jooqClientDataService);
    }

    @Override
    protected SelectWhereStep<Record> buildSelect(Query<Record, String> query) {
        SelectWhereStep<Record> select = db
            .select(fields(
                TRANSACTION_.fields(),
                FIELD_CLIENT_NAME,
                FIELD_LOAN,
                FIELD_INVOICE,
                PAYMENT.PAYMENT_.DETAILS
            ))
            .from(TRANSACTION_
                .join(PAYMENT.PAYMENT_).on(TRANSACTION_.PAYMENT_ID.eq(PAYMENT.PAYMENT_.ID))
                .leftJoin(CLIENT).on(TRANSACTION_.CLIENT_ID.eq(CLIENT.ID))
                .leftJoin(LOAN).on(TRANSACTION_.LOAN_ID.eq(LOAN.ID)))
            .leftJoin(INVOICE).on(TRANSACTION_.INVOICE_ID.eq(INVOICE.ID));

        if (clientId != null) {
            select.where(TRANSACTION_.CLIENT_ID.eq(clientId));
        }
        if (loanId != null) {
            select.where(TRANSACTION_.LOAN_ID.eq(loanId));
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

    public void setClientId(Long clientId) {
        this.clientId = clientId;
    }

    public void setLoanId(Long loanId) {
        this.loanId = loanId;
    }

    public void setVoided(Boolean voided) {
        this.voided = voided;
    }
}
