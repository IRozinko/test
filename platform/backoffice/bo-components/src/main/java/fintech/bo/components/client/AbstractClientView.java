package fintech.bo.components.client;

import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.server.Sizeable;
import com.vaadin.shared.ui.MarginInfo;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Component;
import com.vaadin.ui.HorizontalSplitPanel;
import com.vaadin.ui.Panel;
import com.vaadin.ui.StyleGenerator;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.ValoTheme;
import fintech.bo.api.model.permissions.BackofficePermissions;
import fintech.bo.components.BackofficeTheme;
import fintech.bo.components.JooqGridBuilder;
import fintech.bo.components.Refreshable;
import fintech.bo.components.TabHelper;
import fintech.bo.components.accounting.AccountingComponents;
import fintech.bo.components.accounting.AccountingEntryDataProvider;
import fintech.bo.components.accounting.AccountingTrialBalanceProvider;
import fintech.bo.components.activity.ActivityComponents;
import fintech.bo.components.activity.ActivityHistoryComponent;
import fintech.bo.components.activity.AddActivityComponent;
import fintech.bo.components.application.LoanApplicationComponents;
import fintech.bo.components.application.LoanApplicationDataProvider;
import fintech.bo.components.application.LoanApplicationQueries;
import fintech.bo.components.application.info.ApplicationInfo;
import fintech.bo.components.attachments.AttachmentDataProvider;
import fintech.bo.components.attachments.AttachmentsComponents;
import fintech.bo.components.client.dto.ClientDTO;
import fintech.bo.components.client.repository.ClientRepository;
import fintech.bo.components.common.Tab;
import fintech.bo.components.dc.DebtDataProvider;
import fintech.bo.components.emails.EmailLogTab;
import fintech.bo.components.emails.EmailsComponents;
import fintech.bo.components.invoice.InvoiceComponents;
import fintech.bo.components.invoice.InvoiceDataProvider;
import fintech.bo.components.layouts.BusinessObjectLayout;
import fintech.bo.components.loan.LoanComponents;
import fintech.bo.components.loan.LoanDataProvider;
import fintech.bo.components.loan.LoanQueries;
import fintech.bo.components.loan.discounts.DiscountComponents;
import fintech.bo.components.loan.discounts.DiscountDataProvider;
import fintech.bo.components.notifications.Notifications;
import fintech.bo.components.payments.disbursement.DisbursementComponents;
import fintech.bo.components.payments.disbursement.DisbursementDataProvider;
import fintech.bo.components.security.SecuredTab;
import fintech.bo.components.sms.SmsComponents;
import fintech.bo.components.sms.SmsLogTab;
import fintech.bo.components.task.TaskComponents;
import fintech.bo.components.task.TaskDataProvider;
import fintech.bo.components.transaction.PaymentTransactionDataProvider;
import fintech.bo.components.transaction.TransactionComponents;
import fintech.bo.components.transaction.TransactionDataProvider;
import fintech.bo.components.utils.UrlUtils;
import fintech.bo.components.views.BoComponentContext;
import fintech.bo.components.views.BoComponentDiscovery;
import fintech.bo.components.views.BoComponentMetadata;
import fintech.bo.components.views.BoComponentProvider;
import fintech.bo.components.views.StandardFeatures;
import fintech.bo.components.views.StandardScopes;
import fintech.bo.components.workflow.WorkflowComponents;
import fintech.bo.components.workflow.WorkflowDataProvider;
import fintech.bo.db.jooq.crm.tables.records.ClientAddressRecord;
import fintech.bo.db.jooq.crm.tables.records.ClientAttributeRecord;
import fintech.bo.db.jooq.crm.tables.records.ClientBankAccountRecord;
import lombok.extern.slf4j.Slf4j;
import org.jooq.DSLContext;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Objects;

import static fintech.bo.db.jooq.crm.Tables.CLIENT_ADDRESS;
import static fintech.bo.db.jooq.crm.Tables.CLIENT_ATTRIBUTE;
import static fintech.bo.db.jooq.crm.tables.ClientBankAccount.CLIENT_BANK_ACCOUNT;

@Slf4j
public abstract class AbstractClientView extends VerticalLayout implements View, Refreshable {

    public static final String NAME = "client";

    @Autowired
    protected AttachmentsComponents attachmentsComponents;

    @Autowired
    protected LoanComponents loanComponents;

    @Autowired
    protected LoanApplicationComponents applicationComponents;

    @Autowired
    protected SmsComponents smsComponents;

    @Autowired
    protected EmailsComponents emailsComponents;

    protected final ClientComponents clientComponents;

    @Autowired
    protected TransactionComponents transactionComponents;

    @Autowired
    protected TaskComponents taskComponents;

    @Autowired
    protected WorkflowComponents workflowComponents;

    @Autowired
    protected ClientQueries clientQueries;

    @Autowired
    protected LoanQueries loanQueries;

    @Autowired
    protected LoanApplicationQueries loanApplicationQueries;

    @Autowired
    protected AccountingComponents accountingComponents;

    @Autowired
    protected DiscountComponents discountComponents;

    @Autowired
    private InvoiceComponents invoiceComponents;

    @Autowired
    private DisbursementComponents disbursementComponents;

    @Autowired
    private ActivityComponents activityComponents;

    @Autowired
    protected DSLContext db;

    @Autowired
    private BoComponentDiscovery componentDiscovery;

    @Autowired
    private ClientRepository clientRepository;

    protected long clientId;

    private AddActivityComponent activityComponent;

    protected AbstractClientView(ClientComponents clientComponents) {
        this.clientComponents = Objects.requireNonNull(clientComponents);
    }

    @Override
    public void enter(ViewChangeListener.ViewChangeEvent event) {
        clientId = Long.parseLong(UrlUtils.getParam(event.getParameters(), UrlUtils.ID));
        refresh();
    }

    protected abstract void addCustomActions(BusinessObjectLayout layout);

    protected void addTabsBefore(ClientDTO client, BusinessObjectLayout layout) {
    }

    protected void addTabsAfter(ClientDTO client, BusinessObjectLayout layout) {
    }

    @Override
    public void refresh() {
        removeAllComponents();
        setSpacing(false);
        setMargin(false);

        ClientDTO client = clientRepository.getRequired(clientId);
        if (client == null) {
            Notifications.errorNotification("Client not found");
            return;
        }
        setCaption(String.format("Client %s", client.getClientNumber()));

        BusinessObjectLayout layout = new BusinessObjectLayout();
        if (client.isDeleted()) {
            layout.setTitleRed(ClientComponents.firstAndLastName(client) + " (DELETED)");
        } else {
            layout.setTitle(ClientComponents.firstAndLastName(client));
        }
        buildLeft(client, layout);
        buildTabs(client, layout);
        buildActions(client, layout);
        addComponentsAndExpand(layout);
    }

    private void buildActions(ClientDTO client, BusinessObjectLayout layout) {
        layout.setRefreshAction(this::refresh);
        if (!client.isDeleted()) {
            addCustomActions(layout);
        }
    }

    protected void buildTabs(ClientDTO client, BusinessObjectLayout layout) {
        addTabsBefore(client, layout);
        layout.addTab("Activity", () -> activity(client));
        layout.addTab("Applications", () -> applications(client));
        layout.addTab("Loans", () -> loans(client));
        layout.addTab("Payments", () -> payments(client));
        layout.addTab("Invoices", () -> invoices(client));
        TabHelper.addIfAllowed(layout, new AttachmentsTab("Attachments", client, attachmentsComponents));
        TabHelper.addIfAllowed(layout, new BankAccountTab("Bank accounts", client, db));
        TabHelper.addIfAllowed(layout, new AttributesTab("Attributes", client, db));
        TabHelper.addIfAllowed(layout, new AddressTab("Addresses", client, db));
        layout.addTab("Tasks", () -> tasks(client));
        TabHelper.addIfAllowed(layout, new SmsLogTab("SMS", client, smsComponents));
        TabHelper.addIfAllowed(layout, new EmailLogTab("Email", client, emailsComponents));
        layout.addTab("Workflows", () -> workflows(client));
        layout.addTab("Transactions", () -> transactions(client));
        layout.addTab("Disbursements", () -> disbursements(client));
        layout.addTab("Accounting", () -> accounting(client));
        layout.addTab("Discounts", () -> discounts(client));
        TabHelper.addIfAllowed(layout, new AdvancedTab("Advanced", client, componentDiscovery));
        addTabsAfter(client, layout);
    }

    private void buildLeft(ClientDTO client, BusinessObjectLayout layout) {
        layout.addLeftComponent(clientComponents.clientInfo(client));
        loanApplicationQueries.findOpen(clientId).forEach(application -> layout.addLeftComponent(ApplicationInfo.fromApplication(application)));
        loanQueries.findOpen(clientId).forEach(loan -> layout.addLeftComponent(loanComponents.loanInfo(loan)));
    }

    protected Component activity(ClientDTO client) {
        if (activityComponent == null) {
            activityComponent = activityComponents.addActivityComponent(client.getId());
            activityComponent.setOnSavedCallback(() -> {
                activityComponent = null;
                refresh();
            });
            activityComponent.setMargin(new MarginInfo(false, true, false, false));
        }

        ActivityHistoryComponent historyComponent = activityComponents.latestActivities(clientId);

        Panel leftPanel = new Panel();
        leftPanel.addStyleName(ValoTheme.PANEL_BORDERLESS);
        leftPanel.setSizeFull();
        leftPanel.setContent(activityComponent);

        Panel rightPanel = new Panel("Latest activities");
        rightPanel.addStyleName(ValoTheme.PANEL_BORDERLESS);
        rightPanel.setSizeFull();
        rightPanel.setContent(historyComponent);

        HorizontalSplitPanel splitPanel = new HorizontalSplitPanel();
        splitPanel.setSplitPosition(70, Unit.PERCENTAGE);
        splitPanel.addComponent(leftPanel);
        splitPanel.addComponent(rightPanel);
        return splitPanel;
    }

    private Component invoices(ClientDTO client) {
        InvoiceDataProvider dataProvider = invoiceComponents.dataProvider();
        dataProvider.setClientId(client.getId());
        return invoiceComponents.grid(dataProvider);
    }

    protected Component workflows(ClientDTO client) {
        WorkflowDataProvider dataProvider = workflowComponents.workflowDataProvider();
        dataProvider.setClientId(client.getId());
        return workflowComponents.workflowGrid(dataProvider);
    }

    protected Component transactions(ClientDTO client) {
        TransactionDataProvider dataProvider = transactionComponents.dataProvider();
        dataProvider.setClientId(client.getId());
        return transactionComponents.grid(dataProvider);
    }

    protected Component payments(ClientDTO client) {
        PaymentTransactionDataProvider dataProvider = transactionComponents.paymentTransactionsDataProvider();
        dataProvider.setClientId(client.getId());
        return transactionComponents.paymentTransactionsGrid(dataProvider);
    }

    protected Component disbursements(ClientDTO client) {
        DisbursementDataProvider dataProvider = disbursementComponents.dataProvider();
        dataProvider.setClientId(client.getId());
        return disbursementComponents.grid(dataProvider);
    }

    protected Component tasks(ClientDTO client) {
        TaskDataProvider dataProvider = taskComponents.taskDataProvider();
        dataProvider.setClientId(client.getId());
        return taskComponents.taskGrid(dataProvider);
    }

    protected Component loans(ClientDTO client) {
        LoanDataProvider dataProvider = loanComponents.dataProvider();
        dataProvider.setClientId(client.getId());
        return loanComponents.grid(dataProvider);
    }

    protected Component applications(ClientDTO client) {
        LoanApplicationDataProvider dataProvider = applicationComponents.dataProvider();
        dataProvider.setClientId(client.getId());
        return applicationComponents.grid(dataProvider);
    }

    protected Component accounting(ClientDTO client) {
        AccountingTrialBalanceProvider trialBalanceDataProvider = accountingComponents.trialBalanceDataProvider();
        trialBalanceDataProvider.setClientId(client.getId());

        AccountingEntryDataProvider entryDataProvider = accountingComponents.entryDataProvider();
        entryDataProvider.setClientId(client.getId());

        return accountingComponents.accountingTabs(trialBalanceDataProvider, entryDataProvider);
    }

    protected Component discounts(ClientDTO client) {
        DiscountDataProvider discountDataProvider = discountComponents.dataProvider();
        discountDataProvider.setClientId(client.getId());

        return discountComponents.grid(discountDataProvider);
    }

    @SecuredTab(permissions = BackofficePermissions.ADMIN, condition = "#client.deleted == true")
    protected static class AttributesTab extends Tab {

        private final DSLContext db;

        public AttributesTab(String caption, ClientDTO client, DSLContext db) {
            super(caption, client);
            this.db = db;
        }

        @Override
        public Component build() {
            ClientAttributeDataProvider dataProvider = new ClientAttributeDataProvider(db);
            dataProvider.setClientId(client.getId());
            JooqGridBuilder<ClientAttributeRecord> grid = new JooqGridBuilder<>();
            grid.addColumn(CLIENT_ATTRIBUTE.KEY).setWidth(250);
            grid.addColumn(CLIENT_ATTRIBUTE.VALUE).setWidth(400);
            return grid.build(dataProvider);
        }
    }

    @SecuredTab(permissions = BackofficePermissions.ADMIN, condition = "#client.deleted == true")
    protected static class AdvancedTab extends Tab {

        private final BoComponentDiscovery componentDiscovery;

        public AdvancedTab(String caption, ClientDTO client, BoComponentDiscovery componentDiscovery) {
            super(caption, client);
            this.componentDiscovery = componentDiscovery;
        }

        @Override
        public Component build() {
            VerticalLayout mainLayout = new VerticalLayout();
            mainLayout.setMargin(false);

            VerticalLayout viewLayout = new VerticalLayout();
            viewLayout.setMargin(false);
            ComboBox<BoComponentProvider> viewSelection = new ComboBox<>();
            viewSelection.setItemCaptionGenerator(provider -> provider.metadata().getCaption());
            viewSelection.setPageLength(30);
            viewSelection.setPlaceholder("Select view");
            viewSelection.setTextInputAllowed(false);
            viewSelection.setWidth(200, Sizeable.Unit.PIXELS);

            List<BoComponentProvider> providers = componentDiscovery.find(new BoComponentMetadata()
                .withScope(StandardScopes.SCOPE_CLIENT)
                .withFeature(StandardFeatures.FEATURE_ADVANCED_DATA));
            viewSelection.setItems(providers);

            viewSelection.addValueChangeListener(e -> {
                viewLayout.removeAllComponents();
                if (e.getValue() != null) {
                    BoComponentContext context = new BoComponentContext()
                        .withScope(StandardScopes.SCOPE_CLIENT, client.getId())
                        .withFeature(StandardFeatures.FEATURE_COMPACT_VIEW);
                    viewLayout.addComponentsAndExpand(e.getValue().build(context));
                }
            });

            mainLayout.addComponent(viewSelection);
            mainLayout.addComponentsAndExpand(viewLayout);
            return mainLayout;
        }
    }

    @SecuredTab(permissions = BackofficePermissions.ADMIN, condition = "#client.deleted == true")
    protected static class AddressTab extends Tab {

        private final DSLContext db;

        public AddressTab(String caption, ClientDTO client, DSLContext db) {
            super(caption, client);
            this.db = db;
        }

        @Override
        public Component build() {
            ClientAddressDataProvider dataProvider = new ClientAddressDataProvider(db);
            dataProvider.setClientId(client.getId());
            JooqGridBuilder<ClientAddressRecord> grid = new JooqGridBuilder<>();
            grid.addColumn(CLIENT_ADDRESS.IS_PRIMARY).setCaption("Primary").setWidth(100);
            grid.addColumn(CLIENT_ADDRESS.TYPE).setWidth(100);
            grid.addColumn(CLIENT_ADDRESS.POSTAL_CODE).setWidth(100);
            grid.addColumn(CLIENT_ADDRESS.PROVINCE);
            grid.addColumn(CLIENT_ADDRESS.CITY);
            grid.addColumn(CLIENT_ADDRESS.STREET).setWidth(200);
            grid.addColumn(CLIENT_ADDRESS.HOUSE_NUMBER);
            grid.addColumn(CLIENT_ADDRESS.HOUSING_TENURE);
            grid.addAuditColumns(CLIENT_ADDRESS);
            grid.sortDesc(CLIENT_ADDRESS.IS_PRIMARY);
            return grid.build(dataProvider);
        }
    }

    @SecuredTab(permissions = BackofficePermissions.ADMIN, condition = "#client.deleted == true")
    protected static class BankAccountTab extends Tab {

        private final DSLContext db;

        public BankAccountTab(String caption, ClientDTO client, DSLContext db) {
            super(caption, client);
            this.db = db;
        }

        @Override
        public Component build() {
            ClientBankAccountsDataProvider dataProvider = new ClientBankAccountsDataProvider(db);
            dataProvider.setClientId(client.getId());
            JooqGridBuilder<ClientBankAccountRecord> grid = new JooqGridBuilder<>();
            grid.addColumn(CLIENT_BANK_ACCOUNT.ACCOUNT_NUMBER).setWidth(250);
            grid.addColumn(CLIENT_BANK_ACCOUNT.IS_PRIMARY).setStyleGenerator((StyleGenerator<ClientBankAccountRecord>) item -> {
                if (item.getIsPrimary()) {
                    return BackofficeTheme.TEXT_SUCCESS;
                }
                return null;
            });
            grid.addColumn(CLIENT_BANK_ACCOUNT.ACCOUNT_OWNER_NAME);
            grid.addColumn(CLIENT_BANK_ACCOUNT.BANK_NAME);
            grid.addAuditColumns(CLIENT_BANK_ACCOUNT);
            return grid.build(dataProvider);
        }
    }

    @SecuredTab(permissions = BackofficePermissions.ADMIN, condition = "#client.deleted == true")
    protected static class AttachmentsTab extends Tab {

        private final AttachmentsComponents attachmentsComponents;

        public AttachmentsTab(String caption, ClientDTO client, AttachmentsComponents attachmentsComponents) {
            super(caption, client);
            this.attachmentsComponents = attachmentsComponents;
        }

        @Override
        public Component build() {
            AttachmentDataProvider dataProvider = attachmentsComponents.attachmentDataProvider();
            dataProvider.setClientId(client.getId());
            return attachmentsComponents.attachmentGrid(dataProvider);
        }
    }
}
