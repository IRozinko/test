package fintech.bo.spain.alfa.transaction;

import com.vaadin.ui.Grid;
import com.vaadin.ui.UI;
import fintech.bo.api.client.TransactionApiClient;
import fintech.bo.components.JooqGridBuilder;
import fintech.bo.components.client.ClientComponents;
import fintech.bo.components.client.JooqClientDataService;
import fintech.bo.components.invoice.InvoiceComponents;
import fintech.bo.components.loan.LoanComponents;
import fintech.bo.components.payments.PaymentComponents;
import fintech.bo.components.transaction.PaymentTransactionDataProvider;
import fintech.bo.components.transaction.TransactionComponents;
import fintech.bo.components.transaction.TransactionDataProvider;
import fintech.bo.components.transaction.TransactionInfoDialog;
import org.jooq.DSLContext;
import org.jooq.Record;
import org.springframework.stereotype.Component;

import static fintech.bo.components.client.ClientDataProviderUtils.FIELD_CLIENT_NAME;
import static fintech.bo.components.transaction.TransactionDataProvider.FIELD_INVOICE;
import static fintech.bo.components.transaction.TransactionDataProvider.FIELD_LOAN;
import static fintech.bo.db.jooq.payment.tables.Payment.PAYMENT_;
import static fintech.bo.db.jooq.transaction.tables.Transaction.TRANSACTION_;

@Component
public class AlfaTransactionComponents extends TransactionComponents {

    public AlfaTransactionComponents(DSLContext db, TransactionApiClient transactionApiClient, JooqClientDataService jooqClientDataService) {
        super(db, transactionApiClient, jooqClientDataService);
    }

    public Grid<Record> grid(TransactionDataProvider dataProvider) {
        JooqGridBuilder<Record> builder = new JooqGridBuilder<>();
        builder.addActionColumn("Open", r -> {
            TransactionInfoDialog dialog = transactionInfoDialog(r.get(TRANSACTION_.ID));
            UI.getCurrent().addWindow(dialog);
        });
        builder.addColumn(TRANSACTION_.ID).setWidth(80);
        builder.addColumn(TRANSACTION_.TRANSACTION_TYPE);
        builder.addColumn(TRANSACTION_.TRANSACTION_SUB_TYPE);
        builder.addColumn(TRANSACTION_.VALUE_DATE);
        builder.addLinkColumn(FIELD_CLIENT_NAME, r -> ClientComponents.clientLink(r.get(TRANSACTION_.CLIENT_ID)));
        builder.addLinkColumn(FIELD_LOAN, r -> LoanComponents.loanLink(r.get(TRANSACTION_.LOAN_ID)));
        builder.addLinkColumn(FIELD_INVOICE, r -> InvoiceComponents.invoiceLink(r.get(TRANSACTION_.INVOICE_ID)));
        builder.addLinkColumn(TRANSACTION_.PAYMENT_ID, r -> PaymentComponents.paymentLink(r.get(TRANSACTION_.PAYMENT_ID)));
        builder.addColumn(TRANSACTION_.CASH_IN);
        builder.addColumn(TRANSACTION_.CASH_OUT);
        builder.addColumn(TRANSACTION_.PRINCIPAL_DISBURSED);
        builder.addColumn(TRANSACTION_.PRINCIPAL_PAID);
        builder.addColumn(TRANSACTION_.PRINCIPAL_WRITTEN_OFF);
        builder.addColumn(TRANSACTION_.INTEREST_APPLIED);
        builder.addColumn(TRANSACTION_.INTEREST_PAID);
        builder.addColumn(TRANSACTION_.INTEREST_WRITTEN_OFF);
        builder.addColumn(TRANSACTION_.PENALTY_APPLIED);
        builder.addColumn(TRANSACTION_.PENALTY_PAID);
        builder.addColumn(TRANSACTION_.PENALTY_WRITTEN_OFF);
        builder.addColumn(TRANSACTION_.FEE_APPLIED);
        builder.addColumn(TRANSACTION_.FEE_PAID);
        builder.addColumn(TRANSACTION_.FEE_WRITTEN_OFF);
        builder.addColumn(TRANSACTION_.OVERPAYMENT_RECEIVED);
        builder.addColumn(TRANSACTION_.OVERPAYMENT_REFUNDED);
        builder.addColumn(TRANSACTION_.OVERPAYMENT_USED);
        builder.addColumn(TRANSACTION_.CREDIT_LIMIT);
        builder.addColumn(TRANSACTION_.COMMENTS);
        builder.addColumn(TRANSACTION_.BOOKING_DATE);
        builder.addColumn(TRANSACTION_.VOIDED_DATE);
        builder.addAuditColumns(TRANSACTION_);
        builder.sortDesc(TRANSACTION_.CREATED_AT);
        Grid<Record> grid = builder.build(dataProvider);
        grid.setStyleGenerator(rowStyle());
        return grid;
    }

    public Grid<Record> paymentTransactionsGrid(PaymentTransactionDataProvider dataProvider) {
        JooqGridBuilder<Record> builder = new JooqGridBuilder<>();
        builder.addActionColumn("Open", r -> {
            TransactionInfoDialog dialog = transactionInfoDialog(r.get(TRANSACTION_.ID));
            UI.getCurrent().addWindow(dialog);
        });
        builder.addLinkColumn(TRANSACTION_.PAYMENT_ID, r -> PaymentComponents.paymentLink(r.get(TRANSACTION_.PAYMENT_ID))).setCaption("Payment id").setWidth(120);
        builder.addColumn(TRANSACTION_.VALUE_DATE).setWidth(120);
        builder.addColumn(TRANSACTION_.TRANSACTION_TYPE);
        builder.addColumn(TRANSACTION_.TRANSACTION_SUB_TYPE);
        builder.addColumn(TRANSACTION_.CASH_IN).setWidth(120);
        builder.addColumn(TRANSACTION_.CASH_OUT).setWidth(120);
        builder.addColumn(TRANSACTION_.PRINCIPAL_PAID).setWidth(120);
        builder.addColumn(TRANSACTION_.INTEREST_PAID).setWidth(120);
        builder.addColumn(TRANSACTION_.PENALTY_PAID).setWidth(120);
        builder.addColumn(TRANSACTION_.FEE_PAID).setWidth(120);
        builder.addColumn(TRANSACTION_.OVERPAYMENT_RECEIVED).setWidth(120);
        builder.addColumn(PAYMENT_.DETAILS).setWidth(200);
        builder.addLinkColumn(FIELD_LOAN, r -> LoanComponents.loanLink(r.get(TRANSACTION_.LOAN_ID)));
        builder.addLinkColumn(FIELD_INVOICE, r -> InvoiceComponents.invoiceLink(r.get(TRANSACTION_.INVOICE_ID)));
        builder.addAuditColumns(TRANSACTION_);
        builder.addColumn(TRANSACTION_.ID);
        builder.sortDesc(TRANSACTION_.CREATED_AT);
        Grid<Record> grid = builder.build(dataProvider);
        grid.setStyleGenerator(rowStyle());
        return grid;
    }
}
