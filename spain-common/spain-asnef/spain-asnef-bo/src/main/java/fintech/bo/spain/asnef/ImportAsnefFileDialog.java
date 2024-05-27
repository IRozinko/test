package fintech.bo.spain.asnef;

import com.vaadin.data.Binder;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Component;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Upload;
import fintech.bo.api.client.FileApiClient;
import fintech.bo.api.model.IdResponse;
import fintech.bo.components.background.BackgroundOperations;
import fintech.bo.components.dialogs.ActionDialog;
import fintech.bo.components.notifications.Notifications;
import fintech.bo.spain.asnef.api.AsnefApiClient;
import fintech.bo.spain.asnef.model.ImportAsnefFileRequest;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;

public class ImportAsnefFileDialog extends ActionDialog implements Upload.Receiver, Upload.SucceededListener {

    private final FileApiClient fileApiClient;

    private final AsnefApiClient asnefApiClient;

    private final ImportAsnefFileRequest request;

    private Label fileInfo;

    private ByteArrayOutputStream content;

    private Upload.SucceededEvent fileUploadEvent;

    public ImportAsnefFileDialog(FileApiClient fileApiClient, AsnefApiClient asnefApiClient) {
        super("Upload asnef file", "Upload");

        this.fileApiClient = fileApiClient;
        this.asnefApiClient = asnefApiClient;
        this.request = new ImportAsnefFileRequest();

        setDialogContent(build());
        setWidth(600, Unit.PIXELS);
    }

    private Component build() {
        Binder<ImportAsnefFileRequest> binder = new Binder<>(ImportAsnefFileRequest.class);
        binder.setBean(request);

        ComboBox<String> type = new ComboBox<>("Select type");
        type.setItems(AsnefComponents.LOG_TYPE_NOTIFICA_RP, AsnefComponents.LOG_TYPE_FOTOALTAS);
        type.setTextInputAllowed(false);
        type.setEmptySelectionAllowed(false);
        type.setRequiredIndicatorVisible(true);
        type.setWidth(100, Unit.PERCENTAGE);
        binder.bind(type, ImportAsnefFileRequest::getType, ImportAsnefFileRequest::setType);

        Upload upload = new Upload("Select file", this);
        upload.addSucceededListener(this);

        fileInfo = new Label();

        FormLayout formLayout = new FormLayout();
        formLayout.addComponents(type, upload, fileInfo);
        return formLayout;
    }

    @Override
    public OutputStream receiveUpload(String filename, String mimeType) {
        this.content = new ByteArrayOutputStream();
        return this.content;
    }

    @Override
    public void uploadSucceeded(Upload.SucceededEvent event) {
        this.fileInfo.setValue(event.getFilename());
        this.fileUploadEvent = event;
    }

    @Override
    protected void executeAction() {
        if (request.getType() == null) {
            Notifications.errorNotification("Type not selected");
            return;
        }

        if (fileUploadEvent == null) {
            Notifications.errorNotification("File not selected");
            return;
        }

        RequestBody body = new MultipartBody.Builder()
            .setType(MultipartBody.MIXED)
            .addFormDataPart("file", fileUploadEvent.getFilename(), MultipartBody.create(MediaType.parse(fileUploadEvent.getMIMEType()), content.toByteArray()))
            .build();

        Call<IdResponse> call = fileApiClient.upload(body, "uploads");

        BackgroundOperations.callApi("Uploading file", call, t -> {
            request.setFileId(t.getId());

            BackgroundOperations.callApi("Importing asnef file", asnefApiClient.importAsnefFile(request), s -> {
                Notifications.trayNotification("Asnef file imported");

                close();
            }, Notifications::errorNotification);
        }, Notifications::errorNotification);
    }
}
