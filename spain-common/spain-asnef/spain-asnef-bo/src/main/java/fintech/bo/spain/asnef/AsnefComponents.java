package fintech.bo.spain.asnef;

import com.vaadin.server.Sizeable;
import com.vaadin.ui.Button;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Grid;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.components.grid.FooterCell;
import com.vaadin.ui.components.grid.FooterRow;
import com.vaadin.ui.themes.ValoTheme;
import fintech.bo.api.client.FileApiClient;
import fintech.bo.api.model.CloudFile;
import fintech.bo.components.BackofficeTheme;
import fintech.bo.components.JooqGridBuilder;
import fintech.bo.components.Refreshable;
import fintech.bo.components.background.BackgroundOperations;
import fintech.bo.components.client.ClientComponents;
import fintech.bo.components.client.JooqClientDataService;
import fintech.bo.components.common.DangerLabel;
import fintech.bo.components.dialogs.ConfirmDialog;
import fintech.bo.components.loan.LoanComponents;
import fintech.bo.components.notifications.Notifications;
import fintech.bo.components.utils.UrlUtils;
import fintech.bo.spain.asnef.api.AsnefApiClient;
import fintech.bo.spain.asnef.model.ExportAsnefFileRequest;
import fintech.bo.spain.db.jooq.asnef.tables.records.LogRecord;
import org.jooq.DSLContext;
import org.jooq.Record;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Objects;
import java.util.Optional;

import static fintech.bo.components.client.ClientDataProviderUtils.FIELD_CLIENT_NAME;
import static fintech.bo.db.jooq.lending.tables.Loan.LOAN;
import static fintech.bo.spain.db.jooq.asnef.tables.Log.LOG;
import static fintech.bo.spain.db.jooq.asnef.tables.LogRow.LOG_ROW;

@Component
public class AsnefComponents {

    public static final String LOG_TYPE_NOTIFICA_RP = "NOTIFICA_RP";

    public static final String LOG_TYPE_FOTOALTAS = "FOTOALTAS";

    public static final String LOG_STATUS_PREPARED = "PREPARED";

    @Autowired
    private DSLContext db;

    @Autowired
    private FileApiClient fileApiClient;

    @Autowired
    private AsnefQueries asnefQueries;

    @Autowired
    private AsnefApiClient asnefApiClient;

    @Autowired
    private JooqClientDataService jooqClientDataService;

    public Grid<LogRecord> grid(AsnefLogsDataProvider asnefLogsDataProvider, Refreshable refreshable) {
        JooqGridBuilder<LogRecord> builder = new JooqGridBuilder<>();
        gridActionButtons(builder, refreshable);
        gridColumns(builder);
        return builder.build(asnefLogsDataProvider);
    }

    public Grid<LogRecord> grid(AsnefLogsDataProvider asnefLogsDataProvider) {
        JooqGridBuilder<LogRecord> builder = new JooqGridBuilder<>();
        gridColumns(builder);
        return builder.build(asnefLogsDataProvider);
    }

    private void gridActionButtons(JooqGridBuilder<LogRecord> builder, Refreshable refreshable) {
        builder.addActionColumn("Rows", record -> {
            AsnefLogRowsDataProvider asnefLogRowsDataProvider = new AsnefLogRowsDataProvider(db, jooqClientDataService);
            asnefLogRowsDataProvider.setLogId(record.getId());

            Window dialog = new Window(record.getType());
            dialog.setContent(grid(asnefLogRowsDataProvider));
            dialog.setWidth(1000, Sizeable.Unit.PIXELS);
            dialog.setHeight(800, Sizeable.Unit.PIXELS);
            dialog.center();
            UI.getCurrent().addWindow(dialog);
        });
        builder.addComponentColumn(record -> generateExportButton(record, refreshable)).setWidth(80).setStyleGenerator(item -> BackofficeTheme.ACTION_ROW);
        builder.addComponentColumn(record -> generateDeleteButton(record, refreshable)).setWidth(80).setStyleGenerator(item -> BackofficeTheme.ACTION_ROW);
    }

    private void gridColumns(JooqGridBuilder<LogRecord> builder) {
        builder.addColumn(LOG.TYPE);
        builder.addColumn(LOG.STATUS);
        builder.addColumn(LOG.PREPARED_AT);
        builder.addColumn(LOG.EXPORTED_AT);
        builder.addColumn(LOG.RESPONSE_RECEIVED_AT);
        builder.addComponentColumn(log -> generateViewLink(log.getOutgoingFileId(), "outgoing.txt")).setWidth(300);
        builder.addComponentColumn(log -> generateViewLink(log.getIncomingFileId(), "incoming.txt")).setWidth(300);
        builder.addAuditColumns(LOG);
        builder.addColumn(LOAN.ID);
        builder.sortDesc(LOG.ID);
    }

    private com.vaadin.ui.Component grid(AsnefLogRowsDataProvider asnefLogRowsDataProvider) {
        JooqGridBuilder<Record> builder = new JooqGridBuilder<>();
        builder.addColumn(LOG_ROW.STATUS);
        builder.addLinkColumn(FIELD_CLIENT_NAME, r -> ClientComponents.clientLink(r.get(LOG_ROW.CLIENT_ID))).setCaption("Client");
        builder.addLinkColumn(LOAN.LOAN_NUMBER, r -> LoanComponents.loanLink(r.get(LOG_ROW.LOAN_ID))).setCaption("Loan");
        builder.addColumn(LOG_ROW.OUTGOING_ROW);
        builder.addColumn(LOG_ROW.INCOMING_ROW);

        Grid<Record> grid = builder.build(asnefLogRowsDataProvider);

        FooterRow footer = grid.appendFooterRow();
        FooterCell footerTotalCell = footer.join(grid.getColumns().stream().map(Grid.Column::getId).filter(Objects::nonNull).toArray(String[]::new));

        asnefLogRowsDataProvider.addSizeListener((size, moreRecords) -> footerTotalCell.setText("Total: " + size + (moreRecords ? "+" : "")));

        return grid;
    }

    public com.vaadin.ui.Component asnefTab(Long clientId) {
        AsnefLogsDataProvider dataProvider = new AsnefLogsDataProvider(db);
        dataProvider.setClientId(clientId);

        VerticalLayout layout = new VerticalLayout();
        asnefQueries.getLatestFotoaltasByClientId(clientId)
            .ifPresent(preparedAt -> layout.addComponent(new DangerLabel(String.format("Included in ASNEF DB. Latest file date: %s.", preparedAt))));

        layout.addComponent(grid(dataProvider));
        layout.setMargin(false);
        return layout;
    }

    public ComboBox<String> typeComboBox() {
        ComboBox<String> comboBox = new ComboBox<>();
        comboBox.setPlaceholder("Type");
        comboBox.setItems("NOTIFICA_RP", "FOTOALTAS");
        comboBox.setTextInputAllowed(false);
        comboBox.setWidth(200, Sizeable.Unit.PIXELS);
        return comboBox;
    }

    public ComboBox<String> statusComboBox() {
        ComboBox<String> comboBox = new ComboBox<>();
        comboBox.setPlaceholder("Status");
        comboBox.setItems("PREPARED", "UPLOADED", "RESPONSE_RECEIVED");
        comboBox.setTextInputAllowed(false);
        comboBox.setWidth(200, Sizeable.Unit.PIXELS);
        return comboBox;
    }

    public GenerateAsnefFileDialog generateAsnefFileDialog() {
        return new GenerateAsnefFileDialog(asnefApiClient);
    }

    public ImportAsnefFileDialog importAsnefFileDialog() {
        return new ImportAsnefFileDialog(fileApiClient, asnefApiClient);
    }

    private com.vaadin.ui.Component generateViewLink(Long fileId, String filename) {
        return Optional.ofNullable(fileId).map(f -> UrlUtils.generateViewLink(fileApiClient, new CloudFile(f, filename))).orElse(null);
    }

    private com.vaadin.ui.Component generateExportButton(LogRecord record, Refreshable refreshable) {
        ConfirmDialog dialog = new ConfirmDialog("Are you sure?", e -> {
            ExportAsnefFileRequest request = new ExportAsnefFileRequest();
            request.setLogId(record.getId());

            BackgroundOperations.callApi("Exporting asnef file", asnefApiClient.exportAsnefFile(request), t -> {
                Notifications.trayNotification("Asnef file exported");

                refreshable.refresh();
            }, Notifications::errorNotification);
        });

        Button button = new Button("Export");
        button.addStyleNames(ValoTheme.BUTTON_SMALL);
        button.setEnabled(LOG_STATUS_PREPARED.equals(record.getStatus()));
        button.addClickListener(e -> UI.getCurrent().addWindow(dialog));
        return button;
    }

    private com.vaadin.ui.Component generateDeleteButton(LogRecord record, Refreshable refreshable) {
        ConfirmDialog dialog = new ConfirmDialog("Are you sure?", e -> {
            BackgroundOperations.callApi("Deleting asnef file", asnefApiClient.deleteAsnefFile(record.getId()), t -> {
                Notifications.trayNotification("Asnef file deleted");

                refreshable.refresh();
            }, Notifications::errorNotification);
        });

        Button button = new Button("Delete");
        button.addStyleNames(ValoTheme.BUTTON_SMALL);
        button.setEnabled(LOG_STATUS_PREPARED.equals(record.getStatus()));
        button.addClickListener(e -> UI.getCurrent().addWindow(dialog));
        return button;
    }
}
