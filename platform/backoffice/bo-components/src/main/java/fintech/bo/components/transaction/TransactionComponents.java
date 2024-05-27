package fintech.bo.components.transaction;

import com.vaadin.server.Sizeable;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Grid;
import com.vaadin.ui.StyleGenerator;
import fintech.bo.api.client.TransactionApiClient;
import fintech.bo.components.BackofficeTheme;
import fintech.bo.components.PropertyLayout;
import fintech.bo.components.client.ClientComponents;
import fintech.bo.components.client.JooqClientDataService;
import fintech.bo.components.loan.LoanComponents;
import fintech.bo.components.payments.PaymentComponents;
import fintech.bo.components.utils.BigDecimalUtils;
import org.apache.commons.lang3.StringUtils;
import org.jooq.DSLContext;
import org.jooq.Record;

import java.math.BigDecimal;

import static fintech.bo.components.JooqDataProvider.fields;
import static fintech.bo.components.client.ClientDataProviderUtils.FIELD_CLIENT_NAME;
import static fintech.bo.components.invoice.InvoiceComponents.invoiceLink;
import static fintech.bo.components.transaction.TransactionDataProvider.FIELD_INVOICE;
import static fintech.bo.components.transaction.TransactionDataProvider.FIELD_LOAN;
import static fintech.bo.db.jooq.crm.tables.Client.CLIENT;
import static fintech.bo.db.jooq.lending.tables.Invoice.INVOICE;
import static fintech.bo.db.jooq.lending.tables.Loan.LOAN;
import static fintech.bo.db.jooq.transaction.tables.Transaction.TRANSACTION_;

public abstract class TransactionComponents {

    private final DSLContext db;
    private final TransactionApiClient transactionApiClient;
    private final JooqClientDataService jooqClientDataService;

    protected TransactionComponents(DSLContext db, TransactionApiClient transactionApiClient, JooqClientDataService jooqClientDataService) {
        this.db = db;
        this.transactionApiClient = transactionApiClient;
        this.jooqClientDataService = jooqClientDataService;
    }

    public ComboBox<String> transactionTypeComboBox() {
        ComboBox<String> comboBox = new ComboBox<>();
        comboBox.setPlaceholder("Transaction type");
        comboBox.setItems(TransactionConstants.ALL_TRANSACTION_TYPES);
        comboBox.setTextInputAllowed(false);
        comboBox.setPageLength(30);
        comboBox.setWidth(200, Sizeable.Unit.PIXELS);
        return comboBox;
    }

    public TransactionDataProvider dataProvider() {
        return new TransactionDataProvider(db, jooqClientDataService);
    }

    public abstract Grid<Record> grid(TransactionDataProvider dataProvider);

    public PaymentTransactionDataProvider paymentTransactionsDataProvider() {
        PaymentTransactionDataProvider provider = new PaymentTransactionDataProvider(db, jooqClientDataService);
        provider.setVoided(false);
        return provider;
    }

    public abstract Grid<Record> paymentTransactionsGrid(PaymentTransactionDataProvider dataProvider);

    public TransactionInfoDialog transactionInfoDialog(Long id) {
        Record tx = jooqClientDataService.runQueryHidingDeletedClients(db
            .select(fields(
                TRANSACTION_.fields(),
                FIELD_CLIENT_NAME,
                FIELD_LOAN,
                FIELD_INVOICE
            ))
            .from(TRANSACTION_
                .leftJoin(CLIENT).on(TRANSACTION_.CLIENT_ID.eq(CLIENT.ID))
                .leftJoin(LOAN).on(TRANSACTION_.LOAN_ID.eq(LOAN.ID)))
            .leftJoin(INVOICE).on(TRANSACTION_.INVOICE_ID.eq(INVOICE.ID))
            .where(TRANSACTION_.ID.eq(id)))
            .get(0);
        return new TransactionInfoDialog(transactionApiClient, this, tx);
    }

    public PropertyLayout transactionInfo(Record record) {
        PropertyLayout layout = new PropertyLayout("Transaction");
        layout.add("Id", record.get(TRANSACTION_.ID));
        layout.add("Type", record.get(TRANSACTION_.TRANSACTION_TYPE));
        layout.add("Sub type", record.get(TRANSACTION_.TRANSACTION_SUB_TYPE));
        layout.add("Value date", record.get(TRANSACTION_.VALUE_DATE));
        layout.add("Booking date", record.get(TRANSACTION_.BOOKING_DATE));
        layout.add("Post date", record.get(TRANSACTION_.POST_DATE));
        if (record.get(TRANSACTION_.EXTENSION) != 0) {
            layout.addSpacer();
            layout.add("Extension", record.get(TRANSACTION_.EXTENSION));
            layout.add("Extension days", record.get(TRANSACTION_.EXTENSION_DAYS));
        }
        layout.addSpacer();
        addIfNonZero(layout, "Principal disbursed", record.get(TRANSACTION_.PRINCIPAL_DISBURSED));
        addIfNonZero(layout, "Cash in", record.get(TRANSACTION_.CASH_IN));
        addIfNonZero(layout, "Cash out", record.get(TRANSACTION_.CASH_OUT));
        addIfNonZero(layout, "Principal paid", record.get(TRANSACTION_.PRINCIPAL_PAID));
        addIfNonZero(layout, "Principal written off", record.get(TRANSACTION_.PRINCIPAL_WRITTEN_OFF));
        addIfNonZero(layout, "Principal invoiced", record.get(TRANSACTION_.PRINCIPAL_INVOICED));
        addIfNonZero(layout, "Interest applied", record.get(TRANSACTION_.INTEREST_APPLIED));
        addIfNonZero(layout, "Interest paid", record.get(TRANSACTION_.INTEREST_PAID));
        addIfNonZero(layout, "Interest written off", record.get(TRANSACTION_.INTEREST_WRITTEN_OFF));
        addIfNonZero(layout, "Interest invoiced", record.get(TRANSACTION_.INTEREST_INVOICED));
        addIfNonZero(layout, "Penalty applied", record.get(TRANSACTION_.PENALTY_APPLIED));
        addIfNonZero(layout, "Penalty paid", record.get(TRANSACTION_.PENALTY_PAID));
        addIfNonZero(layout, "Penalty written off", record.get(TRANSACTION_.PENALTY_WRITTEN_OFF));
        addIfNonZero(layout, "Penalty invoiced", record.get(TRANSACTION_.PENALTY_INVOICED));
        addIfNonZero(layout, "Fee applied", record.get(TRANSACTION_.FEE_APPLIED));
        addIfNonZero(layout, "Fee paid", record.get(TRANSACTION_.FEE_PAID));
        addIfNonZero(layout, "Fee written off", record.get(TRANSACTION_.FEE_WRITTEN_OFF));
        addIfNonZero(layout, "Fee invoiced", record.get(TRANSACTION_.FEE_INVOICED));
        addIfNonZero(layout, "Overpayment received", record.get(TRANSACTION_.OVERPAYMENT_RECEIVED));
        addIfNonZero(layout, "Overpayment used", record.get(TRANSACTION_.OVERPAYMENT_USED));
        addIfNonZero(layout, "Overpayment refunded", record.get(TRANSACTION_.OVERPAYMENT_REFUNDED));
        layout.addSpacer();
        layout.addLink("Client", record.get(FIELD_CLIENT_NAME), ClientComponents.clientLink(record.get(TRANSACTION_.CLIENT_ID)));
        layout.addLink("Loan", record.get(FIELD_LOAN), LoanComponents.loanLink(record.get(TRANSACTION_.LOAN_ID)));
        layout.addLink("Invoice", record.get(FIELD_INVOICE), invoiceLink(record.get(TRANSACTION_.INVOICE_ID)));
        layout.add("Installment id", record.get(TRANSACTION_.INSTALLMENT_ID));
        layout.add("Contract id", record.get(TRANSACTION_.CONTRACT_ID));
        layout.addLink("Payment", record.get(TRANSACTION_.PAYMENT_ID), PaymentComponents.paymentLink(record.get(TRANSACTION_.PAYMENT_ID)));
        layout.add("Comments", record.get(TRANSACTION_.COMMENTS));
        layout.add("Disbursement id", record.get(TRANSACTION_.DISBURSEMENT_ID));
        layout.add("Voided date", record.get(TRANSACTION_.VOIDED_DATE));
        layout.add("Voided", record.get(TRANSACTION_.VOIDED));
        layout.add("Voids tx id", record.get(TRANSACTION_.VOIDS_TRANSACTION_ID));
        layout.add("Updated at", record.get(TRANSACTION_.UPDATED_AT));
        layout.add("Updated by", record.get(TRANSACTION_.UPDATED_BY));
        layout.add("Created at", record.get(TRANSACTION_.CREATED_AT));
        layout.add("Created by", record.get(TRANSACTION_.CREATED_BY));
        return layout;
    }

    private static void addIfNonZero(PropertyLayout layout, String title, BigDecimal value) {
        if (!BigDecimalUtils.isZero(value)) {
            layout.add(title, value);
        }
    }

    protected static StyleGenerator<Record> rowStyle() {
        return item -> {
            if (item.get(TRANSACTION_.VOIDED) || StringUtils.startsWith(item.get(TRANSACTION_.TRANSACTION_TYPE), "VOID_")) {
                return BackofficeTheme.TEXT_GRAY;
            } else {
                return "";
            }
        };
    }
}
