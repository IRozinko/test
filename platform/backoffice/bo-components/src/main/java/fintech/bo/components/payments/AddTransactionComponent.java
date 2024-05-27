package fintech.bo.components.payments;

import com.google.common.base.MoreObjects;
import com.google.common.collect.Iterables;
import com.vaadin.ui.*;
import com.vaadin.ui.themes.ValoTheme;
import fintech.bo.components.BackofficeTheme;
import fintech.bo.components.PropertyLayout;
import fintech.bo.db.jooq.payment.tables.records.PaymentRecord;
import retrofit2.Call;

import java.util.Map;
import java.util.Optional;
import java.util.function.Supplier;

public class AddTransactionComponent extends CustomComponent {

    private final PaymentRecord payment;
    private final Panel handlerPanel;
    private final ComboBox<String> transactionTypes;
    private Map<String, Supplier<TransactionHandler>> handlers;
    private TransactionHandler handler;
    private Accordion infoTabSheet;

    public AddTransactionComponent(PaymentRecord payment, Map<String, Supplier<TransactionHandler>> handlers) {
        this.payment = payment;
        this.handlers = handlers;

        transactionTypes = new ComboBox<>("Transaction type");
        transactionTypes.setPlaceholder("Select transaction type");
        transactionTypes.setEmptySelectionAllowed(false);
        transactionTypes.setTextInputAllowed(false);
        transactionTypes.setItems(handlers.keySet());
        transactionTypes.setWidth(100, Unit.PERCENTAGE);
        transactionTypes.focus();


        VerticalLayout transactionLayout = new VerticalLayout();
        transactionLayout.setSpacing(true);
        transactionLayout.addComponent(transactionTypes);
        handlerPanel = new Panel();
        handlerPanel.setVisible(false);
        handlerPanel.addStyleName(ValoTheme.PANEL_BORDERLESS);
        transactionLayout.addComponent(handlerPanel);
        Panel transactionPanel = new Panel(transactionLayout);
        transactionPanel.addStyleName(ValoTheme.PANEL_BORDERLESS);


        Component infoPanel = builInfoPanel();

        HorizontalLayout rootLayout = new HorizontalLayout();
        rootLayout.addComponent(infoPanel);
        rootLayout.addComponent(transactionPanel);
        rootLayout.setExpandRatio(infoPanel, 5);
        rootLayout.setExpandRatio(transactionPanel, 7);
        rootLayout.setSizeFull();
        setCompositionRoot(rootLayout);


        transactionTypes.addValueChangeListener(e -> {
            resetInfoPanel();
            Supplier<TransactionHandler> factory = handlers.get(e.getValue());
            handler = factory.get();
            handler.init(payment, this);
            VerticalLayout handlerLayout = new VerticalLayout();
            handlerLayout.setSizeFull();
            handlerLayout.setMargin(false);
            handlerLayout.addComponent(handler);
            handlerPanel.setContent(handlerLayout);
            handlerPanel.setVisible(true);
        });
        transactionTypes.setValue(Iterables.getFirst(handlers.keySet(), null));
    }

    public void resetInfoPanel() {
        infoTabSheet.removeAllComponents();
        infoTabSheet.addTab(buildPaymentInfo(payment), "Payment");
    }

    public Accordion getInfoTabSheet() {
        return infoTabSheet;
    }

    public ComboBox<String> getTransactionTypes() {
        return transactionTypes;
    }

    public Optional<Call<?>> saveCall() {
        return handler.saveCall();
    }

    private Component builInfoPanel() {
        infoTabSheet = new Accordion();
        resetInfoPanel();
        return infoTabSheet;
    }

    private Component buildPaymentInfo(PaymentRecord payment) {
        PropertyLayout propertyLayout = new PropertyLayout();
        propertyLayout.add("Type", payment.getPaymentType());
        propertyLayout.add("Value date", payment.getValueDate());
        propertyLayout.add("Amount", payment.getAmount());
        propertyLayout.add("Pending amount", payment.getPendingAmount());
        propertyLayout.add("Counterparty account", payment.getCounterpartyAccount());
        propertyLayout.add("Counterparty name", payment.getCounterpartyName());
        propertyLayout.add("Counterparty address", payment.getCounterpartyAddress());
        propertyLayout.addSpacer();
        propertyLayout.addComponent(details(payment));
        propertyLayout.setSizeFull();
        return propertyLayout;
    }

    private TextArea details(PaymentRecord payment) {
        TextArea details = new TextArea();
        details.setValue(MoreObjects.firstNonNull(payment.getDetails(), "-"));
        details.setWordWrap(false);
        details.setWidth(100, Unit.PERCENTAGE);
        details.addStyleName(BackofficeTheme.TEXT_MONO);
        details.setRows(8);
        details.setReadOnly(true);
        return details;
    }

}
