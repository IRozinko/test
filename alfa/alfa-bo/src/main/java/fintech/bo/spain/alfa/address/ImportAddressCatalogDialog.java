package fintech.bo.spain.alfa.address;

import com.vaadin.ui.FormLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Upload;
import fintech.bo.api.client.FileApiClient;
import fintech.bo.api.model.IdResponse;
import fintech.bo.api.model.address.ImportAddressCatalogRequest;
import fintech.bo.components.dialogs.ActionDialog;
import fintech.bo.components.notifications.Notifications;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;

import java.io.ByteArrayOutputStream;

import static fintech.bo.components.background.BackgroundOperations.callApi;

class ImportAddressCatalogDialog extends ActionDialog {

    private final ImportAddressCatalogRequest request;

    private final Upload uploadButton;

    private final Label label;

    ImportAddressCatalogDialog(FileApiClient fileApiClient) {
        super("Import address catalog", "Import");

        this.request = new ImportAddressCatalogRequest();

        uploadButton = uploadButton(fileApiClient);
        label = new Label();

        FormLayout layout = new FormLayout();
        layout.addComponents(uploadButton, label);
        setDialogContent(layout);
        setWidth(500, Unit.PIXELS);
    }

    private Upload uploadButton(FileApiClient fileApiClient) {
        ByteArrayOutputStream output = new ByteArrayOutputStream();

        Upload upload = new Upload("", (Upload.Receiver) (filename, mimeType) -> output);
        upload.addFinishedListener(event -> {
            RequestBody body = new MultipartBody.Builder()
                .setType(MultipartBody.MIXED)
                .addFormDataPart("file", event.getFilename(), MultipartBody.create(MediaType.parse(event.getMIMEType()), output.toByteArray()))
                .build();

            Call<IdResponse> call = fileApiClient.upload(body, "address_catalog");

            callApi("Uploading address catalog", call, t -> {
                request.setFileId(t.getId());

                uploadButton.setEnabled(false);
                label.setValue(event.getFilename());
            }, Notifications::errorNotification);
        });

        return upload;
    }

    ImportAddressCatalogRequest getRequest() {
        return request;
    }
}
