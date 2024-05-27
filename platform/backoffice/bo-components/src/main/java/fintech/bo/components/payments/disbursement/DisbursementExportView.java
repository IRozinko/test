package fintech.bo.components.payments.disbursement;

import com.google.common.base.Throwables;
import com.vaadin.event.UIEvents;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.shared.Registration;
import com.vaadin.spring.annotation.SpringView;
import com.vaadin.ui.Button;
import com.vaadin.ui.Grid;
import com.vaadin.ui.StyleGenerator;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.renderers.ComponentRenderer;
import elemental.json.JsonObject;
import elemental.json.JsonValue;
import elemental.json.impl.JsonUtil;
import fintech.bo.api.client.DisbursementApiClient;
import fintech.bo.api.client.FileApiClient;
import fintech.bo.api.model.CloudFile;
import fintech.bo.api.model.disbursements.DisbursementExportResponse;
import fintech.bo.api.model.disbursements.ExportDisbursementsRequest;
import fintech.bo.components.BackofficeTheme;
import fintech.bo.components.layouts.GridViewLayout;
import fintech.bo.components.notifications.Notifications;
import fintech.bo.components.utils.CloudFileDownloader;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.jooq.Record;
import org.springframework.beans.factory.annotation.Autowired;
import retrofit2.Response;

import java.io.IOException;
import java.time.LocalDateTime;

import static fintech.bo.components.Formats.decimalRenderer;
import static fintech.bo.components.GridHelper.alignRightStyle;
import static fintech.bo.db.jooq.payment.tables.Institution.INSTITUTION;
import static fintech.bo.db.jooq.payment.tables.InstitutionAccount.INSTITUTION_ACCOUNT;
import static java.time.temporal.ChronoUnit.SECONDS;

@Slf4j
@SpringView(name = DisbursementExportView.NAME)
public class DisbursementExportView extends VerticalLayout implements View {

    private static final int AUTO_REFRESH_INTERVAL = 10;

    public static final String NAME = "disbursement-export";

    @Autowired
    private DisbursementComponents disbursementComponents;

    @Autowired
    private DisbursementApiClient disbursementApiClient;

    @Autowired
    private FileApiClient fileApiClient;

    private Grid<Record> grid;
    private LocalDateTime lastRefreshed = LocalDateTime.now();
    private Registration pollListener;

    @Override
    public void enter(ViewChangeListener.ViewChangeEvent event) {
        removeAllComponents();
        setCaption("Disbursement Export");
        grid = grid();

        GridViewLayout layout = new GridViewLayout();
        layout.setContent(grid);
        layout.setRefreshAction(e -> grid.getDataProvider().refreshAll());
        addComponentsAndExpand(layout);
    }

    public Grid<Record> grid() {
        Grid<Record> grid = new Grid<>();
        grid.addColumn(record -> {
            Button button = this.generateDownloadBtn(record, grid);
            button.setEnabled(record.get(DisbursementExportDataProvider.PENDING_DISBURSEMENT_COUNT) > 0);
            return button;
        }, new ComponentRenderer()).setStyleGenerator(item -> BackofficeTheme.ACTION_ROW).setWidth(120).setSortable(false);
        grid.addColumn(record -> record.get(INSTITUTION.NAME)).setCaption("Bank").setId(INSTITUTION.NAME.getName()).setWidth(200);
        grid.addColumn(record -> record.get(INSTITUTION_ACCOUNT.ACCOUNT_NUMBER)).setCaption("Account").setId(INSTITUTION_ACCOUNT.ACCOUNT_NUMBER.getName()).setWidth(200);
        grid.addColumn(record -> record.get(DisbursementExportDataProvider.PENDING_DISBURSEMENT_COUNT)).setCaption("Pending disbursements").setId(DisbursementExportDataProvider.PENDING_DISBURSEMENT_COUNT.getName()).setStyleGenerator(pendingColumnStyle()).setWidth(200);
        grid.addColumn(record -> record.get(DisbursementExportDataProvider.PENDING_DISBURSEMENT_AMOUNT)).setCaption("Amount").setId(DisbursementExportDataProvider.PENDING_DISBURSEMENT_AMOUNT.getName()).setRenderer(decimalRenderer()).setStyleGenerator(alignRightStyle()).setStyleGenerator(pendingColumnStyle());
        grid.setRowHeight(35);
        grid.setDataProvider(disbursementComponents.exportDataProvider());
        grid.setSelectionMode(Grid.SelectionMode.NONE);
        return grid;
    }

    private StyleGenerator<Record> pendingColumnStyle() {
        return item -> {
            Integer pending = item.get(DisbursementExportDataProvider.PENDING_DISBURSEMENT_COUNT);
            if (pending > 0) {
                return BackofficeTheme.TEXT_ACTIVE;
            } else {
                return BackofficeTheme.TEXT_GRAY;
            }
        };
    }

    private Button generateDownloadBtn(Record record, Grid<Record> grid) {
        Button exportBtn = new Button("Export");

        if (StringUtils.isNotBlank(record.get(INSTITUTION.STATEMENT_API_EXPORTER))) {
            exportBtn.addClickListener(event -> {
                DisbursementExportResponse response = sendExportRequest(record.get(INSTITUTION_ACCOUNT.INSTITUTION_ID),
                    record.get(INSTITUTION_ACCOUNT.ID));
                Notifications.trayNotification("Disbursements exported");
                grid.getDataProvider().refreshAll();
            });
        } else {
            CloudFileDownloader onDemandFileDownloader = new CloudFileDownloader(fileApiClient, () -> {
                DisbursementExportResponse response = sendExportRequest(record.get(INSTITUTION_ACCOUNT.INSTITUTION_ID), record.get(INSTITUTION_ACCOUNT.ID));
                return new CloudFile(response.getFileId(), response.getFileName());
            }, cloudFile -> {
                Notifications.trayNotification("File downloaded: " + cloudFile.getName());
                grid.getDataProvider().refreshAll();
            });
            onDemandFileDownloader.extend(exportBtn);
        }
        return exportBtn;
    }

    private DisbursementExportResponse sendExportRequest(Long institutionId, Long institutionAccountId) {
        ExportDisbursementsRequest request = new ExportDisbursementsRequest();
        request.setInstitutionId(institutionId);
        request.setInstitutionAccountId(institutionAccountId);
        try {
            Response<DisbursementExportResponse> exportResponse = disbursementApiClient.export(request).execute();
            if (!exportResponse.isSuccessful()) {
                String errorBody = exportResponse.errorBody().string();
                log.error("Disbursement export API request failed: {}", errorBody);
                JsonValue errorMessage = ((JsonObject) JsonUtil.parse(errorBody)).get("message");
                throw new RuntimeException(errorMessage.asString());
            }
            return exportResponse.body();
        } catch (IOException e) {
            log.error("Disbursement export failed", e);
            throw Throwables.propagate(e);
        }
    }

    @Override
    public void attach() {
        super.attach();
        pollListener = getUI().addPollListener((UIEvents.PollListener) event -> {
            if (lastRefreshedInSeconds() > AUTO_REFRESH_INTERVAL) {
                lastRefreshed = LocalDateTime.now();
                try {
                    grid.getDataProvider().refreshAll();
                } catch (Exception e) {
                    log.warn("Failed to refresh disbursement view", e);
                }
            }
        });
    }

    @Override
    public void detach() {
        super.detach();
        if (pollListener != null) {
            pollListener.remove();
            pollListener = null;
        }
    }


    private long lastRefreshedInSeconds() {
        return SECONDS.between(lastRefreshed, LocalDateTime.now());
    }
}
