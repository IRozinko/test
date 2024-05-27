package fintech.bo.components.payments;


import com.google.common.base.MoreObjects;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.ui.Button;
import com.vaadin.ui.Component;
import com.vaadin.ui.Grid;
import com.vaadin.ui.MenuBar;
import com.vaadin.ui.TextArea;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.ValoTheme;
import fintech.bo.api.client.PaymentApiClient;
import fintech.bo.api.model.payments.UnvoidPaymentRequest;
import fintech.bo.api.model.payments.VoidPaymentRequest;
import fintech.bo.api.model.permissions.BackofficePermissions;
import fintech.bo.components.PropertyLayout;
import fintech.bo.components.accounting.AccountingComponents;
import fintech.bo.components.accounting.AccountingEntryDataProvider;
import fintech.bo.components.accounting.AccountingTrialBalanceProvider;
import fintech.bo.components.background.BackgroundOperations;
import fintech.bo.components.dialogs.ConfirmDialog;
import fintech.bo.components.layouts.BusinessObjectLayout;
import fintech.bo.components.notifications.Notifications;
import fintech.bo.components.security.LoginService;
import fintech.bo.components.transaction.TransactionComponents;
import fintech.bo.components.transaction.TransactionDataProvider;
import fintech.bo.components.utils.UrlUtils;
import lombok.extern.slf4j.Slf4j;
import org.jooq.DSLContext;
import org.jooq.Record;
import org.springframework.beans.factory.annotation.Autowired;
import retrofit2.Call;


@Slf4j
public abstract class AbstractPaymentView extends VerticalLayout implements View {

    public static final String NAME = "payment";

    protected long paymentId;

    @Autowired
    protected PaymentQueries paymentQueries;

    @Autowired
    protected TransactionComponents transactionComponents;

    @Autowired
    protected PaymentComponents paymentComponents;

    @Autowired
    protected PaymentApiClient paymentApiClient;

    @Autowired
    protected AccountingComponents accountingComponents;

    @Autowired
    protected DSLContext db;

    @Override
    public void enter(ViewChangeListener.ViewChangeEvent event) {
        paymentId = Long.parseLong(UrlUtils.getParam(event.getParameters(), UrlUtils.ID));
        refresh();
    }

    protected void refresh() {
        removeAllComponents();
        setSpacing(false);
        setMargin(false);

        PaymentSummary payment = paymentQueries.findSummaryById(paymentId);
        if (payment == null) {
            Notifications.errorNotification("Payment not found");
            return;
        }
        setCaption(String.format("Payment %s", payment.getId()));

        BusinessObjectLayout layout = new BusinessObjectLayout();
        layout.setTitle(String.format("Payment %s, %s", payment.getId(), payment.getStatusDetail()));
        buildLeft(payment, layout);
        buildTabs(payment, layout);
        buildActions(payment, layout);
        addComponentsAndExpand(layout);
    }

    private void buildActions(PaymentSummary payment, BusinessObjectLayout layout) {
        layout.setRefreshAction(this::refresh);
        if (LoginService.hasPermission(BackofficePermissions.PAYMENT_VOID)) {
            MenuBar.MenuItem item = layout.addActionMenuItem("Void payment", e -> voidPayment(payment));
            item.setEnabled(PaymentConstants.STATUS_MANUAL.equals(payment.getStatusDetail()));
        }
        if (LoginService.hasPermission(BackofficePermissions.PAYMENT_VOID)) {
            MenuBar.MenuItem item = layout.addActionMenuItem("Un-void payment", e -> unvoidPayment(payment));
            item.setEnabled(PaymentConstants.STATUS_VOIDED.equals(payment.getStatusDetail()));
        }
        addCustomActions(payment, layout);
    }

    protected abstract void addCustomActions(PaymentSummary payment, BusinessObjectLayout layout);

    private void voidPayment(PaymentSummary payment) {
        ConfirmDialog confirm = new ConfirmDialog("Void payment?", (Button.ClickListener) event -> {
            VoidPaymentRequest request = new VoidPaymentRequest();
            request.setPaymentId(payment.getId());
            Call<Void> call = paymentApiClient.voidPayment(request);
            BackgroundOperations.callApi("Voiding payment", call, t -> {
                Notifications.trayNotification("Payment voided");
                refresh();
            }, Notifications::errorNotification);
        });
        getUI().addWindow(confirm);
    }

    private void unvoidPayment(PaymentSummary payment) {
        ConfirmDialog confirm = new ConfirmDialog("Un-void payment?", (Button.ClickListener) event -> {
            UnvoidPaymentRequest request = new UnvoidPaymentRequest();
            request.setPaymentId(payment.getId());
            Call<Void> call = paymentApiClient.unvoidPayment(request);
            BackgroundOperations.callApi("Unvoiding payment", call, t -> {
                refresh();
                Notifications.trayNotification("Payment un-voided");
            }, Notifications::errorNotification);
        });
        getUI().addWindow(confirm);
    }

    private void buildTabs(PaymentSummary payment, BusinessObjectLayout layout) {
        layout.addTab("Transactions", () -> transactions(payment));
        layout.addTab("Accounting", () -> accounting(payment));
    }

    private Component transactions(PaymentSummary payment) {
        TransactionDataProvider dataProvider = transactionComponents.dataProvider();
        dataProvider.setPaymentId(payment.getId());
        Grid<Record> grid = transactionComponents.grid(dataProvider);

        VerticalLayout layout = new VerticalLayout();
        layout.setMargin(false);
        layout.setSpacing(true);


        if (LoginService.hasPermission(BackofficePermissions.PAYMENT_ADD_TRANSACTION) && PaymentConstants.STATUS_MANUAL.equals(payment.getStatusDetail())) {
            Button addTransactionButton = new Button("Distribute");
            addTransactionButton.addStyleName(ValoTheme.BUTTON_PRIMARY);
            addTransactionButton.addClickListener(e -> {
                AddTransactionDialog dialog = paymentComponents.addTransactionDialog(payment.getId());
                dialog.addCloseListener(e1 -> refresh());
                getUI().addWindow(dialog);
            });
            layout.addComponent(addTransactionButton);
        }
        layout.addComponentsAndExpand(grid);
        return layout;
    }

    private void buildLeft(PaymentSummary payment, BusinessObjectLayout layout) {
        PropertyLayout propertyLayout = new PropertyLayout("Payment");
        propertyLayout.add("Id", payment.getId());
        propertyLayout.add("Status", payment.getStatus());
        propertyLayout.add("Status detail", payment.getStatusDetail());
        propertyLayout.add("Value date", payment.getValueDate());
        propertyLayout.add("Type", payment.getPaymentType());
        propertyLayout.add("Amount", payment.getAmount());
        propertyLayout.add("Pending amount", payment.getPendingAmount());
        propertyLayout.add("Institution", payment.getInstitutionName());
        propertyLayout.add("Account number", payment.getInstitutionAccountNumber());
        propertyLayout.add("Counterparty account", payment.getCounterpartyAccount());
        propertyLayout.add("Counterparty name", payment.getCounterpartyName());
        propertyLayout.add("Counterparty address", payment.getCounterpartyAddress());
        propertyLayout.add("Created by", payment.getCreatedBy());
        propertyLayout.add("Created at", payment.getCreatedAt());
        propertyLayout.addSpacer();
        propertyLayout.addComponent(details(payment));

        layout.addLeftComponent(propertyLayout);
    }

    private TextArea details(PaymentSummary payment) {
        TextArea details = new TextArea("Details");
        details.setValue(MoreObjects.firstNonNull(payment.getDetails(), "-"));
        details.setWidth(100, Unit.PERCENTAGE);
        details.setReadOnly(true);
        return details;
    }

    private Component accounting(PaymentSummary payment) {
        AccountingTrialBalanceProvider trialBalanceDataProvider = accountingComponents.trialBalanceDataProvider();
        trialBalanceDataProvider.setPaymentId(payment.getId());

        AccountingEntryDataProvider entryDataProvider = accountingComponents.entryDataProvider();
        entryDataProvider.setPaymentId(payment.getId());

        return accountingComponents.accountingTabs(trialBalanceDataProvider, entryDataProvider);
    }
}
