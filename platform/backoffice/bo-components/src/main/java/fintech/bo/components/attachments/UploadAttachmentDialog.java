package fintech.bo.components.attachments;

import com.vaadin.server.Sizeable;
import com.vaadin.ui.Component;
import com.vaadin.ui.Label;
import com.vaadin.ui.Upload;
import com.vaadin.ui.VerticalLayout;
import fintech.bo.api.client.AttachmentApiClient;
import fintech.bo.api.client.FileApiClient;
import fintech.bo.api.model.IdResponse;
import fintech.bo.api.model.attachement.SaveAttachmentRequest;
import fintech.bo.components.background.BackgroundOperations;
import fintech.bo.components.dialogs.ActionDialog;
import fintech.bo.components.notifications.Notifications;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;

public class UploadAttachmentDialog extends ActionDialog implements Upload.Receiver, Upload.SucceededListener {

    private ByteArrayOutputStream os;
    private Label fileInfo;
    private Upload.SucceededEvent fileUploadEvent;
    private Long clientId;
    private String attachmentType;
    private FileApiClient fileApiClient;
    private AttachmentApiClient attachmentApiClient;

    public UploadAttachmentDialog(String caption, Long clientId, String attachmentType, FileApiClient fileApiClient, AttachmentApiClient attachmentApiClient) {
        super(caption, "Upload");
        this.clientId = clientId;
        this.attachmentType = attachmentType;
        this.fileApiClient = fileApiClient;
        this.attachmentApiClient = attachmentApiClient;
        setDialogContent(content());
        setModal(true);
        setWidth(400, Sizeable.Unit.PIXELS);
    }

    private Component content() {
        Upload upload = new Upload("Select file", this);
        upload.addSucceededListener(this);

        fileInfo = new Label();

        VerticalLayout layout = new VerticalLayout();
        layout.addComponents(upload, fileInfo);
        return layout;
    }

    @Override
    public OutputStream receiveUpload(String filename, String mimeType) {
        this.os = new ByteArrayOutputStream();
        return this.os;
    }

    @Override
    public void uploadSucceeded(Upload.SucceededEvent event) {
        this.fileInfo.setValue(event.getFilename());
        this.fileUploadEvent = event;
    }

    @Override
    protected void executeAction() {
        if (fileUploadEvent == null) {
            Notifications.errorNotification("File not selected");
            return;
        }
        RequestBody body = new MultipartBody.Builder()
            .setType(MultipartBody.MIXED)
            .addFormDataPart("file", fileUploadEvent.getFilename(), MultipartBody.create(MediaType.parse(fileUploadEvent.getMIMEType()), os.toByteArray()))
            .build();
        Call<IdResponse> call = fileApiClient.upload(body, "uploads");
        BackgroundOperations.callApi("Uploading document", call, t -> {
            SaveAttachmentRequest saveAttachmentRequest = new SaveAttachmentRequest();
            saveAttachmentRequest.setClientId(clientId);
            saveAttachmentRequest.setType(attachmentType);
            saveAttachmentRequest.setFileId(t.getId());
            Call<Void> saveAttachmentCall = attachmentApiClient.saveAttachment(saveAttachmentRequest);
            BackgroundOperations.callApi("Saving attachment", saveAttachmentCall, s -> {
                Notifications.trayNotification("Attachment saved");
                close();
            }, Notifications::errorNotification);
        }, Notifications::errorNotification);
    }
}
