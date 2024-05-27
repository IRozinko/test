package fintech.bo.components.payments.disbursement;

import com.google.common.base.Throwables;
import com.vaadin.ui.*;
import elemental.json.JsonObject;
import elemental.json.JsonValue;
import elemental.json.impl.JsonUtil;
import fintech.bo.api.client.DisbursementApiClient;
import fintech.bo.api.client.FileApiClient;
import fintech.bo.api.model.CloudFile;
import fintech.bo.api.model.disbursements.DisbursementExportResponse;
import fintech.bo.api.model.disbursements.ExportSingleDisbursementRequest;
import fintech.bo.components.BackofficeTheme;
import fintech.bo.components.JooqGridBuilder;
import fintech.bo.components.PropertyLayout;
import fintech.bo.components.client.JooqClientDataService;
import fintech.bo.components.notifications.Notifications;
import fintech.bo.components.utils.CloudFileDownloader;
import lombok.SneakyThrows;
import org.jooq.DSLContext;
import org.jooq.Record;
import org.springframework.beans.factory.annotation.Autowired;
import retrofit2.Response;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static fintech.bo.components.payments.disbursement.DisbursementConstants.STATUS_DETAIL_CANCELLED;
import static fintech.bo.components.payments.disbursement.DisbursementConstants.STATUS_DETAIL_ERROR;
import static fintech.bo.components.payments.disbursement.DisbursementConstants.STATUS_DETAIL_EXPORTED;
import static fintech.bo.components.payments.disbursement.DisbursementConstants.STATUS_DETAIL_EXPORT_ERROR;
import static fintech.bo.components.payments.disbursement.DisbursementConstants.STATUS_DETAIL_INVALID;
import static fintech.bo.components.payments.disbursement.DisbursementConstants.STATUS_DETAIL_PENDING;
import static fintech.bo.components.payments.disbursement.DisbursementConstants.STATUS_DETAIL_SETTLED;
import static fintech.bo.db.jooq.lending.tables.Loan.LOAN;
import static fintech.bo.db.jooq.payment.tables.Disbursement.DISBURSEMENT;

public abstract class DisbursementComponents {

    private static final Map<String, String> STATUS_STYLE_MAP = new HashMap<>();

    static {
        STATUS_STYLE_MAP.put(STATUS_DETAIL_EXPORTED, BackofficeTheme.TEXT_ACTIVE);
        STATUS_STYLE_MAP.put(STATUS_DETAIL_CANCELLED, BackofficeTheme.TEXT_GRAY);
        STATUS_STYLE_MAP.put(STATUS_DETAIL_INVALID, BackofficeTheme.TEXT_DANGER);
        STATUS_STYLE_MAP.put(STATUS_DETAIL_ERROR, BackofficeTheme.TEXT_DANGER);
        STATUS_STYLE_MAP.put(STATUS_DETAIL_SETTLED, BackofficeTheme.TEXT_SUCCESS);
    }

    @Autowired
    private DSLContext db;

    @Autowired
    private FileApiClient fileApiClient;

    @Autowired
    private DisbursementApiClient disbursementApiClient;

    @Autowired
    private JooqClientDataService jooqClientDataService;

    public DisbursementDataProvider dataProvider() {
        return new DisbursementDataProvider(db, jooqClientDataService);
    }

    public DisbursementExportDataProvider exportDataProvider() {
        return new DisbursementExportDataProvider(db);
    }

    public Grid<Record> grid(DisbursementDataProvider dataProvider) {
        JooqGridBuilder<Record> builder = new JooqGridBuilder<>();
        addGridColumns(builder);
        builder.sortDesc(LOAN.CREATED_AT);
        return builder.build(dataProvider);
    }

    protected abstract void addGridColumns(JooqGridBuilder<Record> builder);

    protected Component exportButton(Record record, Runnable andNext) {
        String disbursementStatusDetail = record.get(DISBURSEMENT.STATUS_DETAIL);
        if (STATUS_DETAIL_PENDING.equals(disbursementStatusDetail))
            return generateExportButton(record, andNext);
        else if (STATUS_DETAIL_ERROR.equals(disbursementStatusDetail) || STATUS_DETAIL_EXPORT_ERROR.equals(disbursementStatusDetail))
            return generateRetryButton(record, andNext);
        else return empty();
    }

    protected Component voidButton(Record record, Runnable andNext) {
        String disbursementStatusDetail = record.get(DISBURSEMENT.STATUS_DETAIL);
        if (STATUS_DETAIL_PENDING.equals(disbursementStatusDetail) || STATUS_DETAIL_ERROR.equals(disbursementStatusDetail))
            return generateVoidButton(record, andNext);
        else return empty();
    }

    private Component empty() {
        return new Label();
    }

    private Button generateVoidButton(Record record, Runnable andThen) {
        Button.ClickListener listener = event -> {
            sendVoidRequest(record.get(DISBURSEMENT.ID));
            Notifications.trayNotification("Disbursement voided");
            andThen.run();
        };
        return generateButton("Void", listener);
    }

    private Button generateRetryButton(Record record, Runnable andThen) {
        Button.ClickListener listener = event -> {
            sendRetryRequest(record.get(DISBURSEMENT.ID));
            Notifications.trayNotification("Disbursement retried");
            andThen.run();
        };
        return generateButton("Retry", listener);
    }

    private Button generateButton(String caption, Button.ClickListener clickListener) {
        Button btn = new Button(caption);
        btn.addClickListener(clickListener);
        return btn;
    }

    private Button generateExportButton(Record record, Runnable andThen) {
        Button exportBtn = new Button("Export");
        if (record.get(DISBURSEMENT.API_EXPORT)) {
            exportBtn.addClickListener(event -> {
                sendExportRequest(record.get(DISBURSEMENT.ID));
                Notifications.trayNotification("Disbursement exported");
                andThen.run();
            });
        } else {
            CloudFileDownloader onDemandFileDownloader = new CloudFileDownloader(fileApiClient, () -> {
                DisbursementExportResponse response = sendExportRequest(record.get(DISBURSEMENT.ID));
                return new CloudFile(response.getFileId(), response.getFileName());
            }, cloudFile -> {
                Notifications.trayNotification("File downloaded: " + cloudFile.getName());
                andThen.run();
            });
            onDemandFileDownloader.extend(exportBtn);
        }
        return exportBtn;
    }

    private DisbursementExportResponse sendExportRequest(Long disbursementId) {
        ExportSingleDisbursementRequest request = new ExportSingleDisbursementRequest();
        request.setDisbursementId(disbursementId);
        try {
            Response<DisbursementExportResponse> exportResponse = disbursementApiClient.exportSingle(request).execute();
            if (!exportResponse.isSuccessful()) {
                String errorBody = exportResponse.errorBody().string();
                JsonValue errorMessage = ((JsonObject) JsonUtil.parse(errorBody)).get("message");
                throw new RuntimeException(errorMessage.asString());
            }
            return exportResponse.body();
        } catch (IOException e) {
            throw Throwables.propagate(e);
        }
    }

    @SneakyThrows
    private void sendVoidRequest(Long disbursementId) {
        Response<Void> response = disbursementApiClient.voidSingle(disbursementId).execute();
        if (!response.isSuccessful()) {
            String errorBody = response.errorBody().string();
            JsonValue errorMessage = ((JsonObject) JsonUtil.parse(errorBody)).get("message");
            throw new RuntimeException(errorMessage.asString());
        }
    }

    @SneakyThrows
    private void sendRetryRequest(Long disbursementId) {
        Response<Void> response = disbursementApiClient.retrySingle(disbursementId).execute();
        if (!response.isSuccessful()) {
            String errorBody = response.errorBody().string();
            JsonValue errorMessage = ((JsonObject) JsonUtil.parse(errorBody)).get("message");
            throw new RuntimeException(errorMessage.asString());
        }
    }

    public ComboBox<String> statusComboBox() {
        ComboBox<String> comboBox = new ComboBox<>();
        comboBox.setPlaceholder("Disbursement status");
        comboBox.setItems(DisbursementConstants.ALL_STATUS_DETAILS);
        comboBox.setTextInputAllowed(false);
        return comboBox;
    }

    public SelectDisbursementDialog selectDisbursementDialog() {
        return new SelectDisbursementDialog(this);
    }

    public PropertyLayout disbursementInfo(Record disbursement) {
        PropertyLayout layout = new PropertyLayout("Disbursement");
        layout.add("Id", disbursement.get(DISBURSEMENT.ID));
        layout.add("Status", disbursement.get(DISBURSEMENT.STATUS));
        layout.add("Amount", disbursement.get(DISBURSEMENT.AMOUNT));
        layout.add("Date", disbursement.get(DISBURSEMENT.VALUE_DATE));
        layout.add("Loan number", disbursement.get(LOAN.LOAN_NUMBER));
        layout.add("Created at", disbursement.get(DISBURSEMENT.CREATED_AT));
        return layout;
    }

    protected StyleGenerator<Record> statusStyle() {
        return item -> {
            String status = item.get(DISBURSEMENT.STATUS_DETAIL);
            return STATUS_STYLE_MAP.getOrDefault(status, "");
        };
    }

    protected Link generateDownloadLink(Record record) {
        Link downloadLink = new Link();
        downloadLink.setCaption(record.get(DISBURSEMENT.EXPORTED_FILE_NAME));

        CloudFile cloudFile = new CloudFile(record.get(DISBURSEMENT.EXPORTED_CLOUD_FILE_ID), record.get(DISBURSEMENT.EXPORTED_FILE_NAME));
        CloudFileDownloader onDemandFileDownloader = new CloudFileDownloader(fileApiClient, () -> cloudFile, file -> {
            Notifications.trayNotification("File downloaded: " + file.getName());
        });
        onDemandFileDownloader.extend(downloadLink);

        return downloadLink;
    }
}
