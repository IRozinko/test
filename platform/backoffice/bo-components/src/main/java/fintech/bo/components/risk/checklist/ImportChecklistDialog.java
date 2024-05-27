package fintech.bo.components.risk.checklist;

import com.vaadin.data.Binder;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.Upload;
import fintech.bo.api.client.FileApiClient;
import fintech.bo.api.model.IdResponse;
import fintech.bo.api.model.risk.checklist.ImportChecklistRequest;
import fintech.bo.components.dialogs.ActionDialog;
import fintech.bo.components.notifications.Notifications;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;

import java.io.ByteArrayOutputStream;
import java.util.List;

import static fintech.bo.components.background.BackgroundOperations.callApi;

public class ImportChecklistDialog extends ActionDialog {

    private Binder<ImportChecklistRequest> binder = new Binder<>();
    private final Upload uploadButton;

    public ImportChecklistDialog(List<String> types, FileApiClient fileApiClient) {
        super("Edit Checklist", "Import");
        setWidth(500, Unit.PIXELS);


        ComboBox<String> typeField = new ComboBox<>();
        typeField.setWidth(100, Unit.PERCENTAGE);
        typeField.setPlaceholder("Type");
        typeField.setItems(types);
        typeField.setEmptySelectionAllowed(false);
        typeField.setTextInputAllowed(false);

        CheckBox override = new CheckBox("Override");
        override.setWidth(100, Unit.PERCENTAGE);
        override.setDescription("Override present values with imported ones");

        binder.bind(typeField, ImportChecklistRequest::getType, ImportChecklistRequest::setType);
        binder.bind(override, ImportChecklistRequest::isOverride, ImportChecklistRequest::setOverride);


        ImportChecklistRequest request = new ImportChecklistRequest();
        binder.setBean(request);

        uploadButton = generateUploadBtn(fileApiClient);

        FormLayout layout = new FormLayout();
        layout.addComponents(typeField, override, uploadButton);
        setDialogContent(layout);
    }

    public ImportChecklistRequest getRequest() {
        return binder.getBean();
    }


    private Upload generateUploadBtn(FileApiClient fileApiClient) {

        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        Upload sample = new Upload("", (Upload.Receiver) (filename, mimeType) -> bos);

        sample.addFinishedListener(event -> {
            RequestBody body = new MultipartBody.Builder()
                .setType(MultipartBody.MIXED)
                .addFormDataPart("file", event.getFilename(), MultipartBody.create(MediaType.parse(event.getMIMEType()), bos.toByteArray()))
                .build();

            Call<IdResponse> call = fileApiClient.upload(body, "statements");
            callApi("Uploading statements", call, t -> {
                binder.getBean().setFileId(t.getId());
                uploadButton.setEnabled(false);
                uploadButton.setCaption(shortName(event.getFilename(), 10));
            }, Notifications::errorNotification);


        });

        return sample;
    }

    String shortName(String input, int maxLength) {
        String ellip = "...";
        if (input == null || input.length() <= maxLength
            || input.length() < ellip.length()) {
            return input;
        }
        return input.substring(0, maxLength - ellip.length()).concat(ellip);
    }
}




