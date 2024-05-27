package fintech.bo.components.payments.statement;

import com.vaadin.ui.Grid;
import com.vaadin.ui.Upload;
import com.vaadin.ui.renderers.ComponentRenderer;
import com.vaadin.ui.themes.ValoTheme;
import fintech.bo.api.client.FileApiClient;
import fintech.bo.api.client.StatementApiClient;
import fintech.bo.api.model.IdResponse;
import fintech.bo.api.model.statements.ImportStatementRequest;
import fintech.bo.components.BackofficeTheme;
import fintech.bo.components.Formats;
import fintech.bo.components.notifications.Notifications;
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
import static fintech.bo.db.jooq.payment.tables.Institution.INSTITUTION;

@Slf4j
@Component
public class StatementComponents {


    @Autowired
    private FileApiClient fileApiClient;

    @Autowired
    private StatementApiClient statementApiClient;

    @Autowired
    private DSLContext db;

    public Grid<Record> institutionGrid() {
        Grid<Record> grid = new Grid<>();
        grid.addColumn(record -> this.generateUploadBtn(record, grid), new ComponentRenderer()).setStyleGenerator(item -> BackofficeTheme.ACTION_ROW).setWidth(120).setSortable(false);
        grid.addColumn(record -> record.get(INSTITUTION.NAME)).setCaption("Bank").setId("BANK").setWidth(300).setSortable(false);
        grid.addColumn(record -> Formats.formatDateTime(record.get(StatementUploadDataProvider.LAST_STATEMENT_IMPORTED))).setCaption("Last uploaded").setId("LAST_STATEMENT_IMPORTED").setSortable(false);
        grid.setSelectionMode(Grid.SelectionMode.NONE);
        grid.setDataProvider(new StatementUploadDataProvider(db));
        grid.setRowHeight(35);
        grid.setSizeFull();
        return grid;
    }

    private class FileUploader implements Upload.Receiver, Upload.SucceededListener {

        private ByteArrayOutputStream bos;
        private final Long institutionId;
        private final Grid<Record> grid;

        public FileUploader(Long institutionId, Grid<Record> grid) {
            this.institutionId = institutionId;
            this.grid = grid;
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

            Call<IdResponse> call = fileApiClient.upload(body, "statements");
            callApi("Uploading statements", call, t -> {
                ImportStatementRequest importStatementRequest = new ImportStatementRequest();
                importStatementRequest.setFileId(t.getId());
                importStatementRequest.setInstitutionId(institutionId);
                Call<IdResponse> importStatementCall = statementApiClient.importStatement(importStatementRequest);
                callApi("Importing statement", importStatementCall, s -> {
                    Notifications.trayNotification("Statement imported");
                    grid.getDataProvider().refreshAll();
                }, Notifications::errorNotification);

            }, Notifications::errorNotification);
        }
    }

    private Upload generateUploadBtn(Record record, Grid<Record> grid) {
        FileUploader uploader = new FileUploader(record.get(INSTITUTION.ID), grid);
        Upload sample = new Upload("Upload caption", uploader);
        sample.setImmediateMode(true);
        sample.addSucceededListener(uploader);
        sample.addStyleName(ValoTheme.BUTTON_SMALL);
        return sample;
    }
}
