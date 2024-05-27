package fintech.bo.components.dc;

import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.shared.ui.MarginInfo;
import com.vaadin.ui.Component;
import com.vaadin.ui.Grid;
import com.vaadin.ui.HorizontalSplitPanel;
import com.vaadin.ui.Panel;
import com.vaadin.ui.StyleGenerator;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.ValoTheme;
import fintech.bo.api.client.CalendarApiClient;
import fintech.bo.api.client.DcApiClient;
import fintech.bo.api.client.FileApiClient;
import fintech.bo.api.model.permissions.BackofficePermissions;
import fintech.bo.components.BackofficeTheme;
import fintech.bo.components.JooqGridBuilder;
import fintech.bo.components.activity.ActivityComponents;
import fintech.bo.components.attachments.AttachmentDataProvider;
import fintech.bo.components.attachments.AttachmentsComponents;
import fintech.bo.components.client.ClientAddressDataProvider;
import fintech.bo.components.client.ClientComponents;
import fintech.bo.components.client.JooqClientDataService;
import fintech.bo.components.dialogs.ActionDialog;
import fintech.bo.components.dialogs.InfoDialog;
import fintech.bo.components.emails.EmailsComponents;
import fintech.bo.components.invoice.InvoiceComponents;
import fintech.bo.components.invoice.InvoiceDataProvider;
import fintech.bo.components.layouts.BusinessObjectLayout;
import fintech.bo.components.loan.LoanScheduleComponent;
import fintech.bo.components.notifications.Notifications;
import fintech.bo.components.sms.SmsComponents;
import fintech.bo.components.tabs.LazyTabSheet;
import fintech.bo.components.transaction.PaymentTransactionDataProvider;
import fintech.bo.components.transaction.TransactionComponents;
import fintech.bo.components.transaction.TransactionDataProvider;
import fintech.bo.components.utils.UrlUtils;
import fintech.bo.db.jooq.crm.tables.records.ClientAddressRecord;
import fintech.bo.db.jooq.dc.tables.records.DebtRecord;
import org.apache.commons.lang3.StringUtils;
import org.jooq.DSLContext;
import org.jooq.Record;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Objects;
import java.util.Optional;

import static fintech.bo.components.security.LoginService.hasPermission;
import static fintech.bo.db.jooq.crm.Tables.CLIENT_ADDRESS;
import static fintech.bo.db.jooq.dc.Tables.ACTION;
import static java.lang.String.format;

public abstract class AbstractDebtView extends VerticalLayout implements View {

    public static final String NAME = "debt";

    @Autowired
    protected DcQueries dcQueries;

    protected final DcComponents dcComponents;

    @Autowired
    protected DcApiClient dcApiClient;

    @Autowired
    protected DSLContext db;

    @Autowired
    protected ClientComponents clientComponents;

    @Autowired
    protected InvoiceComponents invoiceComponents;

    @Autowired
    protected AttachmentsComponents attachmentsComponents;

    @Autowired
    protected SmsComponents smsComponents;

    @Autowired
    protected EmailsComponents emailsComponents;

    @Autowired
    protected ActivityComponents activityComponents;

    @Autowired
    protected TransactionComponents transactionComponents;

    @Autowired
    protected FileApiClient fileApiClient;

    @Autowired
    protected CalendarApiClient calendarApiClient;

    @Autowired
    protected JooqClientDataService jooqClientDataService;

    protected Long debtId;

    protected DebtRecord debt;

    private NewActionComponent actionComponent;

    protected AbstractDebtView(DcComponents dcComponents) {
        this.dcComponents = Objects.requireNonNull(dcComponents);
    }

    protected abstract void addCustomActions(BusinessObjectLayout layout);

    @Override
    public void enter(ViewChangeListener.ViewChangeEvent event) {
        String idParam = UrlUtils.getParam(event.getParameters(), UrlUtils.ID);
        if (!StringUtils.isNumeric(idParam)) {
            String phoneNumber = UrlUtils.getParam(event.getParameters(), UrlUtils.TEL);
            if (phoneNumber != null) {
                Optional<Long> maybeDebtId = dcQueries.findDebtIdByPhone(phoneNumber);
                if (maybeDebtId.isPresent()) {
                    debtId = maybeDebtId.get();
                    refresh();
                } else {
                    Notifications.errorNotification("Debt not found", "Phone= " + phoneNumber);
                }
            }
        } else {
            debtId = Long.parseLong(idParam);
            refresh();
        }
    }

    protected void refresh() {
        removeAllComponents();
        setSpacing(false);
        setMargin(false);

        debt = dcQueries.findDebtById(debtId);
        if (debt == null) {
            Notifications.errorNotification("Debt not found");
            return;
        }
        setCaption(format("Debt %s", debt.getLoanNumber()));

        BusinessObjectLayout layout = new BusinessObjectLayout();
        layout.setSplitPosition(450);
        layout.setTitle(format("%s, %s", debt.getLoanNumber(), debt.getStatus()));
        buildLeft(layout);
        buildTabs(layout);
        buildActions(layout);
        addComponentsAndExpand(layout);
    }

    private void buildActions(BusinessObjectLayout layout) {
        if (hasPermission(BackofficePermissions.DC_DEBT_EDIT)) {
            layout.addActionMenuItem("State and status edit", e -> editDebtStateAndStatus(debt));
        }
        layout.setRefreshAction(this::refresh);
        addCustomActions(layout);
    }

    private void editDebtStateAndStatus(DebtRecord debt) {
        ActionDialog dialog = dcComponents.editDebtStateAndStatus(debt.getId());
        dialog.addCloseListener(e -> refresh());
        getUI().addWindow(dialog);
    }

    protected abstract void buildTabs(BusinessObjectLayout layout);

    protected void buildLeft(BusinessObjectLayout layout) {
        layout.addLeftComponent(clientComponents.clientInfoSimple(debt.getClientId(), true));
        layout.addLeftComponent(dcComponents.debtInfo(debt));
    }

    protected Component newActionTab() {
        if (actionComponent == null) {
            actionComponent = new NewActionComponent(dcQueries.getSettings(), debt, dcApiClient, calendarApiClient, dcComponents.getBulkActions()) {
                @Override
                protected void onActionSaved() {
                    actionComponent = null;
                    refresh();
                }
            };
            actionComponent.setMargin(new MarginInfo(false, true, false, false));
        }

        Panel leftPanel = new Panel();
        leftPanel.addStyleName(ValoTheme.PANEL_BORDERLESS);
        leftPanel.setSizeFull();
        leftPanel.setContent(actionComponent);

        LazyTabSheet tabSheet = new LazyTabSheet();
        tabSheet.setSizeFull();
        tabSheet.addTab(LazyTabSheet.lazyTab(() -> {
            ActionDataProvider dataProvider = new ActionDataProvider(db, jooqClientDataService);
            dataProvider.setDebtId(debtId);
            ActionHistoryComponent component = new ActionHistoryComponent();
            component.setDataProvider(dataProvider);
            Panel panel = new Panel();
            panel.addStyleName(ValoTheme.PANEL_BORDERLESS);
            panel.setSizeFull();
            panel.setContent(component);
            return panel;
        }, "Debt actions"));
        tabSheet.addTab(LazyTabSheet.lazyTab(() -> {
            Panel panel = new Panel();
            panel.addStyleName(ValoTheme.PANEL_BORDERLESS);
            panel.setSizeFull();
            panel.setContent(activityComponents.latestActivities(debt.getClientId()));
            return panel;
        }, "Client activities"));

        HorizontalSplitPanel splitPanel = new HorizontalSplitPanel();
        splitPanel.setSplitPosition(70, Unit.PERCENTAGE);
        splitPanel.addComponent(leftPanel);
        splitPanel.addComponent(tabSheet);
        return splitPanel;
    }

    protected Component actionsTab() {
        DebtActionDataProvider dataProvider = new DebtActionDataProvider(db);
        dataProvider.setDebtId(this.debtId);

        JooqGridBuilder<Record> builder = new JooqGridBuilder<>();
        builder.addActionColumn("Open", r -> {
            InfoDialog dialog = dcComponents.actionInfoDialog(r.get(ACTION.ID));
            getUI().addWindow(dialog);
        });
        builder.addColumn(ACTION.CREATED_AT).setWidth(180);
        builder.addColumn(ACTION.AGENT).setWidth(150);
        builder.addColumn(ACTION.ACTION_NAME).setWidth(150);
        builder.addColumn(ACTION.DEBT_STATUS).setWidth(150);
        builder.addColumn(ACTION.RESOLUTION).setWidth(150);
        builder.addColumn(ACTION.COMMENTS).setWidth(400);
        builder.addAuditColumns(ACTION);
        builder.addColumn(ACTION.ID);
        builder.sortDesc(ACTION.CREATED_AT);
        Grid<Record> grid = builder.build(dataProvider);
        grid.setStyleGenerator((StyleGenerator<Record>) item -> {
            if ("SYSTEM".equals(item.get(ACTION.AGENT))) {
                return BackofficeTheme.TEXT_GRAY;
            }
            return null;
        });

        return grid;
    }

    protected Component schedule() {
        LoanScheduleComponent component = new LoanScheduleComponent(db, fileApiClient, debt.getLoanId());
        component.setMargin(false);
        return component;
    }

    protected Component invoices() {
        InvoiceDataProvider dataProvider = invoiceComponents.dataProvider();
        dataProvider.setLoanId(debt.getLoanId());
        return invoiceComponents.grid(dataProvider);
    }

    protected Component payments() {
        PaymentTransactionDataProvider dataProvider = transactionComponents.paymentTransactionsDataProvider();
        dataProvider.setLoanId(debt.getLoanId());
        return transactionComponents.paymentTransactionsGrid(dataProvider);
    }

    protected Component attachments() {
        AttachmentDataProvider dataProvider = attachmentsComponents.attachmentDataProvider();
        dataProvider.setClientId(debt.getClientId());
        return attachmentsComponents.attachmentGrid(dataProvider);
    }

    protected Component addresses() {
        ClientAddressDataProvider dataProvider = new ClientAddressDataProvider(db);
        dataProvider.setClientId(debt.getClientId());
        JooqGridBuilder<ClientAddressRecord> grid = new JooqGridBuilder<>();
        grid.addColumn(CLIENT_ADDRESS.IS_PRIMARY).setWidth(100).setCaption("Primary");
        grid.addColumn(CLIENT_ADDRESS.TYPE).setWidth(100);
        grid.addColumn(CLIENT_ADDRESS.POSTAL_CODE).setWidth(100);
//        grid.addColumn(CLIENT_ADDRESS.PROVINCE);
        grid.addColumn(CLIENT_ADDRESS.PROVINCE);
        grid.addColumn(CLIENT_ADDRESS.CITY);
        grid.addColumn(CLIENT_ADDRESS.STREET).setWidth(200);
        grid.addColumn(CLIENT_ADDRESS.HOUSE_NUMBER);
        grid.addColumn(CLIENT_ADDRESS.HOUSING_TENURE);
        grid.addAuditColumns(CLIENT_ADDRESS);
        grid.sortDesc(CLIENT_ADDRESS.IS_PRIMARY);
        return grid.build(dataProvider);
    }

    protected Component transactions() {
        TransactionDataProvider dataProvider = transactionComponents.dataProvider();
        dataProvider.setClientId(debt.getClientId());
        return transactionComponents.grid(dataProvider);
    }

}
