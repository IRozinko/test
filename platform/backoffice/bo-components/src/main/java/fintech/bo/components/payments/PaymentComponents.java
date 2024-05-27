package fintech.bo.components.payments;


import com.vaadin.ui.ComboBox;
import com.vaadin.ui.StyleGenerator;
import fintech.bo.components.BackofficeTheme;
import fintech.bo.db.jooq.payment.tables.records.InstitutionRecord;
import fintech.bo.db.jooq.payment.tables.records.PaymentRecord;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.jooq.DSLContext;
import org.jooq.Record;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Predicate;
import java.util.function.Supplier;

import static fintech.bo.db.jooq.payment.Payment.PAYMENT;
import static fintech.bo.db.jooq.payment.tables.Institution.INSTITUTION;
import static fintech.bo.db.jooq.payment.tables.InstitutionAccount.INSTITUTION_ACCOUNT;

@Component
public class PaymentComponents {

    @Autowired
    private DSLContext db;

    private Map<String, Pair<Predicate<PaymentRecord>, Supplier<TransactionHandler>>> transactionHandlers = new LinkedHashMap<>();

    public PaymentsDataProvider dataProvider() {
        return new PaymentsDataProvider(db);
    }

    public ComboBox<String> statusComboBox() {
        ComboBox<String> comboBox = new ComboBox<>();
        comboBox.setPlaceholder("Payment status");
        comboBox.setItems(PaymentConstants.ALL_STATUSES);
        comboBox.setTextInputAllowed(false);
        return comboBox;
    }

    public ComboBox<Record> institutionAccountsComboBox(InstitutionAccountDataProvider institutionAccountsDataProvider) {
        ComboBox<Record> comboBox = new ComboBox<>();
        comboBox.setPlaceholder("Institution accounts");
        comboBox.setDataProvider(institutionAccountsDataProvider);
        comboBox.setTextInputAllowed(false);
        comboBox.setItemCaptionGenerator(item -> String.format("%s - %s",
            item.get(INSTITUTION.NAME),
            item.get(INSTITUTION_ACCOUNT.ACCOUNT_NUMBER)));
        return comboBox;
    }

    public ComboBox<InstitutionRecord> institutionComboBox() {
        ComboBox<InstitutionRecord> comboBox = new ComboBox<>();
        comboBox.setPlaceholder("Institutions");
        comboBox.setDataProvider(new InstitutionDataProvider(db));
        comboBox.setTextInputAllowed(false);
        comboBox.setItemCaptionGenerator(item -> item.get(INSTITUTION.NAME));
        return comboBox;
    }

    public InstitutionAccountDataProvider institutionAccountsDataProvider() {
        return new InstitutionAccountDataProvider(db);
    }

    public ComboBox<String> typeComboBox() {
        ComboBox<String> comboBox = new ComboBox<>();
        comboBox.setPlaceholder("Payment type");
        comboBox.setItems(PaymentConstants.ALL_TYPES);
        comboBox.setTextInputAllowed(false);
        return comboBox;
    }

    public AddTransactionDialog addTransactionDialog(Long paymentId) {
        PaymentRecord payment = db.selectFrom(PAYMENT.PAYMENT_).where(PAYMENT.PAYMENT_.ID.eq(paymentId)).fetchOne();
        return new AddTransactionDialog(payment, addTransactionComponent(payment));
    }

    public AddTransactionComponent addTransactionComponent(PaymentRecord payment) {
        Map<String, Supplier<TransactionHandler>> filtered = new LinkedHashMap<>();
        transactionHandlers.forEach((type, pair) -> {
            if (pair.getLeft().test(payment)) {
                filtered.put(type, pair.getRight());
            }
        });
        return new AddTransactionComponent(payment, filtered);
    }

    public static StyleGenerator<Record> statusStyle() {
        return item -> {
            if (PaymentConstants.STATUS_PROCESSED.equals(item.get(PAYMENT.PAYMENT_.STATUS_DETAIL))) {
                return BackofficeTheme.TEXT_SUCCESS;
            } else if (PaymentConstants.STATUS_VOIDED.equals(item.get(PAYMENT.PAYMENT_.STATUS_DETAIL))) {
                return BackofficeTheme.TEXT_GRAY;
            } else if (PaymentConstants.STATUS_MANUAL.equals(item.get(PAYMENT.PAYMENT_.STATUS_DETAIL))) {
                return BackofficeTheme.TEXT_ACTIVE;
            } else {
                return "";
            }
        };
    }

    public void registerTransactionHandler(String transactionType, Predicate<PaymentRecord> predicate, Supplier<TransactionHandler> handlerFactory) {
        this.transactionHandlers.put(transactionType, ImmutablePair.of(predicate, handlerFactory));
    }

    public static String paymentLink(Long id) {
        return AbstractPaymentView.NAME + "/" + id;
    }
}
