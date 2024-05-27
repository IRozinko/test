package fintech.spain;

import fintech.TimeMachine;
import fintech.bo.api.model.IdRequest;
import fintech.bo.api.model.IdResponse;
import fintech.bo.api.model.StringResponse;
import fintech.bo.api.model.marketing.MarketingSettings;
import fintech.bo.api.model.marketing.PreviewCampaignRequest;
import fintech.bo.api.model.marketing.SaveMarketingCampaignRequest;
import fintech.bo.api.model.marketing.SaveMarketingTemplateRequest;
import fintech.bo.api.model.permissions.BackofficePermissions;
import fintech.filestorage.CloudFile;
import fintech.marketing.*;
import fintech.spain.notification.NotificationConfig;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.beans.PropertyEditorSupport;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static org.apache.commons.lang3.Validate.isTrue;

@RestController
@RequiredArgsConstructor
public class MarketingBOApiController {

    private final MarketingCampaignService marketingCampaignService;
    private final MarketingClientSelectionService selectionService;
    private final MarketingSettingsProvider marketingSettingsProvider;

    @InitBinder
    public void initBinder(final WebDataBinder binder) {
        binder.registerCustomEditor(LocalDateTime.class, new PropertyEditorSupport() {
            @Override
            public void setAsText(String text) throws IllegalArgumentException {
                setValue(LocalDateTime.parse(text, DateTimeFormatter.ISO_DATE_TIME));
            }

            @Override
            public String getAsText() throws IllegalArgumentException {
                return DateTimeFormatter.ISO_DATE_TIME.format((LocalDateTime) getValue());
            }
        });
    }

    @SneakyThrows
    @Secured({BackofficePermissions.ADMIN, BackofficePermissions.MARKETING_MANAGER})
    @PostMapping("/api/bo/spain/marketing/save-marketing-template")
    public void saveMarketingTemplate(
        @RequestParam(value = "mockImage", required = false) MultipartFile mainImage,
        @Valid SaveMarketingTemplateRequest request
    ) {
        byte[] mainImageBytes = mainImage == null ? null : mainImage.getBytes();

        marketingCampaignService.saveTemplate(new SaveMarketingTemplateCommand()
            .setId(request.getId())
            .setName(request.getName())
            .setEmailBody(request.getEmailBody())
            .setHtmlTemplate(request.getHtmlTemplate())
            .setMainCampaignImage(mainImageBytes)
        );
    }

    @SneakyThrows
    @Secured({BackofficePermissions.ADMIN, BackofficePermissions.MARKETING_MANAGER})
    @PostMapping("/api/bo/spain/marketing/resend-marketing-campaign")
    public void resendMarketingCampaign(
        @RequestParam(value = "mainCampaignImage", required = false) MultipartFile mainImage,
        @RequestParam(value = "remindCampaignImage", required = false) MultipartFile remindImage,
        @Valid SaveMarketingCampaignRequest request
    ) {
        isTrue(request.getId() != null, "Resend request should be based on existing template");
        marketingCampaignService.resendCampaign(new SaveMarketingCampaignCommand()
            .setId(request.getId())
            .setName(request.getName())
            .setAudienceSettingsJson(request.getAudienceSettingsJson())
            .setEmailSubject(request.getEmailSubject())
            .setRemindEmailSubject(request.getRemindEmailSubject())
            .setEmailBody(request.getEmailBody())
            .setRemindEmailBody(request.getRemindEmailBody())
            .setRemindIntervalHours(request.getRemindIntervalHours())
            .setSms(request.getSms())
            .setMainCampaignImage(mainImage == null ? null : mainImage.getBytes())
            .setRemindCampaignImage(remindImage == null ? null : remindImage.getBytes())
            .setRemindPromoCodeId(request.getRemindPromoCodeId())
            .setMainPromoCodeId(request.getMainPromoCodeId())
            //UTC tz in backend, Spain tz in BO
            .setTriggerDate(request.getTriggerNow() ? TimeMachine.now() : request.getTriggerDate().minusHours(1))
            .setScheduleType(null)
            .setHasMainPromoCodeId(request.getHasMainPromoCodeId())
            .setHasRemindPromoCodeId(request.getHasRemindPromoCodeId())
            .setEnableRemind(request.getEnableRemind())
            .setMainMarketingTemplateId(request.getMainMarketingTemplateId())
            .setRemindMarketingTemplateId(request.getRemindMarketingTemplateId())
        );
    }


    @SneakyThrows
    @Secured({BackofficePermissions.ADMIN, BackofficePermissions.MARKETING_MANAGER})
    @PostMapping("/api/bo/spain/marketing/save-marketing-campaign")
    public void saveMarketingTemplate(
        @RequestParam(value = "mainCampaignImage", required = false) MultipartFile mainImage,
        @RequestParam(value = "remindCampaignImage", required = false) MultipartFile remindImage,
        @Valid SaveMarketingCampaignRequest request
    ) {
        marketingCampaignService.saveCampaign(new SaveMarketingCampaignCommand()
            .setId(request.getId())
            .setName(request.getName())
            .setAudienceSettingsJson(request.getAudienceSettingsJson())
            .setEmailSubject(request.getEmailSubject())
            .setRemindEmailSubject(request.getRemindEmailSubject())
            .setEmailBody(request.getEmailBody())
            .setRemindEmailBody(request.getRemindEmailBody())
            .setRemindIntervalHours(request.getRemindIntervalHours())
            .setSms(request.getSms())
            .setMainCampaignImage(mainImage == null ? null : mainImage.getBytes())
            .setRemindCampaignImage(remindImage == null ? null : remindImage.getBytes())
            .setRemindPromoCodeId(request.getRemindPromoCodeId())
            .setMainPromoCodeId(request.getMainPromoCodeId())
            //UTC tz in backend, Spain tz in BO
            .setTriggerDate(request.getTriggerNow() ? TimeMachine.now() : request.getTriggerDate().minusHours(1))
            .setScheduleType(request.getAutomated() ? MarketingCampaignScheduleType.valueOf(request.getScheduleType()) : null)
            .setHasMainPromoCodeId(request.getHasMainPromoCodeId())
            .setHasRemindPromoCodeId(request.getHasRemindPromoCodeId())
            .setEnableRemind(request.getEnableRemind())
            .setMainMarketingTemplateId(request.getMainMarketingTemplateId())
            .setRemindMarketingTemplateId(request.getRemindMarketingTemplateId())
        );
    }

    @Secured({BackofficePermissions.ADMIN, BackofficePermissions.MARKETING_MANAGER})
    @PostMapping("/api/bo/spain/marketing/template-preview")
    public StringResponse templatePreview(@Valid @RequestBody IdRequest request) {
        return new StringResponse(marketingCampaignService.previewTemplate(request.getId()));//todo com.mitchellbosecke.pebble.error.ParserException:
    }

    @Secured({BackofficePermissions.ADMIN, BackofficePermissions.MARKETING_MANAGER})
    @PostMapping("/api/bo/spain/marketing/sms-preview")
    public StringResponse smsPreview(Long promoCodeId, String sms) {
        return new StringResponse(marketingCampaignService.previewSms(promoCodeId, sms));
    }

    @Secured({BackofficePermissions.ADMIN, BackofficePermissions.MARKETING_MANAGER})
    @PostMapping("/api/bo/spain/marketing/campaign-preview")
    public StringResponse campaignPreview(@RequestParam(value = "image", required = false) MultipartFile mainImage,
                                          @Valid PreviewCampaignRequest request) throws IOException {
        return new StringResponse(marketingCampaignService.previewCampaignEmail(
            request.getCampaignId(),
            mainImage == null ? null : mainImage.getBytes(),
            request.getTemplateId(),
            request.getPromoCodeId(),
            request.getContent() == null ? "" : request.getContent(),
            request.getReminder()
        ));
    }

    @Secured({BackofficePermissions.ADMIN, BackofficePermissions.MARKETING_MANAGER})
    @PostMapping("/api/bo/spain/marketing/audience-preview")
    public IdResponse exportAudiencePreview(@Valid @RequestBody MarketingAudienceSettings request) {
        CloudFile cloudFile = selectionService.exportAudiencePreview(request);
        return new IdResponse(cloudFile.getFileId());
    }

    @Secured({BackofficePermissions.ADMIN, BackofficePermissions.MARKETING_MANAGER})
    @PostMapping("/api/bo/spain/marketing/toggle-campaign-status")
    public void toggleCampaignStatus(@Valid @RequestBody IdRequest request) {
        marketingCampaignService.toggleCampaignStatus(request.getId());
    }

    @Secured({BackofficePermissions.ADMIN, BackofficePermissions.MARKETING_MANAGER})
    @GetMapping("/api/bo/spain/marketing/documentation")
    public StringResponse getDocumentation() {
        StringResponse response = new StringResponse();
        response.setString(marketingCampaignService.getContextDocumentation());
        return response;
    }

    @Secured({BackofficePermissions.ADMIN, BackofficePermissions.MARKETING_MANAGER})
    @GetMapping("/api/bo/spain/marketing/settings")
    public MarketingSettings getSettings() {
        NotificationConfig config = marketingSettingsProvider.getMarketingNotificationConfig();
        if (config == null) {
            config = new NotificationConfig();
        }
        MarketingSettings response = new MarketingSettings();
        response.setEmailFrom(config.getEmailFrom());
        response.setEmailFromName(config.getEmailFromName());
        response.setEmailReplyTo(config.getEmailReplyTo());
        response.setSmsSenderId(config.getSmsSenderId());
        return response;
    }

    @Secured({BackofficePermissions.ADMIN, BackofficePermissions.MARKETING_MANAGER})
    @PostMapping("/api/bo/spain/marketing/settings")
    public void saveSettings(@Valid @RequestBody MarketingSettings marketingSettings) {
        NotificationConfig config = new NotificationConfig();
        config.setSmsSenderId(marketingSettings.getSmsSenderId());
        config.setEmailFromName(marketingSettings.getEmailFromName());
        config.setEmailFrom(marketingSettings.getEmailFrom());
        config.setEmailReplyTo(marketingSettings.getEmailReplyTo());
        marketingSettingsProvider.setMarketingNotificationConfig(config);
    }

}
