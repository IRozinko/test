package fintech.bo.spain.alfa.attachments;

import com.vaadin.icons.VaadinIcons;
import com.vaadin.ui.Button;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Component;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Upload;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.ValoTheme;
import fintech.bo.api.client.AttachmentApiClient;
import fintech.bo.api.client.FileApiClient;
import fintech.bo.api.model.IdResponse;
import fintech.bo.api.model.attachement.SaveAttachmentRequest;
import fintech.bo.components.background.BackgroundOperations;
import fintech.bo.components.dialogs.ActionDialog;
import fintech.bo.components.notifications.Notifications;
import fintech.bo.spain.alfa.AlfaBoConstants;
import lombok.extern.slf4j.Slf4j;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import org.apache.commons.lang3.StringUtils;
import retrofit2.Response;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;

@Slf4j
public class UploadAlfaAttachmentDialog extends ActionDialog {

    private Long clientId;
    private FileApiClient fileApiClient;
    private AttachmentApiClient attachmentApiClient;
    private Consumer<String> onUploadComplete;
    private VerticalLayout formHolder;
    private SimpleUploadForm form;

    public UploadAlfaAttachmentDialog(String caption, Long clientId, FileApiClient fileApiClient, AttachmentApiClient attachmentApiClient, Consumer<String> onUploadComplete) {
        super(caption, "Upload");
        this.clientId = clientId;
        this.fileApiClient = fileApiClient;
        this.attachmentApiClient = attachmentApiClient;
        this.onUploadComplete = onUploadComplete;
        setDialogContent(content());
        setModal(true);
        setWidth(500, Unit.PIXELS);
    }

    private Component content() {
        ComboBox<String> typeOfFile = new ComboBox<>("Type of File");
        typeOfFile.setWidth(80, Unit.PERCENTAGE);
        typeOfFile.setItems(
            AlfaBoConstants.ATTACHMENT_TYPE_ID_DOCUMENT,
            AlfaBoConstants.ATTACHMENT_TYPE_BANK_ACC_OWNERSHIP,
            AlfaBoConstants.ATTACHMENT_TYPE_OTHER
        );
        typeOfFile.setTextInputAllowed(false);
        typeOfFile.setEmptySelectionAllowed(false);
        typeOfFile.setItemCaptionGenerator(v -> StringUtils.capitalize(StringUtils.join(StringUtils.splitByCharacterTypeCamelCase(v), " ")));
        typeOfFile.addValueChangeListener(e -> renderForm(e.getValue()));

        formHolder = new VerticalLayout();
        formHolder.setMargin(false);

        VerticalLayout layout = new VerticalLayout();
        layout.addComponents(typeOfFile, formHolder);
        return layout;
    }

    private void renderForm(String attachmentType) {
        formHolder.removeAllComponents();
        form = new SimpleUploadForm(attachmentType);
        formHolder.addComponent(form);
        center();
    }

    @Override
    protected void executeAction() {
        if (form == null) {
            Notifications.errorNotification("Type of file not selected");
            return;
        }
        form.submit();
    }

    private static class FileReceiver implements Upload.Receiver {

        private List<UploadResult> uploadResults = new ArrayList<>();
        private UploadResult latestResult;

        @Override
        public OutputStream receiveUpload(String filename, String mimeType) {
            latestResult = new UploadResult();
            uploadResults.add(latestResult);
            return latestResult.os;
        }
    }

    private static class UploadResult {
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        String fileName;
        String mimeType;
    }

    private class SimpleUploadForm extends VerticalLayout {

        private final FileReceiver fileReceiver = new FileReceiver();
        private String attachmentType;

        public SimpleUploadForm(String attachmentType) {
            this.attachmentType = attachmentType;
            VerticalLayout fileListHolder = new VerticalLayout();
            fileListHolder.setMargin(false);

            Upload upload = new Upload("Select file", fileReceiver);
            upload.addSucceededListener(event -> {
                fileReceiver.latestResult.fileName = event.getFilename();
                fileReceiver.latestResult.mimeType = event.getMIMEType();

                refreshUploadedFileList(fileListHolder);
            });

            addComponent(upload);
            addComponent(fileListHolder);
            setMargin(false);
        }

        private void refreshUploadedFileList(VerticalLayout fileListHolder) {
            fileListHolder.removeAllComponents();

            fileReceiver.uploadResults.forEach(result -> {
                HorizontalLayout fileResultRow = new HorizontalLayout();
                fileResultRow.setMargin(false);

                Label fileNameLabel = new Label(result.fileName);
                Button button = new Button();
                button.addStyleNames(ValoTheme.BUTTON_ICON_ONLY, ValoTheme.BUTTON_TINY, ValoTheme.BUTTON_DANGER);
                button.setIcon(VaadinIcons.TRASH);
                button.addClickListener(c -> {
                    fileReceiver.uploadResults.remove(result);
                    refreshUploadedFileList(fileListHolder);
                });

                fileResultRow.addComponents(fileNameLabel, button);
                fileListHolder.addComponent(fileResultRow);
            });
        }

        void submit() {
            if (fileReceiver.uploadResults.isEmpty()) {
                Notifications.errorNotification("File not selected");
                return;
            }

            List<RequestBody> uploadRequestBody = fileReceiver
                .uploadResults
                .stream()
                .map(fileResult ->
                    (RequestBody) new MultipartBody.Builder()
                        .setType(MultipartBody.MIXED)
                        .addFormDataPart("file", fileResult.fileName, MultipartBody.create(MediaType.parse(fileResult.mimeType), fileResult.os.toByteArray()))
                        .build()
                )
                .collect(Collectors.toList());


            BackgroundOperations.run("Uploading document(s)", feedback -> {
                    uploadRequestBody
                        .stream()
                        .map(requestBody -> fileApiClient.upload(requestBody, "uploads"))
                        .map(uploadCall -> {
                            try {
                                Response<IdResponse> response = uploadCall.execute();
                                if (response.isSuccessful()) {
                                    return response.body();
                                } else {
                                    return BackgroundOperations.throwOnRequestFailed("Uploading document(s)", response);
                                }
                            } catch (IOException e) {
                                throw new RuntimeException(e);
                            }
                        })
                        .map(idResult -> {
                            SaveAttachmentRequest saveAttachmentRequest = new SaveAttachmentRequest();
                            saveAttachmentRequest.setClientId(clientId);
                            saveAttachmentRequest.setType(attachmentType);
                            saveAttachmentRequest.setFileId(idResult.getId());
                            return saveAttachmentRequest;
                        })
                        .map(request -> attachmentApiClient.saveAttachment(request))
                        .forEach(saveAttachmentCall -> {
                            try {
                                Response<Void> response = saveAttachmentCall.execute();
                                if (!response.isSuccessful()) {
                                    BackgroundOperations.throwOnRequestFailed("Uploading document(s)", response);
                                }
                            } catch (IOException e) {
                                throw new RuntimeException(e);
                            }
                        });
                    return true;
                },
                result -> {
                    Notifications.trayNotification("Document(s) uploaded");
                    if (onUploadComplete != null) {
                        onUploadComplete.accept(attachmentType);
                    }
                    close();
                },
                Notifications::errorNotification);
        }
    }
}
