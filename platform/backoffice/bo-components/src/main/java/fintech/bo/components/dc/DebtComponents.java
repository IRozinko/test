package fintech.bo.components.dc;

import com.vaadin.server.Sizeable;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Grid;
import com.vaadin.ui.TextField;
import com.vaadin.ui.Upload;
import com.vaadin.ui.renderers.ComponentRenderer;
import com.vaadin.ui.themes.ValoTheme;
import fintech.bo.api.client.DcApiClient;
import fintech.bo.api.client.FileApiClient;
import fintech.bo.api.model.IdResponse;
import fintech.bo.api.model.dc.ImportDebtRequest;
import fintech.bo.api.model.dc.ImportDebtResponse;
import fintech.bo.components.BackofficeTheme;
import fintech.bo.components.notifications.Notifications;
import lombok.Data;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import org.jooq.DSLContext;
import org.jooq.Record;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import retrofit2.Call;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;

import static fintech.bo.components.background.BackgroundOperations.callApi;
import static fintech.bo.db.jooq.dc.Tables.DEBT_IMPORT;

@Slf4j
@Component
public class DebtComponents {

    @Autowired
    private FileApiClient fileApiClient;

    @Autowired
    private DcApiClient dcApiClient;
    @Autowired
    private DSLContext db;
    private DebtPortfolio debtPortfolio;

    public Grid<Record> institutionGrid() {
        Grid<Record> grid = new Grid<>();
        grid.addColumn(record -> this.generateUploadBtn(record, grid), new ComponentRenderer()).setStyleGenerator(item -> BackofficeTheme.ACTION_ROW).setWidth(120).setSortable(false);
        grid.addComponentColumn(record -> {
            ComboBox<String> comboBox = new ComboBox<>();
            comboBox.setPlaceholder("Debt state");
            comboBox.setItems(DebtState.states);
            comboBox.setEmptySelectionAllowed(false);
            comboBox.setTextInputAllowed(false);
            comboBox.setWidth(200, Sizeable.Unit.PIXELS);
            comboBox.addValueChangeListener(event -> {
                debtPortfolio.setState(event.getValue());
            });
            return comboBox;
        }).setWidth(210);
        grid.addComponentColumn(record -> {
            ComboBox<String> comboBox = new ComboBox<>();
            comboBox.setPlaceholder("Debt status");
            comboBox.setItems(DebtStatus.statuses);
            comboBox.setEmptySelectionAllowed(false);
            comboBox.setTextInputAllowed(false);
            comboBox.setWidth(200, Sizeable.Unit.PIXELS);
            comboBox.addValueChangeListener(event -> {
                debtPortfolio.setStatus(event.getValue());
            }
            );
            return comboBox;
        }).setWidth(210);
        grid.addComponentColumn(item -> {
            TextField portfolio = new TextField("Portfolio name");
            debtPortfolio = new DebtPortfolio();
            portfolio.setValue("");
            portfolio.addValueChangeListener(event -> {
                debtPortfolio.setName(event.getValue());
            });
            return portfolio;
        }).setCaption("Portfolio name").setWidth(250);
        grid.addColumn(record -> record.get(DEBT_IMPORT.NAME)).setCaption("Company").setId("COMPANY").setWidth(300).setSortable(false);
        grid.setSelectionMode(Grid.SelectionMode.NONE);
        grid.setDataProvider(new DebtImportDataProvider(db));
        grid.setRowHeight(35);
        grid.setSizeFull();
//        grid.addColumn(record -> this.generateUploadDocBtn(record, grid), new ComponentRenderer()).setStyleGenerator(item -> BackofficeTheme.ACTION_ROW).setWidth(120).setSortable(false).setCaption("Import Documents");
        return grid;
    }
    @Data
    @Accessors(chain = true)
    private static class DebtPortfolio {
        private String name;
        private String state;
        private String status;
    }
    private class FileUploader implements Upload.Receiver, Upload.SucceededListener {

        private final Record record;
        private ByteArrayOutputStream bos;
        private final Long institutionId;
        private final Grid<Record> grid;

        public FileUploader(Long institutionId, Grid<Record> grid, Record record) {
            this.institutionId = institutionId;
            this.grid = grid;
            this.record = record;
        }

        @Override
        public OutputStream receiveUpload(String filename, String mimeType) {
            bos = new ByteArrayOutputStream();
            return bos;
        }

        @Override
        public void uploadSucceeded(Upload.SucceededEvent event) {
            RequestBody body = new MultipartBody.Builder()
                .setType(MultipartBody.MIXED)
                .addFormDataPart("file", event.getFilename(), MultipartBody.create(MediaType.parse(event.getMIMEType()), bos.toByteArray()))
                .build();
            this.bos = null;
            if (debtPortfolio.getState() == null || debtPortfolio.getStatus() == null) {
                throw new IllegalStateException("need to set debt's status and state");
            }
            Call<IdResponse> call = fileApiClient.upload(body, "debts");
            callApi("Uploading debts", call, t -> {
                ImportDebtRequest importDebtRequest = new ImportDebtRequest();
                importDebtRequest.setFileId(t.getId());
                importDebtRequest.setInstitutionId(institutionId);
                importDebtRequest.setCompanyName(record.get(DEBT_IMPORT.NAME));
                importDebtRequest.setPortfolioName(debtPortfolio.getName());
                importDebtRequest.setDebtState(debtPortfolio.getState());
                importDebtRequest.setDebtStatus(debtPortfolio.getStatus());
                Call<ImportDebtResponse> importDebtCall = dcApiClient.importDebts(importDebtRequest);
                callApi("Importing debts", importDebtCall, s -> {
                    Notifications.trayNotification("debts processed:"+s.getProcessedCount()+", total: "+ s.getTotalCount());
                    grid.getDataProvider().refreshAll();
                }, Notifications::errorNotification);

            }, Notifications::errorNotification);
        }
    }

    private Upload generateUploadBtn(Record record, Grid<Record> grid) {
        FileUploader uploader = new FileUploader(record.get(DEBT_IMPORT.ID), grid, record);
        Upload sample = new Upload("Upload caption", uploader);
        sample.setImmediateMode(true);
        sample.addSucceededListener(uploader);
        sample.addStyleName(ValoTheme.BUTTON_SMALL);
        return sample;
    }

    private Upload generateUploadDocBtn(Record record, Grid<Record> grid) {
        FileUploader uploader = new FileUploader(record.get(DEBT_IMPORT.ID), grid, record);
        Upload sample = new Upload("Upload caption", uploader);
        sample.setImmediateMode(true);
        sample.addSucceededListener(uploader);
        sample.addStyleName(ValoTheme.BUTTON_SMALL);
        return sample;
    }


}
