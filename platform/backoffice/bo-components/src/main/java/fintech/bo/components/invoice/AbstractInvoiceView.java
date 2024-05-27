package fintech.bo.components.invoice;

import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.ui.Component;
import com.vaadin.ui.VerticalLayout;
import fintech.bo.api.client.InvoiceApiClient;
import fintech.bo.components.client.ClientComponents;
import fintech.bo.components.layouts.BusinessObjectLayout;
import fintech.bo.components.loan.LoanComponents;
import fintech.bo.components.notifications.Notifications;
import fintech.bo.components.transaction.TransactionComponents;
import fintech.bo.components.transaction.TransactionDataProvider;
import fintech.bo.components.utils.UrlUtils;
import fintech.bo.db.jooq.lending.tables.records.InvoiceRecord;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;

import static java.lang.String.format;

@Slf4j
public abstract class AbstractInvoiceView extends VerticalLayout implements View {

    public static final String NAME = "invoice";

    @Autowired
    protected ClientComponents clientComponents;

    @Autowired
    protected TransactionComponents transactionComponents;

    @Autowired
    protected InvoiceComponents invoiceComponents;

    @Autowired
    protected InvoiceItemComponents invoiceItemComponents;

    @Autowired
    protected InvoiceQueries invoiceQueries;

    @Autowired
    protected LoanComponents loanComponents;

    @Autowired
    protected InvoiceApiClient invoiceApiClient;

    protected long invoiceId;

    @Override
    public void enter(ViewChangeListener.ViewChangeEvent event) {
        invoiceId = Long.parseLong(UrlUtils.getParam(event.getParameters(), UrlUtils.ID));
        refresh();
    }

    protected abstract void addCustomActions(BusinessObjectLayout layout);

    protected abstract void addTabsBefore(BusinessObjectLayout layout);

    protected abstract void addTabsAfter(BusinessObjectLayout layout);

    private void refresh() {
        removeAllComponents();
        setSpacing(false);
        setMargin(false);

        InvoiceRecord invoice = invoiceQueries.findById(invoiceId);
        if (invoice == null) {
            Notifications.errorNotification("Invoice not found");
            return;
        }
        setCaption(format("Invoice %s", invoice.getNumber()));

        BusinessObjectLayout layout = new BusinessObjectLayout();
        layout.setTitle(invoice.getNumber());
        buildLeft(invoice, layout);
        buildTabs(invoice, layout);
        buildActions(invoice, layout);
        addComponentsAndExpand(layout);
    }

    private void buildTabs(InvoiceRecord invoice, BusinessObjectLayout layout) {
        addTabsBefore(layout);
        layout.addTab("Items", () -> items(invoice));
        layout.addTab("Transactions", () -> transactions(invoice));
        addTabsAfter(layout);
    }

    private void buildLeft(InvoiceRecord invoice, BusinessObjectLayout layout) {
        layout.addLeftComponent(invoiceComponents.invoiceInfo(invoice));
        layout.addLeftComponent(clientComponents.clientInfo(invoice.getClientId()));
        layout.addLeftComponent(loanComponents.loanInfo(invoice.getLoanId()));
    }

    private void buildActions(InvoiceRecord invoice, BusinessObjectLayout layout) {
        layout.setRefreshAction(this::refresh);
        addCustomActions(layout);
    }

    private Component transactions(InvoiceRecord invoice) {
        TransactionDataProvider dataProvider = transactionComponents.dataProvider();
        dataProvider.setInvoiceId(invoice.getId());
        return transactionComponents.grid(dataProvider);
    }

    private Component items(InvoiceRecord invoice) {
        InvoiceItemDataProvider dataProvider = invoiceItemComponents.dataProvider();
        dataProvider.setInvoiceId(invoice.getId());
        return invoiceItemComponents.grid(dataProvider);
    }

}
