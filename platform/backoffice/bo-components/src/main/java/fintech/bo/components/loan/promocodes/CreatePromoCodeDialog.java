package fintech.bo.components.loan.promocodes;

import com.vaadin.data.Binder;
import com.vaadin.data.converter.StringToBigDecimalConverter;
import com.vaadin.data.converter.StringToLongConverter;
import com.vaadin.ui.Component;
import com.vaadin.ui.DateField;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.RadioButtonGroup;
import com.vaadin.ui.TextArea;
import com.vaadin.ui.TextField;
import com.vaadin.ui.TwinColSelect;
import com.vaadin.ui.Upload;
import fintech.DateUtils;
import fintech.bo.api.client.FileApiClient;
import fintech.bo.api.client.PromoCodeApiClient;
import fintech.bo.api.model.loan.CreatePromoCodeRequest;
import fintech.bo.components.Formats;
import fintech.bo.components.background.BackgroundOperations;
import fintech.bo.components.dialogs.ActionDialog;
import fintech.bo.components.notifications.Notifications;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;

import java.io.ByteArrayOutputStream;
import java.time.LocalDate;

import static com.vaadin.data.ValidationResult.error;
import static com.vaadin.data.ValidationResult.ok;
import static fintech.bo.components.background.BackgroundOperations.callApi;

public class CreatePromoCodeDialog extends ActionDialog {

    private final PromoCodeApiClient promoCodeApi;
    private final FileApiClient fileApiClient;
    private final PromoCodesComponents components;
    private final Binder<CreatePromoCodeRequest> binder;

    private ByteArrayOutputStream fileOutputStream;
    private Upload.SucceededEvent fileUploadEvent;

    public CreatePromoCodeDialog(PromoCodeApiClient promoCodeApi, FileApiClient fileApiClient,
                                 PromoCodesComponents components) {
        super("Create Promo code", "Create");
        this.promoCodeApi = promoCodeApi;
        this.fileApiClient = fileApiClient;
        this.components = components;

        binder = new Binder<>();
        binder.setBean(new CreatePromoCodeRequest().setType(PromoCodeType.TYPE_NEW_CLIENTS));

        setDialogContent(createForm());
        setWidth(600, Unit.PIXELS);
    }

    private Component createForm() {
        FormLayout form = new FormLayout();
        form.setMargin(true);

        TextField codeString = new TextField("Code string");
        binder.forField(codeString)
            .asRequired()
            .bind(CreatePromoCodeRequest::getCode, CreatePromoCodeRequest::setCode);
        form.addComponent(codeString);

        TextArea description = new TextArea("Description");
        binder.forField(description)
            .bind(CreatePromoCodeRequest::getDescription, CreatePromoCodeRequest::setDescription);
        form.addComponent(description);

        DateField effectiveFrom = new DateField("Effective from");
        effectiveFrom.setDateFormat(Formats.DATE_FORMAT);
        effectiveFrom.setPlaceholder(Formats.DATE_FORMAT);
        binder.forField(effectiveFrom)
            .asRequired()
            .bind(CreatePromoCodeRequest::getEffectiveFrom, CreatePromoCodeRequest::setEffectiveFrom);
        form.addComponent(effectiveFrom);

        DateField effectiveTo = new DateField("Effective to");
        effectiveTo.setDateFormat(Formats.DATE_FORMAT);
        effectiveTo.setPlaceholder(Formats.DATE_FORMAT);
        binder.forField(effectiveTo)
            .asRequired()
            .withValidator((value, context) -> DateUtils.lt(value, LocalDate.now()) ? error("Can not be in the past") : ok())
            .bind(CreatePromoCodeRequest::getEffectiveTo, CreatePromoCodeRequest::setEffectiveTo);
        form.addComponent(effectiveTo);

        TextField discount = new TextField("Discount in percent");
        binder.forField(discount)
            .asRequired()
            .withNullRepresentation("")
            .withConverter(new StringToBigDecimalConverter("Invalid number"))
            .bind(CreatePromoCodeRequest::getRateInPercent, CreatePromoCodeRequest::setRateInPercent);
        form.addComponent(discount);

        TextField maxTimesToUse = new TextField("Max times to use");
        binder.forField(maxTimesToUse)
            .asRequired()
            .withNullRepresentation("")
            .withConverter(new StringToLongConverter("Invalid number"))
            .bind(CreatePromoCodeRequest::getMaxTimesToApply, CreatePromoCodeRequest::setMaxTimesToApply);
        form.addComponent(maxTimesToUse);

        TwinColSelect<String> affiliates = components.sourceSelector();
        binder.forField(affiliates)
            .bind(CreatePromoCodeRequest::getSources, CreatePromoCodeRequest::setSources);
        form.addComponent(affiliates);

        RadioButtonGroup<String> typeSelector = new RadioButtonGroup<>("Promo code type");
        typeSelector.setItems(PromoCodeType.TYPE_NEW_CLIENTS, PromoCodeType.TYPE_REPEATING_CLIENTS);
        typeSelector.setSelectedItem(PromoCodeType.TYPE_NEW_CLIENTS);
        binder.forField(typeSelector)
            .bind(CreatePromoCodeRequest::getType, CreatePromoCodeRequest::setType);
        form.addComponent(typeSelector);

        form.forEach(component -> component.setWidth(100, Unit.PERCENTAGE));

        fileOutputStream = new ByteArrayOutputStream();
        Upload upload = new Upload("Select file with clients", (f, m) -> fileOutputStream);
        Label fileInfo = new Label();
        upload.addSucceededListener(e -> {
            fileInfo.setValue(e.getFilename());
            this.fileUploadEvent = e;
        });
        upload.setEnabled(false);
        form.addComponents(upload, fileInfo);


        typeSelector.addValueChangeListener(e -> {
            boolean newClientsOnly = PromoCodeType.TYPE_NEW_CLIENTS.equals(e.getValue());
            upload.setEnabled(!newClientsOnly);
            if (newClientsOnly) {
                this.fileUploadEvent = null;
                fileInfo.setValue(null);
            }
        });

        return form;
    }

    @Override
    protected void executeAction() {
        if (binder.validate().isOk()) {
            CreatePromoCodeRequest request = binder.getBean();

            if (PromoCodeType.TYPE_REPEATING_CLIENTS.equals(request.getType()) && fileUploadEvent == null) {
                Notifications.errorNotification("Please select clients file");
                return;
            }

            if (fileUploadEvent != null) {
                RequestBody body = new MultipartBody.Builder()
                    .setType(MultipartBody.MIXED)
                    .addFormDataPart("file", fileUploadEvent.getFilename(), MultipartBody.create(MediaType.parse(fileUploadEvent.getMIMEType()), fileOutputStream.toByteArray()))
                    .build();

                callApi("Uploading file", fileApiClient.upload(body, "discounts"), r -> {
                    request.setClientFileId(r.getId());
                    createPromoCode(request);
                }, Notifications::errorNotification);

            } else {
                createPromoCode(request);
            }
        }
    }

    private void createPromoCode(CreatePromoCodeRequest request) {
        BackgroundOperations.callApi("Creating promo code", promoCodeApi.createPromoCode(request), t -> {
            Notifications.trayNotification("Created");
            close();
        }, Notifications::errorNotification);
    }

}
