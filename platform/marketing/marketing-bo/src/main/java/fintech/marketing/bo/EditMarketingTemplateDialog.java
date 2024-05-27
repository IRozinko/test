package fintech.marketing.bo;

import com.google.common.base.MoreObjects;
import com.vaadin.data.Binder;
import com.vaadin.data.BinderValidationStatus;
import com.vaadin.shared.ui.ContentMode;
import com.vaadin.ui.*;
import fintech.bo.api.model.StringResponse;
import fintech.bo.api.model.marketing.SaveMarketingTemplateRequest;
import fintech.bo.components.BackofficeTheme;
import fintech.bo.components.background.BackgroundOperations;
import fintech.bo.components.dialogs.ActionDialog;
import fintech.bo.components.notifications.Notifications;
import fintech.bo.db.jooq.marketing.tables.records.MarketingTemplateRecord;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import retrofit2.Call;

import java.io.ByteArrayOutputStream;
import java.lang.reflect.Field;

@Slf4j
public class EditMarketingTemplateDialog extends ActionDialog {

    private final MarketingApiClient apiClient;

    private final Binder<SaveMarketingTemplateRequest> binder = new Binder<>();

    private ByteArrayOutputStream mainImageUploadedFile;

    public EditMarketingTemplateDialog(MarketingApiClient apiClient, MarketingTemplateRecord item) {
        super(item.getId() == null ? "Add template" : "Edit template", "Save");
        removeClickShortcut();
        this.apiClient = apiClient;
        SaveMarketingTemplateRequest request = new SaveMarketingTemplateRequest();
        request.setName(item.getName());
        request.setId(item.getId());
        request.setEmailBody(item.getEmailBody());
        request.setHtmlTemplate(item.getHtmlTemplate());
        binder.setBean(request);

        setDialogContent(editor());
        setWidth(800, Unit.PIXELS);
        fullHeight();
    }

    private Component help() {
        TextArea docs = new TextArea("Template engine variables");
        Call<StringResponse> call = apiClient.getDocumentation();
        BackgroundOperations.callApiSilent(call,
            response -> docs.setValue(MoreObjects.firstNonNull(response.getString(), "")),
            Notifications::errorNotification);
        docs.setReadOnly(true);
        docs.addStyleName(BackofficeTheme.TEXT_MONO);
        docs.setSizeFull();

        VerticalLayout layout = new VerticalLayout();
        layout.addComponent(docs);
        layout.addComponent(new Label("<a href=\"https://pebbletemplates.io\" target=\"_blank\">Pebble template engine</a>", ContentMode.HTML));
        layout.setExpandRatio(docs, 1.0f);
        layout.setSizeFull();
        return layout;
    }

    private Component editor() {
        VerticalLayout layout = new VerticalLayout();
        layout.setSizeFull();

        TabSheet tabsheet = new TabSheet();
        tabsheet.addTab(edit(), "Basic");
        tabsheet.addTab(template(), "Template");
        tabsheet.addTab(help(), "Help");
        layout.addComponent(tabsheet);

        tabsheet.setSizeFull();

        return layout;
    }

    private Component template() {
        VerticalLayout layout = new VerticalLayout();

        TextArea htmlTemplate = new TextArea("Html template");
        htmlTemplate.setWidth(100, Unit.PERCENTAGE);
        binder.forField(htmlTemplate)
            .asRequired()
            .bind(SaveMarketingTemplateRequest::getHtmlTemplate, SaveMarketingTemplateRequest::setHtmlTemplate);
        layout.addComponentsAndExpand(htmlTemplate);
        return layout;
    }

    private Component edit() {
        VerticalLayout layout = new VerticalLayout();

        TextField name = new TextField("Name");
        name.setWidth(100, Unit.PERCENTAGE);
        binder.forField(name)
            .asRequired()
            .bind(SaveMarketingTemplateRequest::getName, SaveMarketingTemplateRequest::setName);
        layout.addComponent(name);
        layout.addComponent(createImageUploadButton());

        RichTextArea emailBody = new RichTextArea();
        emailBody.setHeight(600f, Unit.PIXELS);
        emailBody.setWidth(100, Unit.PERCENTAGE);
        binder.bind(emailBody, SaveMarketingTemplateRequest::getEmailBody, SaveMarketingTemplateRequest::setEmailBody);
        layout.addComponentsAndExpand(emailBody);
        return layout;
    }

    private Upload createImageUploadButton() {
        final ByteArrayOutputStream[] bos = new ByteArrayOutputStream[1];
        Upload upload = new Upload("Upload mocked image", (Upload.Receiver) (filename, mimeType) -> {
            bos[0] = new ByteArrayOutputStream();
            return bos[0];
        });
        upload.addFinishedListener((Upload.FinishedListener) event -> {
            if (!event.getMIMEType().startsWith("image")) {
                Notifications.errorNotification("Only images are supported for upload");
                return;
            }
            mainImageUploadedFile = bos[0];
        });
        return upload;
    }

    @SneakyThrows
    @Override
    protected void executeAction() {
        if (mainImageUploadedFile == null && binder.getBean().getId() == null) {
            Notifications.errorNotification("Upload main mock image");
            return;
        }
        BinderValidationStatus<SaveMarketingTemplateRequest> validationStatus = binder.validate();
        if (validationStatus.isOk()) {
            MultipartBody.Builder multipartRequest = new MultipartBody.Builder()
                .setType(MultipartBody.FORM);
            if (mainImageUploadedFile != null) {
                multipartRequest.addFormDataPart("mockImage", "any-file-name",
                    MultipartBody.create(MediaType.parse("image/jpeg"), mainImageUploadedFile.toByteArray()));
            }

            Field[] fields = binder.getBean().getClass().getDeclaredFields();
            for (Field field : fields) {
                field.setAccessible(true);
                Object value = field.get(binder.getBean());
                if (value != null) {
                    multipartRequest.addFormDataPart(field.getName(), value.toString());
                }
            }
            Call<Void> call = apiClient.saveMarketingTemplate(multipartRequest.build());
            BackgroundOperations.callApi("Adding template", call, v -> {
                Notifications.trayNotification("Template added");
                close();
            }, Notifications::errorNotification);
        } else {
            Notifications.errorNotification("Fix validation errors");
        }
    }

}
