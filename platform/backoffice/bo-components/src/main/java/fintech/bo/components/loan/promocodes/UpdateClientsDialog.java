package fintech.bo.components.loan.promocodes;

import com.vaadin.data.Binder;
import com.vaadin.ui.Component;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Upload;
import fintech.bo.api.client.FileApiClient;
import fintech.bo.api.client.PromoCodeApiClient;
import fintech.bo.api.model.loan.UpdatePromoCodeClientsRequest;
import fintech.bo.components.background.BackgroundOperations;
import fintech.bo.components.dialogs.ActionDialog;
import fintech.bo.components.notifications.Notifications;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;

import java.io.ByteArrayOutputStream;

import static fintech.bo.components.background.BackgroundOperations.callApi;

public class UpdateClientsDialog extends ActionDialog {

    private final PromoCodeApiClient promoCodeApi;
    private final FileApiClient fileApiClient;
    private final Binder<UpdatePromoCodeClientsRequest> binder;

    private ByteArrayOutputStream fileOutputStream;
    private Upload.SucceededEvent fileUploadEvent;

    public UpdateClientsDialog(Long promoCodeId, PromoCodeApiClient promoCodeApi, FileApiClient fileApiClient) {
        super("Update client IDs", "Update");
        this.promoCodeApi = promoCodeApi;
        this.fileApiClient = fileApiClient;

        binder = new Binder<>();
        binder.setBean(new UpdatePromoCodeClientsRequest().setPromoCodeId(promoCodeId));

        setDialogContent(createForm());
        setWidth(600, Unit.PIXELS);
    }

    private Component createForm() {
        FormLayout form = new FormLayout();
        form.setMargin(true);

        fileOutputStream = new ByteArrayOutputStream();
        Upload upload = new Upload("Select file with clients", (f, m) -> fileOutputStream);
        Label fileInfo = new Label();
        upload.addSucceededListener(e -> {
            fileInfo.setValue(e.getFilename());
            this.fileUploadEvent = e;
        });
        form.addComponents(upload, fileInfo);

        return form;
    }

    @Override
    protected void executeAction() {
        if (binder.validate().isOk()) {
            UpdatePromoCodeClientsRequest request = binder.getBean();

            if (fileUploadEvent == null) {
                Notifications.errorNotification("Please select clients file");
                return;
            }

            RequestBody body = new MultipartBody.Builder()
                .setType(MultipartBody.MIXED)
                .addFormDataPart("file", fileUploadEvent.getFilename(), MultipartBody.create(MediaType.parse(fileUploadEvent.getMIMEType()), fileOutputStream.toByteArray()))
                .build();

            callApi("Uploading file", fileApiClient.upload(body, "discounts"), r -> {
                request.setClientFileId(r.getId());
                updateClientsList(request);
            }, Notifications::errorNotification);

        }
    }

    private void updateClientsList(UpdatePromoCodeClientsRequest request) {
        BackgroundOperations.callApi("Updating clients list", promoCodeApi.updateClients(request), t -> {
            Notifications.trayNotification("Updated");
            close();
        }, Notifications::errorNotification);
    }

}
