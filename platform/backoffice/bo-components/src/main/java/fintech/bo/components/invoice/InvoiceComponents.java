package fintech.bo.components.invoice;

import com.vaadin.server.BrowserWindowOpener;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Grid;
import com.vaadin.ui.Label;
import com.vaadin.ui.Link;
import com.vaadin.ui.StyleGenerator;
import fintech.bo.api.client.FileApiClient;
import fintech.bo.api.model.CloudFile;
import fintech.bo.components.BackofficeTheme;
import fintech.bo.components.JooqGridBuilder;
import fintech.bo.components.PropertyLayout;
import fintech.bo.components.utils.CloudFileResource;
import fintech.bo.db.jooq.lending.tables.records.InvoiceRecord;
import org.jooq.DSLContext;
import org.jooq.Record;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import static fintech.TimeMachine.today;
import static fintech.bo.components.invoice.InvoiceConstants.STATUS_DETAIL_VOIDED;
import static fintech.bo.db.jooq.crm.tables.Client.CLIENT;
import static fintech.bo.db.jooq.lending.tables.Invoice.INVOICE;

@Component
public class InvoiceComponents {

    @Autowired
    private DSLContext db;

    @Autowired
    private FileApiClient fileApiClient;

    public InvoiceDataProvider dataProvider() {
        return new InvoiceDataProvider(db);
    }

    public Grid<Record> grid(InvoiceDataProvider dataProvider) {
        JooqGridBuilder<Record> builder = new JooqGridBuilder<>();
        builder.addNavigationColumn("Open", r -> "invoice/" + r.get(INVOICE.ID));
        builder.addComponentColumn(this::numberColumn).setCaption("Number");
        builder.addColumn(INVOICE.STATUS);
        builder.addColumn(INVOICE.STATUS_DETAIL).setStyleGenerator(statusStyle());
        builder.addColumn(INVOICE.INVOICE_DATE);
        builder.addColumn(INVOICE.DUE_DATE);
        builder.addColumn(INVOICE.PERIOD_FROM);
        builder.addColumn(INVOICE.PERIOD_TO);
        builder.addColumn(INVOICE.TOTAL);
        builder.addColumn(INVOICE.TOTAL_PAID);
        builder.addColumn(INVOICE.CLOSE_DATE);
        builder.addColumn(INVOICE.CORRECTIONS);
        builder.addAuditColumns(INVOICE);
        builder.addColumn(INVOICE.ID);
        builder.sortDesc(INVOICE.CREATED_AT);
        return builder.build(dataProvider);
    }


    public PropertyLayout invoiceInfo(InvoiceRecord invoice) {
        PropertyLayout layout = new PropertyLayout("Invoice");
        layout.addLink("Number", invoice.getNumber(), invoiceLink(invoice.getId()));
        layout.add("Status", invoice.getStatus());
        layout.add("Status detail", invoice.getStatusDetail());
        layout.add("Date", invoice.getInvoiceDate());
        layout.add("Due date", invoice.getDueDate());
        layout.add("Period from", invoice.getPeriodFrom());
        layout.add("Period to", invoice.getPeriodTo());
        layout.add("Total", invoice.getTotal());
        layout.add("Corrections", invoice.getCorrections());
        layout.add("Total paid", invoice.getTotalPaid());
        layout.add("Close date", invoice.getCloseDate());
        layout.add("Close reason", invoice.getCloseReason());
        layout.add("Created at", invoice.getCreatedAt());
        return layout;
    }

    public ComboBox<String> statusComboBox() {
        ComboBox<String> comboBox = new ComboBox<>();
        comboBox.setPlaceholder("Status");
        comboBox.setItems(
            InvoiceConstants.STATUS_OPEN,
            InvoiceConstants.STATUS_CLOSED
        );
        comboBox.setTextInputAllowed(false);
        return comboBox;
    }

    public ComboBox<String> statusDetailComboBox() {
        ComboBox<String> comboBox = new ComboBox<>();
        comboBox.setPlaceholder("Status detail");
        comboBox.setItems(
            InvoiceConstants.STATUS_DETAIL_PAID,
            InvoiceConstants.STATUS_DETAIL_PARTIALLY_PAID,
            InvoiceConstants.STATUS_DETAIL_PENDING,
            STATUS_DETAIL_VOIDED,
            InvoiceConstants.STATUS_DETAIL_CANCELLED
        );
        comboBox.setTextInputAllowed(false);
        return comboBox;
    }

    public static String invoiceLink(Long invoiceId) {
        return AbstractInvoiceView.NAME + "/" + invoiceId;
    }

    private static StyleGenerator<Record> statusStyle() {
        return item -> {
            String statusDetail = item.get(INVOICE.STATUS_DETAIL);
            String status = item.get(INVOICE.STATUS);

            boolean isOpen = InvoiceConstants.STATUS_OPEN.equalsIgnoreCase(status);
            boolean isPastDue = isOpen
                && today().isAfter(item.get(INVOICE.DUE_DATE));

            if (isOpen) {
                return isPastDue ? BackofficeTheme.TEXT_DANGER : BackofficeTheme.TEXT_ACTIVE;
            } else {
                return InvoiceConstants.STATUS_DETAIL_CANCELLED.equals(statusDetail)
                    || STATUS_DETAIL_VOIDED.equals(statusDetail) ? BackofficeTheme.TEXT_GRAY : BackofficeTheme.TEXT_SUCCESS;
            }
        };
    }

    private com.vaadin.ui.Component numberColumn(Record record) {
        boolean deleted = record.get(CLIENT.DELETED);
        Long fileId = record.get(INVOICE.FILE_ID);
        if (deleted || fileId == null) {
            return new Label(record.get((INVOICE.NUMBER)));
        }

        return generateViewLink(record);
    }

    private Link generateViewLink(Record record) {
        CloudFile cloudFile = new CloudFile(record.get(INVOICE.FILE_ID), record.get(INVOICE.FILE_NAME));
        Link link = new Link();
        link.setCaption(record.get((INVOICE.NUMBER)));
        CloudFileResource resource = new CloudFileResource(cloudFile, fileApiClient, f -> {});
        BrowserWindowOpener opener = new BrowserWindowOpener(resource);
        opener.extend(link);

        return link;
    }
}
