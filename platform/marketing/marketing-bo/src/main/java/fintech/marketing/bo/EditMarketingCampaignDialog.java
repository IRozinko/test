package fintech.marketing.bo;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.google.common.base.MoreObjects;
import com.google.common.collect.Lists;
import com.vaadin.data.Binder;
import com.vaadin.data.BinderValidationStatus;
import com.vaadin.data.HasValue;
import com.vaadin.data.ValidationResult;
import com.vaadin.server.Sizeable;
import com.vaadin.shared.ui.MarginInfo;
import com.vaadin.ui.*;
import com.vaadin.ui.themes.ValoTheme;
import fintech.TimeMachine;
import fintech.bo.api.client.FileApiClient;
import fintech.bo.api.model.CloudFile;
import fintech.bo.api.model.IdResponse;
import fintech.bo.api.model.StringResponse;
import fintech.bo.api.model.marketing.PreviewCampaignRequest;
import fintech.bo.api.model.marketing.SaveMarketingCampaignRequest;
import fintech.bo.components.BackofficeTheme;
import fintech.bo.components.Formats;
import fintech.bo.components.IdNameDetails;
import fintech.bo.components.JsonUtils;
import fintech.bo.components.api.ApiAccessor;
import fintech.bo.components.application.LoanApplicationConstants;
import fintech.bo.components.application.LoanApplicationQueries;
import fintech.bo.components.background.BackgroundOperations;
import fintech.bo.components.common.HtmlPreview;
import fintech.bo.components.dialogs.ActionDialog;
import fintech.bo.components.loan.LoanConstants;
import fintech.bo.components.loan.promocodes.PromoCodeDetails;
import fintech.bo.components.loan.promocodes.PromoCodeQueries;
import fintech.bo.components.notifications.Notifications;
import fintech.bo.components.utils.CloudFileDownloader;
import fintech.bo.db.jooq.marketing.tables.records.MarketingCampaignRecord;
import fintech.marketing.bo.components.AudienceConditionComponentAbs;
import fintech.marketing.bo.components.AudienceNumberValuePredicateComponent;
import fintech.marketing.bo.components.AudiencePeriodPredicateComponent;
import fintech.marketing.bo.components.AudienceSelectBoxPredicateComponent;
import fintech.retrofit.RetrofitHelper;
import lombok.Data;
import lombok.Getter;
import lombok.SneakyThrows;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import org.apache.commons.lang3.StringUtils;
import retrofit2.Call;

import java.io.ByteArrayOutputStream;
import java.lang.reflect.Field;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import static fintech.marketing.bo.EditMarketingCampaignDialog.MarketingAudienceSettings.AudienceCondition;
import static java.lang.Boolean.FALSE;
import static java.lang.Boolean.TRUE;
import static java.time.format.DateTimeFormatter.ISO_DATE_TIME;

@Slf4j
public class EditMarketingCampaignDialog extends ActionDialog {

    @Getter
    private final MarketingApiClient apiClient;

    private final FileApiClient fileApiClient;

    private final MarketingAudienceSettings settings;

    private List<AudienceConditionComponentAbs> audiencePredicates;

    @Getter
    private final Binder<SaveMarketingCampaignRequest> binder = new Binder<>();

    private ByteArrayOutputStream mainImageUploadedFile;

    private ByteArrayOutputStream reminderImageUploadedFile;

    private final Map<Long, PromoCodeDetails> actualPromoCodes;
    private final Map<Long, IdNameDetails> marketingTemplates;

    private final Long mainImageFileId;
    private final LoanApplicationQueries loanApplicationQueries;
    private final Long remindImageFileId;

    public EditMarketingCampaignDialog(MarketingCampaignRecord item) {
        super(item.getId() == null ? "Add item" : "Edit item", "Save");
        this.fileApiClient = ApiAccessor.gI().get(FileApiClient.class);
        removeClickShortcut();
        this.apiClient = ApiAccessor.gI().get(MarketingApiClient.class);
        this.loanApplicationQueries = ApiAccessor.gI().get(LoanApplicationQueries.class);

        this.actualPromoCodes = ApiAccessor.gI().get(PromoCodeQueries.class).findActual().stream()
            .collect(Collectors.toMap(PromoCodeDetails::getId, p -> p));

        this.marketingTemplates = ApiAccessor.gI().get(MarketingTemplateQueries.class).findAll().stream()
            .collect(Collectors.toMap(IdNameDetails::getId, t -> t));

        SaveMarketingCampaignRequest request = new SaveMarketingCampaignRequest();
        request.setName(item.getName());
        request.setEmailSubject(item.getEmailSubject());
        request.setRemindEmailSubject(item.getRemindEmailSubject());
        request.setId(item.getId());
        request.setEmailBody(item.getEmailBody());
        request.setRemindEmailBody(item.getRemindEmailBody());
        request.setAudienceSettingsJson(item.getAudienceSettingsJsonConfig());
        settings = (item.getAudienceSettingsJsonConfig() == null) ? new MarketingAudienceSettings() : JsonUtils.readValue(item.getAudienceSettingsJsonConfig(), MarketingAudienceSettings.class);
        request.setTriggerDate(item.getScheduleDate() == null ? TimeMachine.now() : item.getScheduleDate());
        request.setScheduleType(item.getScheduleType());
        request.setAutomated(item.getScheduleType() != null);
        request.setMainPromoCodeId(item.getMainPromoCodeId());
        request.setRemindPromoCodeId(item.getRemindPromoCodeId());
        request.setRemindIntervalHours(item.getRemindIntervalHours());
        request.setSms(item.getSms());
        request.setHasMainPromoCodeId(item.getHasMainPromoCode() == null ? false : item.getHasMainPromoCode());
        request.setHasRemindPromoCodeId(item.getHasRemindPromoCode() == null ? false : item.getHasRemindPromoCode());
        request.setTriggerNow(request.getScheduleType() == null);
        request.setEnableRemind(item.getEnableRemind() == null ? false : item.getEnableRemind());
        request.setMainMarketingTemplateId(item.getMainMarketingTemplateId());
        request.setRemindMarketingTemplateId(item.getRemindMarketingTemplateId());
        mainImageFileId = item.getMainImageFileId();
        remindImageFileId = item.getRemindImageFileId();
        binder.setBean(request);

        setDialogContent(editor());
        setWidth(800, Unit.PIXELS);
        fullHeight();
    }

    private Component editor() {
        VerticalLayout layout = new VerticalLayout();
        layout.setSizeFull();

        TextField name = new TextField("Name");
        name.setWidth(100, Unit.PERCENTAGE);
        binder.forField(name)
            .asRequired()
            .bind(SaveMarketingCampaignRequest::getName, SaveMarketingCampaignRequest::setName);

        TabSheet tabsheet = new TabSheet();
        tabsheet.addTab(new VerticalLayout(name), "Basic");
        tabsheet.addTab(audiencePreviewComponent(), "Audience");
        tabsheet.addTab(mainCommunication(), "Communication");
        tabsheet.addTab(remindCommunication(), "Reminder communication");
        tabsheet.addTab(schedulingComponent(), "Scheduling");
        layout.addComponent(tabsheet);

        tabsheet.setSizeFull();

        return layout;
    }

    public Component schedulingComponent() {
        VerticalLayout root = new VerticalLayout();
        root.setSpacing(false);
        root.setMargin(new MarginInfo(true, true, true, true));

        RadioButtonGroup<Boolean> group = new RadioButtonGroup<>();
        group.setItems(true, false);
        group.setItemCaptionGenerator((ItemCaptionGenerator<Boolean>) item -> TRUE.equals(item) ? "Trigger now" : "Schedule");

        root.addComponent(group);

        VerticalLayout scheduleLayout = new VerticalLayout();
        scheduleLayout.setSpacing(false);
        scheduleLayout.setMargin(new MarginInfo(true, true, true, true));
        DateTimeField nextActionAt = new DateTimeField();
        nextActionAt.setValue(TimeMachine.now());
        binder.forField(nextActionAt)
            .asRequired()
            .bind(SaveMarketingCampaignRequest::getTriggerDate, SaveMarketingCampaignRequest::setTriggerDate);
        nextActionAt.setDateFormat(Formats.DATE_TIME_FORMAT);
        nextActionAt.setTextFieldEnabled(false);
        scheduleLayout.addComponent(new HorizontalLayout(new Label("Date"), nextActionAt));

        HorizontalLayout autoScheduling = new HorizontalLayout();
        autoScheduling.setDefaultComponentAlignment(Alignment.MIDDLE_LEFT);
        CheckBox autoScheduleCheckbox = new CheckBox();
        binder.forField(autoScheduleCheckbox)
            .bind(SaveMarketingCampaignRequest::getAutomated, SaveMarketingCampaignRequest::setAutomated);
        autoScheduling.addComponent(autoScheduleCheckbox);
        autoScheduling.addComponent(new Label("Automate campaign - send"));

        ComboBox<String> scheduleType = new ComboBox<>();
        scheduleType.setItems(SaveMarketingCampaignRequest.DAILY, SaveMarketingCampaignRequest.WEEKLY, SaveMarketingCampaignRequest.MONTHLY);
        scheduleType.setTextInputAllowed(false);
        scheduleType.setEmptySelectionAllowed(false);
        autoScheduling.addComponent(scheduleType);
        binder.forField(scheduleType)
            .bind(SaveMarketingCampaignRequest::getScheduleType, SaveMarketingCampaignRequest::setScheduleType);
        autoScheduling.addComponent(new Label("starting this date"));
        scheduleLayout.addComponent(autoScheduling);

        root.addComponent(scheduleLayout);

        group.addValueChangeListener((HasValue.ValueChangeListener<Boolean>) event -> {
            for (int i = 0; i < scheduleLayout.getComponentCount(); i++) {
                scheduleLayout.getComponent(i).setEnabled(!event.getValue());
            }
        });
        binder.forField(group)
            .bind(SaveMarketingCampaignRequest::getTriggerNow, SaveMarketingCampaignRequest::setTriggerNow);

        autoScheduleCheckbox.addValueChangeListener((HasValue.ValueChangeListener<Boolean>) event -> {
            for (int i = 1; i < autoScheduling.getComponentCount(); i++) {
                autoScheduling.getComponent(i).setEnabled(event.getValue());
            }
        });
        binder.bind(autoScheduleCheckbox, SaveMarketingCampaignRequest::getAutomated, SaveMarketingCampaignRequest::setAutomated);
        if (binder.getBean().getId() != null) {
            group.setReadOnly(true);
            autoScheduleCheckbox.setReadOnly(true);
        }
        return root;
    }

    private Component audiencePreviewComponent() {
        VerticalLayout layout = new VerticalLayout();

        AudienceConditionComponentAbs repaidComponent = new AudiencePeriodPredicateComponent("Loans repaid", "RepaidLoansCount", 0);
        AudienceConditionComponentAbs dysSinceComponent = new AudiencePeriodPredicateComponent("Days since last loan close date", "DaysSinceLastLoanClosed", 0);
        AudienceConditionComponentAbs currentLoanStatusDetail = new AudienceSelectBoxPredicateComponent("Last loan status detail", "LastLoanStatusDetail", LoanConstants.getStatusDetails());
        AudienceConditionComponentAbs lastLoanApplicationStatusDetail = new AudienceSelectBoxPredicateComponent("Last application status detail", "LastLoanApplicationStatusDetail", LoanApplicationConstants.getLoanApplicationStatusDetails());
        AudienceConditionComponentAbs dpdComponent = new AudiencePeriodPredicateComponent("Last loan dpd", "LastLoanDpd", Integer.MIN_VALUE);
        AudienceConditionComponentAbs rejections = new AudienceNumberValuePredicateComponent("Max amount rejections 30d", "Max30DRejections");
        AudienceConditionComponentAbs cancellations = new AudienceNumberValuePredicateComponent("Max amount cancellations 30d", "Max30DCancellations");
        AudienceConditionComponentAbs lastLoanApplicationCloseReason = new AudienceSelectBoxPredicateComponent("Last application close reason", "LastLoanApplicationCloseReason", loanApplicationQueries.findCloseReasons());

        audiencePredicates = Lists.newArrayList(repaidComponent, dysSinceComponent, dpdComponent, rejections, cancellations, currentLoanStatusDetail, lastLoanApplicationStatusDetail, lastLoanApplicationCloseReason);
        audiencePredicates.forEach(ap -> {
            setParamsIfPresent(ap);
            layout.addComponent(ap);
        });

        layout.addComponent(previewAudienceButton());
        return layout;
    }


    private boolean isAudienceSettingsValid() {
        return audiencePredicates.stream()
            .filter(AudienceConditionComponentAbs::isEnabledPredicate)
            .allMatch(AudienceConditionComponentAbs::isValid);
    }

    private MarketingAudienceSettings buildSettings() {
        MarketingAudienceSettings settings = new MarketingAudienceSettings();

        List<AudienceCondition> conditions = audiencePredicates.stream()
            .filter(AudienceConditionComponentAbs::isEnabledPredicate)
            .map(c -> {
                AudienceCondition condition = new AudienceCondition();
                condition.setParams(c.getParams());
                condition.setType(c.getType());
                return condition;
            }).collect(Collectors.toList());
        settings.setAudienceConditions(conditions);
        return settings;
    }

    private void setParamsIfPresent(AudienceConditionComponentAbs componentAbs) {
        settings.findByType(componentAbs.getType()).ifPresent(c -> {
            componentAbs.setParams(c.params);
        });
    }

    @SneakyThrows
    @Override
    protected void executeAction() {
        SaveMarketingCampaignRequest request = binder.getBean();

        BinderValidationStatus<SaveMarketingCampaignRequest> validationStatus = binder.validate();
        if (!validationStatus.isOk()) {
            String errorMsg = binder.validate().getValidationErrors().stream()
                .map(ValidationResult::getErrorMessage)
                .filter(msg -> !StringUtils.isEmpty(msg))
                .collect(Collectors.joining(", "));
            if (!StringUtils.isEmpty(errorMsg)) {
                Notifications.errorNotification(errorMsg);
            } else {
                Notifications.errorNotification("Fix validation errors");
            }
            return;
        }
        if (!validate(request)) {
            return;
        }
        if (!isAudienceSettingsValid()) {
            Notifications.errorNotification("Fix audience settings");
            return;
        }
        request.setAudienceSettingsJson(JsonUtils.writeValueAsString(buildSettings()));

        MultipartBody.Builder multipartRequest = new MultipartBody.Builder()
            .setType(MultipartBody.FORM);
        if (mainImageUploadedFile != null) {
            multipartRequest.addFormDataPart("mainCampaignImage", "any-file-name",
                MultipartBody.create(MediaType.parse("image/jpeg"), mainImageUploadedFile.toByteArray()));
        }

        if (reminderImageUploadedFile != null) {
            multipartRequest.addFormDataPart("remindCampaignImage", "any-file-name",
                MultipartBody.create(MediaType.parse("image/jpeg"), reminderImageUploadedFile.toByteArray()));
        }

        Field[] fields = request.getClass().getDeclaredFields();
        for (Field field : fields) {
            field.setAccessible(true);
            Object value = field.get(request);
            if (value != null) {
                String strVal;
                if (value instanceof LocalDateTime) {
                    LocalDateTime ldt = (LocalDateTime) value;
                    strVal = ldt.format(ISO_DATE_TIME);
                } else {
                    strVal = value.toString();
                }
                multipartRequest.addFormDataPart(field.getName(), strVal);
            }
        }
        processRequest(multipartRequest.build());
    }

    protected void processRequest(MultipartBody body) {
        Call<Void> call = apiClient.saveMarketingCampaign(body);
        BackgroundOperations.callApi("Saving campaign", call, v -> {
            Notifications.trayNotification("Saved");
            close();
        }, Notifications::errorNotification);
    }

    private boolean validate(SaveMarketingCampaignRequest request) {
        if (mainImageUploadedFile == null && mainImageFileId == null) {
            Notifications.errorNotification("Upload main campaign image");
            return false;
        }
        if (request.getEnableRemind()) {
            if (request.getRemindIntervalHours() == null) {
                Notifications.errorNotification("Set remind hours");
                return false;
            }
            if (request.getRemindEmailSubject() == null) {
                Notifications.errorNotification("Set remind email subject");
                return false;
            }
            if (request.getRemindMarketingTemplateId() == null) {
                Notifications.errorNotification("Select remind marketing template");
                return false;
            }
            if (request.getRemindEmailBody() == null) {
                Notifications.errorNotification("Set remind email body");
                return false;
            }
            if (reminderImageUploadedFile == null && remindImageFileId == null) {
                Notifications.errorNotification("Upload remind campaign image");
                return false;
            }
        }

        if (request.getEmailBody() == null) {
            Notifications.errorNotification("Set email body");
            return false;
        }
        if (request.getEmailSubject() == null) {
            Notifications.errorNotification("Set email subject");
            return false;
        }
        if (request.getHasMainPromoCodeId() && request.getMainPromoCodeId() == null) {
            Notifications.errorNotification("Set main promo code");
            return false;
        }
        if (request.getHasRemindPromoCodeId() && request.getRemindPromoCodeId() == null) {
            Notifications.errorNotification("Set remind promo code");
            return false;
        }
        if (FALSE.equals(request.getTriggerNow()) && request.getTriggerDate().isBefore(TimeMachine.now())) {
            Notifications.errorNotification("Incorrect trigger date");
            return false;
        }
        return true;
    }

    private Component previewAudienceButton() {

        CloudFileDownloader downloader =
            new CloudFileDownloader(
                fileApiClient,
                () -> {
                    if (!isAudienceSettingsValid()) {
                        return null;
                    }
                    IdResponse response = RetrofitHelper.syncCall(apiClient.exportAudiencePreview(buildSettings()))
                        .orElseThrow(IllegalStateException::new);
                    return new CloudFile(response.getId(), "export.csv");
                },
                file -> Notifications.trayNotification("File downloaded: " + file.getName())
            );
        Button button = new Button("Preview");
        downloader.extend(button);
        return button;
    }

    private Component remindCommunication() {
        VerticalLayout layout = new VerticalLayout();

        HorizontalLayout remindInfoLine = new HorizontalLayout();
        remindInfoLine.setDefaultComponentAlignment(Alignment.MIDDLE_LEFT);

        CheckBox enableRemind = new CheckBox();

        ComboBox<Integer> remindInHours = new ComboBox<>();
        remindInHours.setItems(12, 24, 36, 48, 64, 72);
        remindInHours.setTextInputAllowed(false);
        remindInHours.setEmptySelectionAllowed(false);
        remindInHours.setWidth(60, Unit.PIXELS);
        binder.forField(remindInHours)
            .bind(SaveMarketingCampaignRequest::getRemindIntervalHours, SaveMarketingCampaignRequest::setRemindIntervalHours);

        remindInfoLine.addComponents(enableRemind, new Label("Generate a reminder after"), remindInHours, new Label("hours"));

        enableRemind.addValueChangeListener((HasValue.ValueChangeListener<Boolean>) event -> {
            for (int i = 1; i < remindInfoLine.getComponentCount(); i++) {
                remindInfoLine.getComponent(i).setEnabled(event.getValue());
            }
        });
        binder.bind(enableRemind, SaveMarketingCampaignRequest::getEnableRemind, SaveMarketingCampaignRequest::setEnableRemind);
        layout.addComponent(remindInfoLine);

        layout.addComponent(createUploadImgButton("Upload remind image", stream -> reminderImageUploadedFile = stream));

        ComboBox<Long> templatesComboBox = templatesComboBox();
        binder.forField(templatesComboBox)
            .bind(SaveMarketingCampaignRequest::getRemindMarketingTemplateId, SaveMarketingCampaignRequest::setRemindMarketingTemplateId);
        layout.addComponent(new HorizontalLayout(templatesComboBox));

        TextField subject = new TextField("Subject");
        subject.setWidth(100, Unit.PERCENTAGE);
        binder.forField(subject)
            .bind(SaveMarketingCampaignRequest::getRemindEmailSubject, SaveMarketingCampaignRequest::setRemindEmailSubject);

        HorizontalLayout subjectLine = new HorizontalLayout(subject);
        subjectLine.setWidth(100, Unit.PERCENTAGE);
        layout.addComponent(subjectLine);

        RichTextArea emailBody = new RichTextArea();
        emailBody.setHeight(500f, Unit.PIXELS);
        emailBody.setWidth(100, Unit.PERCENTAGE);
        binder.bind(emailBody, SaveMarketingCampaignRequest::getRemindEmailBody, SaveMarketingCampaignRequest::setRemindEmailBody);
        layout.addComponent(emailBody);

        ComboBox<Long> promoCodes = new ComboBox<>();
        promoCodes.setTextInputAllowed(false);
        promoCodes.setEmptySelectionAllowed(false);
        promoCodes.setItems(actualPromoCodes.keySet());
        promoCodes.setItemCaptionGenerator((ItemCaptionGenerator<Long>) item -> actualPromoCodes.get(item).getCode());

        binder.bind(promoCodes, SaveMarketingCampaignRequest::getRemindPromoCodeId, SaveMarketingCampaignRequest::setRemindPromoCodeId);

        HorizontalLayout promoCodeLine = new HorizontalLayout();
        promoCodeLine.setWidth(100.0f, Unit.PERCENTAGE);
        promoCodeLine.setDefaultComponentAlignment(Alignment.MIDDLE_LEFT);

        CheckBox enablePromo = new CheckBox();
        binder.forField(enablePromo)
            .bind(SaveMarketingCampaignRequest::getHasRemindPromoCodeId, SaveMarketingCampaignRequest::setHasRemindPromoCodeId);
        HorizontalLayout promoSubLine = new HorizontalLayout();
        promoSubLine.setDefaultComponentAlignment(Alignment.MIDDLE_LEFT);
        promoSubLine.addComponents(enablePromo, new Label("has promo code"), promoCodes);
        promoCodeLine.addComponent(promoSubLine);

        enablePromo.addValueChangeListener((HasValue.ValueChangeListener<Boolean>) event -> {
            for (int i = 1; i < promoSubLine.getComponentCount(); i++) {
                promoSubLine.getComponent(i).setEnabled(event.getValue());
            }
        });

        Component previewEmail = new Button("Preview", (Button.ClickListener) event -> previewNotification(true));
        promoCodeLine.addComponent(previewEmail);
        promoCodeLine.setComponentAlignment(previewEmail, Alignment.MIDDLE_RIGHT);
        layout.addComponent(promoCodeLine);

        return layout;
    }

    private Upload createUploadImgButton(String caption, Consumer<ByteArrayOutputStream> onUpload) {
        final ByteArrayOutputStream[] bos = new ByteArrayOutputStream[1];
        Upload upload = new Upload(caption, (Upload.Receiver) (filename, mimeType) -> {
            bos[0] = new ByteArrayOutputStream();
            return bos[0];
        });
        upload.addFinishedListener((Upload.FinishedListener) event -> {
            if (!event.getMIMEType().startsWith("image")) {
                Notifications.errorNotification("Only images are supported for upload");
                return;
            }
            onUpload.accept(bos[0]);
        });
        return upload;
    }

    private Component mainCommunication() {
        TabSheet tabsheet = new TabSheet();
        VerticalLayout email = new VerticalLayout();

        email.addComponent(createUploadImgButton("Upload main image", stream -> mainImageUploadedFile = stream));
        ComboBox<Long> templatesComboBox = templatesComboBox();
        binder.forField(templatesComboBox)
            .asRequired()
            .bind(SaveMarketingCampaignRequest::getMainMarketingTemplateId, SaveMarketingCampaignRequest::setMainMarketingTemplateId);
        email.addComponent(new HorizontalLayout(templatesComboBox));

        TextField subject = new TextField("Subject");
        subject.setWidth(100, Unit.PERCENTAGE);
        binder.forField(subject)
            .asRequired()
            .bind(SaveMarketingCampaignRequest::getEmailSubject, SaveMarketingCampaignRequest::setEmailSubject);
        HorizontalLayout subjectLine = new HorizontalLayout(subject);
        subjectLine.setWidth(100, Unit.PERCENTAGE);

        email.addComponent(subjectLine);

        RichTextArea emailBody = new RichTextArea();
        emailBody.setHeight(500f, Unit.PIXELS);
        emailBody.setWidth(100, Unit.PERCENTAGE);
        binder.bind(emailBody, SaveMarketingCampaignRequest::getEmailBody, SaveMarketingCampaignRequest::setEmailBody);
        email.addComponent(emailBody);

        HorizontalLayout promoCodeLine = new HorizontalLayout();
        promoCodeLine.setWidth(100.0f, Unit.PERCENTAGE);
        promoCodeLine.setDefaultComponentAlignment(Alignment.MIDDLE_LEFT);
        CheckBox enablePromo = new CheckBox();

        ComboBox<Long> promoCodes = new ComboBox<>();
        promoCodes.setTextInputAllowed(false);
        promoCodes.setEmptySelectionAllowed(false);
        promoCodes.setItems(actualPromoCodes.keySet());
        promoCodes.setItemCaptionGenerator((ItemCaptionGenerator<Long>) item -> actualPromoCodes.get(item).getCode());

        binder.bind(promoCodes, SaveMarketingCampaignRequest::getMainPromoCodeId, SaveMarketingCampaignRequest::setMainPromoCodeId);

        HorizontalLayout promoSubLine = new HorizontalLayout();
        promoSubLine.setDefaultComponentAlignment(Alignment.MIDDLE_LEFT);
        promoSubLine.addComponents(enablePromo, new Label("has promo code"), promoCodes);
        promoCodeLine.addComponent(promoSubLine);

        enablePromo.addValueChangeListener((HasValue.ValueChangeListener<Boolean>) event -> {
            for (int i = 1; i < promoSubLine.getComponentCount(); i++) {
                promoSubLine.getComponent(i).setEnabled(event.getValue());
            }
        });
        binder.forField(enablePromo)
            .bind(SaveMarketingCampaignRequest::getHasMainPromoCodeId, SaveMarketingCampaignRequest::setHasMainPromoCodeId);

        Component previewEmail = new Button("Preview", (Button.ClickListener) event -> previewNotification(false));
        promoCodeLine.addComponent(previewEmail);
        promoCodeLine.setComponentAlignment(previewEmail, Alignment.MIDDLE_RIGHT);
        email.addComponent(promoCodeLine);

        tabsheet.addTab(email, "Email");

        VerticalLayout sms = new VerticalLayout();
        TextArea smsArea = new TextArea();
        smsArea.setWordWrap(false);
        smsArea.setSizeFull();
        smsArea.setWidth(100, Unit.PERCENTAGE);
        smsArea.addStyleName(BackofficeTheme.TEXT_MONO);
        binder.bind(smsArea, SaveMarketingCampaignRequest::getSms, SaveMarketingCampaignRequest::setSms);
        sms.addComponent(smsArea);
        sms.addComponent(new Button("Preview", (Button.ClickListener) event -> previewSms()));
        tabsheet.addTab(sms, "Sms");

        return tabsheet;
    }

    private void previewSms() {
        Call<StringResponse> call = apiClient.smsPreview(
            binder.getBean().getHasMainPromoCodeId() ? binder.getBean().getMainPromoCodeId() : null,
            binder.getBean().getSms()
        );
        callAndShowPreview(call);
    }

    @SneakyThrows
    private void previewNotification(boolean remind) {
        ByteArrayOutputStream img = remind ? reminderImageUploadedFile : mainImageUploadedFile;
        String content = remind ? binder.getBean().getRemindEmailBody() : binder.getBean().getEmailBody();
        boolean promoCodeEnabled = remind ? binder.getBean().getHasRemindPromoCodeId() : binder.getBean().getHasMainPromoCodeId();

        Long promoCodeId = null;
        if (promoCodeEnabled) {
            promoCodeId = remind ? binder.getBean().getRemindPromoCodeId() : binder.getBean().getMainPromoCodeId();
        }

        Long templateId = remind ? binder.getBean().getRemindMarketingTemplateId() : binder.getBean().getMainMarketingTemplateId();
        if (img == null && binder.getBean().getId() == null) {
            Notifications.errorNotification("Upload image");
            return;
        }
        if (templateId == null) {
            Notifications.errorNotification("Select template");
            return;
        }
        if (content == null) {
            Notifications.errorNotification("Set email body");
            return;
        }
        PreviewCampaignRequest request = new PreviewCampaignRequest();
        request.setCampaignId(binder.getBean().getId());
        request.setContent(content);
        request.setReminder(remind);
        request.setPromoCodeId(promoCodeId);
        request.setTemplateId(templateId);

        MultipartBody.Builder multipartRequest = new MultipartBody.Builder()
            .setType(MultipartBody.FORM);
        if (img != null) {
            multipartRequest.addFormDataPart("image", "any-file-name",
                MultipartBody.create(MediaType.parse("image/jpeg"), img.toByteArray()));
        }

        Field[] fields = request.getClass().getDeclaredFields();
        for (Field field : fields) {
            field.setAccessible(true);
            Object value = field.get(request);
            if (value != null) {
                multipartRequest.addFormDataPart(field.getName(), value.toString());
            }
        }
        Call<StringResponse> call = apiClient.campaignPreview(multipartRequest.build());
        callAndShowPreview(call);
    }

    private Panel emailPreviewPanel(String body) {
        Panel panel = new Panel();
        panel.addStyleName(ValoTheme.PANEL_BORDERLESS);
        panel.setWidth(100, Sizeable.Unit.PERCENTAGE);
        panel.setHeightUndefined();
        panel.setContent(new HtmlPreview(MoreObjects.firstNonNull(body, "null")));
        return panel;
    }

    private void callAndShowPreview(Call<StringResponse> call) {
        BackgroundOperations.callApi("Rendering preview", call, renderResponse -> {
            VerticalLayout layout = new VerticalLayout();
            layout.addComponent(emailPreviewPanel(renderResponse.getString()));
            Window previewWindow = new Window("Preview");
            previewWindow.setContent(layout);
            previewWindow.center();
            previewWindow.setModal(true);
            getUI().addWindow(previewWindow);
        }, Notifications::errorNotification);
    }


    private ComboBox<Long> templatesComboBox() {
        ComboBox<Long> comboBox = new ComboBox<>("Marketing template");
        comboBox.setItems(this.marketingTemplates.keySet());
        comboBox.setTextInputAllowed(false);
        comboBox.setEmptySelectionAllowed(false);
        comboBox.setItemCaptionGenerator((ItemCaptionGenerator<Long>) item -> marketingTemplates.get(item).getName());
        return comboBox;
    }

    @Data
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class MarketingAudienceSettings {

        private List<AudienceCondition> audienceConditions = new ArrayList<>();

        @JsonInclude(JsonInclude.Include.NON_NULL)
        @Data
        @Accessors(chain = true)
        public static class AudienceCondition {
            private String type;
            private Map<String, Object> params = new HashMap<>();
        }

        public Optional<AudienceCondition> findByType(String type) {
            return audienceConditions.stream().filter(c -> c.type.equals(type)).findFirst();
        }
    }
}
