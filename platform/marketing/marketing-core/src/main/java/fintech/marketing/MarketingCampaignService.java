package fintech.marketing;

import com.querydsl.jpa.JPQLQueryFactory;
import fintech.JsonUtils;
import fintech.TimeMachine;
import fintech.cms.CmsContextBuilder;
import fintech.cms.CmsDocumentationGenerator;
import fintech.cms.CmsModels;
import fintech.cms.impl.pebble.PebbleTemplateEngine;
import fintech.crm.db.Entities;
import fintech.crm.security.OneTimeTokenService;
import fintech.crm.security.db.TokenType;
import fintech.filestorage.CloudFile;
import fintech.filestorage.FileStorageService;
import fintech.filestorage.SaveFileCommand;
import fintech.lending.core.promocode.CreatePromoCodeCommand;
import fintech.lending.core.promocode.PromoCodeOffer;
import fintech.lending.core.promocode.PromoCodeService;
import fintech.lending.core.promocode.UpdatePromoCodeCommand;
import fintech.lending.core.promocode.db.PromoCode;
import fintech.marketing.db.MarketingCampaignEntity;
import fintech.marketing.db.MarketingCampaignRepository;
import fintech.marketing.db.MarketingCommunicationEntity;
import fintech.marketing.db.MarketingCommunicationRepository;
import fintech.marketing.db.MarketingTemplateEntity;
import fintech.marketing.db.MarketingTemplateRepository;
import fintech.spain.notification.NotificationBuilder;
import fintech.spain.notification.NotificationBuilderFactory;
import fintech.spain.notification.NotificationConfig;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionTemplate;

import javax.annotation.Nullable;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static fintech.BigDecimalUtils.amount;
import static fintech.marketing.MarketingCommunicationStatus.QUEUED;
import static fintech.marketing.MarketingCommunicationStatus.SENT;
import static fintech.marketing.db.Entities.marketingCommunication;
import static java.time.Duration.ofHours;
import static javax.xml.bind.DatatypeConverter.printBase64Binary;
import static org.apache.commons.lang3.RandomStringUtils.randomAlphabetic;
import static org.apache.commons.lang3.Validate.isTrue;

@Slf4j
@RequiredArgsConstructor
@Service
public class MarketingCampaignService {

    private final MarketingCampaignRepository repository;
    private final MarketingTemplateRepository templateRepository;
    private final MarketingCommunicationRepository communicationRepository;
    private final PebbleTemplateEngine pebbleTemplateEngine;
    private final CmsContextBuilder contextBuilder;
    private final MarketingClientSelectionService clientSelectionService;
    private final FileStorageService fileStorageService;
    private final CmsModels cmsModels;
    private final NotificationBuilderFactory notificationFactory;
    private final TransactionTemplate txTemplate;
    private final PromoCodeService promoCodeService;
    private final MarketingSettingsProvider marketingSettingsProvider;
    private final OneTimeTokenService oneTimeTokenService;
    private final MarketingService marketingService;
    private final JPQLQueryFactory queryFactory;

    @SneakyThrows
    public String getContextDocumentation() {
        try (CmsDocumentationGenerator generator = new CmsDocumentationGenerator()) {
            return generator.generateContextDocumentation(testContext(testPromoCodeOffer(), (Long) null, ""));
        }
    }

    @Transactional
    public Long resendCampaign(SaveMarketingCampaignCommand command) {
        MarketingCampaignEntity existed = repository.getRequired(command.getId());
        isTrue(existed.getScheduleType() == null, "Original campaign should be not automated");
        isTrue(command.getScheduleType() == null, "New campaign should be not automated");
        parseSettings(command.getAudienceSettingsJson());

        MarketingCampaignEntity entity = new MarketingCampaignEntity();
        setProperties(entity, command);
        if (entity.getMainImageFileId() == null) {
            entity.setMainImageFileId(existed.getMainImageFileId());
        }
        if (entity.getRemindImageFileId() == null) {
            entity.setRemindImageFileId(existed.getRemindImageFileId());
        }
        entity = repository.save(entity);
        createCommunicationFromCampaign(entity, command.getTriggerDate(), false);
        return entity.getId();
    }

    @Transactional
    public Long saveCampaign(SaveMarketingCampaignCommand command) {
        parseSettings(command.getAudienceSettingsJson());

        MarketingCampaignEntity entity = command.getId() == null ?
            new MarketingCampaignEntity() :
            repository.getRequired(command.getId());
        boolean recreateCommunications = command.getId() == null || (repository.getRequired(command.getId()).getScheduleType() != command.getScheduleType());
        setProperties(entity, command);
        entity = repository.save(entity);

        if (recreateCommunications) {
            communicationRepository.findQueuedByMarketingCampaignId(command.getId())
                .forEach(ce -> {
                    ce.setStatus(MarketingCommunicationStatus.CANCELLED);
                    ce.setNextActionAt(null);
                });
            createCommunicationFromCampaign(entity, command.getTriggerDate(), false);
        }
        return entity.getId();
    }

    @Transactional
    public void triggerCommunication(Long communicationId) {
        MarketingCommunicationEntity communication = communicationRepository.getRequired(communicationId);
        isTrue(communication.getStatus() == QUEUED, String.format("Marketing communication [%s] should be in QUEUED state", communication.getId()));
        MarketingCampaignEntity campaign = repository.getRequired(communication.getMarketingCampaignId());

        MarketingAudienceSettings settings = parseSettings(communication.getAudienceSettingsJson());
        List<Long> clients = clientSelectionService.getClientsSelection(settings.getAudienceConditions(),
            communication.isReminder() ? Optional.of(ofHours(campaign.getRemindIntervalHours())) : Optional.empty()
        );
        updatePromoCodes(communicationId, clients);
        for (Long clientId : clients) {
            txTemplate.execute((TransactionCallback<Object>) status -> {
                sendCommunicationNotification(communicationId, clientId);
                return 1;
            });
        }
        communication.setTargetedUsers(clients.size());

        LocalDateTime nextFireDate = null;
        boolean reminder = false;
        if (!communication.isReminder() && campaign.isEnableRemind() && campaign.getRemindIntervalHours() != null) {
            nextFireDate = TimeMachine.now().plusHours(campaign.getRemindIntervalHours());
            reminder = true;

        } else if (campaign.getScheduleType() != null) {
            nextFireDate = nextActualFireDate(campaign.getScheduleType(), communication.getNextActionAt());
            reminder = false;
        }

        communication.setStatus(SENT);
        communication.setLastExecutionResult("OK");
        communicationRepository.save(communication);
        if (nextFireDate != null) {
            createCommunicationFromCampaign(campaign, nextFireDate, reminder);
        }
    }

    public String previewCampaignEmail(@Nullable Long campaignId, @Nullable byte[] img, long templateId, @Nullable Long promoCodeId, String content, boolean reminder) {
        isTrue(campaignId != null || img != null, "Campaign or image is required");
        MarketingTemplateEntity entity = templateRepository.getRequired(templateId);
        Map<String, Object> context;
        PromoCodeOffer promoCodeOffer = promoCodeId == null ? null : promoCodeService.getRequired(promoCodeId);
        if (campaignId != null && img == null) {
            MarketingCampaignEntity campaignEntity = repository.getRequired(campaignId);
            context = testContext(promoCodeOffer, reminder ? campaignEntity.getRemindImageFileId() : campaignEntity.getMainImageFileId(), content);
        } else {
            context = testContext(promoCodeOffer, img, content);
        }
        return StringUtils.isBlank(entity.getHtmlTemplate()) ? "" : pebbleTemplateEngine.render(entity.getHtmlTemplate(), context, contextBuilder.companyLocale());
    }

    public String previewTemplate(long marketingTemplateId) {
        return renderTemplate(marketingTemplateId, mockTemplateContext(marketingTemplateId));
    }

    public String previewSms(@Nullable Long promoCodeId, String sms) {
        PromoCodeOffer promoCodeOffer = promoCodeId == null ? null : promoCodeService.getRequired(promoCodeId);
        return StringUtils.isBlank(sms) ? "" : pebbleTemplateEngine.render(sms, testContext(promoCodeOffer, (Long) null, sms), contextBuilder.companyLocale());
    }

    @Transactional
    public void toggleCampaignStatus(Long campaignId) {
        MarketingCampaignEntity campaign = repository.getRequired(campaignId);
        isTrue(campaign.getScheduleDate() != null, "Campaign should be automated");
        campaign.setStatus(campaign.getStatus().toggle());
        if (campaign.getStatus() == MarketingCampaignStatus.PAUSED) {
            communicationRepository.findQueuedByMarketingCampaignId(campaign.getId())
                .forEach(ce -> {
                    ce.setStatus(MarketingCommunicationStatus.CANCELLED);
                    ce.setNextActionAt(null);
                });
        } else {
            LocalDateTime nextFireDate = nextActualFireDate(campaign.getScheduleType(), campaign.getScheduleDate());
            createCommunicationFromCampaign(campaign, nextFireDate, false);
        }
    }

    @Transactional
    public long saveTemplate(SaveMarketingTemplateCommand command) {
        isTrue(command.getId() != null || command.getMainCampaignImage() != null, "Uploaded image for new template should exist");
        MarketingTemplateEntity entity = command.getId() == null ? new MarketingTemplateEntity() : templateRepository.getRequired(command.getId());
        entity.setEmailBody(command.getEmailBody());
        entity.setHtmlTemplate(command.getHtmlTemplate());
        if (command.getMainCampaignImage() != null) {
            entity.setImageFileId(saveImage(command.getMainCampaignImage()).getFileId());
        }
        entity.setName(command.getName());
        return templateRepository.save(entity).getId();
    }

    private LocalDateTime nextActualFireDate(MarketingCampaignScheduleType scheduleType, LocalDateTime from) {

        LocalDateTime nextFireDate = from;
        while (nextFireDate.isBefore(TimeMachine.now())) {
            switch (scheduleType) {
                case WEEKLY:
                    nextFireDate = nextFireDate.plusDays(7);
                    break;
                case DAILY:
                    nextFireDate = nextFireDate.plusDays(1);
                    break;
                case MONTHLY:
                    nextFireDate = nextFireDate.plusMonths(1);
                    break;
                default:
                    throw new IllegalStateException(String.format("Unknown Schedule type [%s]", scheduleType));
            }
        }
        return nextFireDate;
    }

    private String renderTemplate(long marketingTemplateId, Map<String, Object> context) {
        MarketingTemplateEntity entity = templateRepository.getRequired(marketingTemplateId);
        return StringUtils.isBlank(entity.getHtmlTemplate()) ? "" : pebbleTemplateEngine.render(entity.getHtmlTemplate(), context, contextBuilder.companyLocale());
    }

    private void createCommunicationFromCampaign(MarketingCampaignEntity campaign, LocalDateTime nextFireDate, boolean reminder) {
        String newEmailBody;
        String newEmailSubject;
        String newSms = null;
        Long promoCodeId;
        Long imageId;
        if (reminder) {
            newEmailBody = campaign.getRemindEmailBody();
            newEmailSubject = campaign.getRemindEmailSubject();
            promoCodeId = campaign.isHasRemindPromoCode() ? campaign.getRemindPromoCodeId() : null;
            imageId = campaign.getRemindImageFileId();
        } else {
            newEmailBody = campaign.getEmailBody();
            newEmailSubject = campaign.getEmailSubject();
            newSms = campaign.getSms();
            promoCodeId = campaign.isHasMainPromoCode() ? campaign.getMainPromoCodeId() : null;
            imageId = campaign.getMainImageFileId();
        }
        MarketingCommunicationEntity newCommunicationEntity = new MarketingCommunicationEntity();
        newCommunicationEntity.setReminder(reminder);
        newCommunicationEntity.setAudienceSettingsJson(campaign.getAudienceSettingsJson());
        newCommunicationEntity.setEmailBody(newEmailBody);
        newCommunicationEntity.setEmailSubject(newEmailSubject);
        newCommunicationEntity.setSms(newSms);
        newCommunicationEntity.setMarketingCampaignId(campaign.getId());
        newCommunicationEntity.setPromoCodeId(promoCodeId);
        newCommunicationEntity.setStatus(campaign.getStatus() == MarketingCampaignStatus.ACTIVE ? QUEUED : MarketingCommunicationStatus.CANCELLED);
        newCommunicationEntity.setNextActionAt(nextFireDate);
        newCommunicationEntity.setImageFileId(imageId);
        communicationRepository.save(newCommunicationEntity);
    }

    private Map<String, Object> mockTemplateContext(long marketingTemplateId) {
        MarketingTemplateEntity entity = templateRepository.getRequired(marketingTemplateId);
        return testContext(testPromoCodeOffer(), entity.getImageFileId(), entity.getEmailBody());
    }

    private Map<String, Object> testContext(PromoCodeOffer promoCode, byte[] image, String content) {
        Map<String, Object> context = cmsModels.testClientContext();
        MarketingModel marketingModel = testMarketingModel(promoCode, image, content);
        context.put("marketing", marketingModel);
        if (!StringUtils.isBlank(marketingModel.getContent())) {
            marketingModel.setContent(pebbleTemplateEngine.render(marketingModel.getContent(), context, contextBuilder.companyLocale()));
        }
        return context;
    }

    private Map<String, Object> testContext(@Nullable PromoCodeOffer promoCode, @Nullable Long imageId, String content) {
        return testContext(
            promoCode,
            imageId == null ? null : fileStorageService.readContents(imageId, this::streamToBytes),
            content);
    }

    private Long sendCommunicationNotification(long communicationId, long clientId) {
        MarketingCommunicationEntity entity = communicationRepository.findOne(communicationId);
        MarketingCampaignEntity campaignEntity = repository.getRequired(entity.getMarketingCampaignId());
        MarketingTemplateEntity templateEntity = templateRepository.getRequired(entity.isReminder() ? campaignEntity.getRemindMarketingTemplateId() : campaignEntity.getMainMarketingTemplateId());
        Map<String, Object> ctx = context(clientId, entity.getId(), entity.getPromoCodeId() == null ? null : promoCodeService.getRequired(entity.getPromoCodeId()), entity.getImageFileId(), entity.getEmailBody());
        NotificationConfig marketingCfg = marketingSettingsProvider.getMarketingNotificationConfig();

        NotificationBuilder nb = notificationFactory.newNotification(clientId, marketingCfg);
        return nb.clientId(clientId)
            .emailBody(renderTemplate(templateEntity.getId(), ctx))
            .emailSubject(entity.getEmailSubject())
            .smsText(StringUtils.isBlank(entity.getSms()) ? null : pebbleTemplateEngine.render(entity.getSms(), ctx, contextBuilder.companyLocale()))
            .send();
    }

    private Map<String, Object> context(long clientId, long communicationId, PromoCodeOffer promoCodeOffer, long imageFileId, String emailBody) {
        Map<String, Object> context = cmsModels.clientContext(clientId);
        byte[] image = fileStorageService.readContents(imageFileId, this::streamToBytes);
        MarketingModel marketingModel = marketingModel(clientId, communicationId, promoCodeOffer, image, emailBody);
        context.put("marketing", marketingModel);
        if (!StringUtils.isBlank(marketingModel.getContent())) {
            marketingModel.setContent(pebbleTemplateEngine.render(marketingModel.getContent(), context, contextBuilder.companyLocale()));
        }
        return context;
    }

    private MarketingAudienceSettings parseSettings(String json) {
        return JsonUtils.readValue(json, MarketingAudienceSettings.class);
    }

    @SneakyThrows
    private CloudFile saveImage(byte[] image) {
        try (ByteArrayInputStream inputStream = new ByteArrayInputStream(image)) {
            SaveFileCommand saveFileCommand = new SaveFileCommand();
            saveFileCommand.setOriginalFileName(String.format("marketing_campaign_img_%s.zip", TimeMachine.currentInstant().toEpochMilli()));
            saveFileCommand.setDirectory("marketing_manager");
            saveFileCommand.setInputStream(inputStream);
            saveFileCommand.setContentType("image/jpeg");
            return fileStorageService.save(saveFileCommand);
        }
    }

    @SneakyThrows
    private byte[] streamToBytes(InputStream input) {
        return IOUtils.toByteArray(input);
    }

    @SneakyThrows
    private MarketingModel testMarketingModel(PromoCodeOffer promoCodeOffer, @Nullable byte[] image, @Nullable String content) {
        String unsubscribeUrl = cmsModels.getWebBaseUrl() + "/comunicaciones-comerciales?token=null";
        return new MarketingModel()
            .setUnsubscribeUrl(unsubscribeUrl)
            .setPromoCode(promoCodeOffer)
            .setContent(content == null ? "" : content)
            .setCommunicationId(0L)
            .setCampaignId(0L)
            .setTrackClickUuid("MockTrackClickUuid")
            .setTrackViewUuid("MockTrackViewUuid")
            .setMainImageBase64String(image == null ? "" : printBase64Binary(image));
    }

    @SneakyThrows
    private MarketingModel marketingModel(long clientId, long communicationId, PromoCodeOffer promoCodeOffer, byte[] image, String content) {
        String token = oneTimeTokenService.generateOrUpdateToken(TokenType.MARKETING_UNSUBSCRIBE, clientId, Duration.ofDays(365));
        String unsubscribeUrl = cmsModels.getWebBaseUrl() + "/comunicaciones-comerciales?token=" + token;
        return new MarketingModel()
            .setUnsubscribeUrl(unsubscribeUrl)
            .setPromoCode(promoCodeOffer)
            .setContent(content)
            .setCommunicationId(communicationId)
            .setCampaignId(communicationRepository.getRequired(communicationId).getMarketingCampaignId())
            .setTrackClickUuid(marketingService.getTrackClickUuid(clientId, communicationId))
            .setTrackViewUuid(marketingService.getTrackViewUuid(clientId, communicationId))
            .setMainImageBase64String(printBase64Binary(image));
    }

    private PromoCodeOffer testPromoCodeOffer() {
        return new PromoCodeOffer().setPromoCode("TEST-PROMO-CODE").setPromoCodeId(0L).setDiscountInPercent(amount(10));
    }

    private void setProperties(MarketingCampaignEntity entity, SaveMarketingCampaignCommand command) {
        entity.setAudienceSettingsJson(command.getAudienceSettingsJson());
        entity.setEmailSubject(command.getEmailSubject());
        entity.setRemindEmailSubject(command.getRemindEmailSubject());
        entity.setRemindEmailBody(command.getRemindEmailBody());
        entity.setName(command.getName());
        entity.setSms(command.getSms());
        entity.setEmailBody(command.getEmailBody());
        entity.setScheduleType(command.getScheduleType());
        entity.setMainPromoCodeId(command.getMainPromoCodeId());
        entity.setRemindPromoCodeId(command.getRemindPromoCodeId());
        entity.setRemindIntervalHours(command.getRemindIntervalHours());
        entity.setHasMainPromoCode(command.isHasMainPromoCodeId());
        entity.setHasRemindPromoCode(command.isHasRemindPromoCodeId());
        entity.setMainMarketingTemplateId(command.getMainMarketingTemplateId());
        entity.setRemindMarketingTemplateId(command.getRemindMarketingTemplateId());
        entity.setEnableRemind(command.isEnableRemind());
        entity.setScheduleDate(command.getTriggerDate());

        if (command.getMainCampaignImage() != null) {
            entity.setMainImageFileId(saveImage(command.getMainCampaignImage()).getFileId());
        }
        if (command.getRemindCampaignImage() != null) {
            entity.setRemindImageFileId(saveImage(command.getRemindCampaignImage()).getFileId());
        }
    }

    private void updatePromoCodes(long communicationId, List<Long> clients) {
        MarketingCommunicationEntity communicationEntity = communicationRepository.getRequired(communicationId);
        MarketingCampaignEntity campaignEntity = repository.getRequired(communicationEntity.getMarketingCampaignId());

        List<String> clientNumbers = queryFactory.select(Entities.client.number).from(Entities.client).where(Entities.client.id.in(clients)).fetch();
        if (communicationEntity.getPromoCodeId() != null) {
            PromoCode promoCodeOffer = promoCodeService.getRequiredEntity(communicationEntity.getPromoCodeId());
            if (campaignEntity.getScheduleType() == null) {
                LocalDate expireDate = communicationEntity.isReminder() ? TimeMachine.today().plusDays(1) : TimeMachine.today().plusDays(7);
                promoCodeService.update(new UpdatePromoCodeCommand()
                    .setPromoCodeId(communicationEntity.getPromoCodeId())
                    .setEffectiveFrom(TimeMachine.today())
                    .setMaxTimesToApply((long) clientNumbers.size())
                    .setEffectiveTo(expireDate)
                    .setDescription(promoCodeOffer.getDescription())
                    .setRateInPercent(promoCodeOffer.getRateInPercent())
                    .setClientNumbers(clientNumbers)
                );
                promoCodeService.activate(communicationEntity.getPromoCodeId());
            } else {
                long timesSent = queryFactory.selectFrom(marketingCommunication)
                    .where(
                        marketingCommunication.marketingCampaignId.eq(communicationEntity.getMarketingCampaignId())
                            .and(marketingCommunication.status.eq(SENT))
                            .and(marketingCommunication.reminder.eq(communicationEntity.isReminder()))
                    )
                    .fetchCount();

                LocalDate expireDate;
                switch (campaignEntity.getScheduleType()) {
                    case DAILY:
                        expireDate = TimeMachine.today();
                        break;
                    case WEEKLY:
                    case MONTHLY:
                        expireDate = communicationEntity.isReminder() ? TimeMachine.today().plusDays(1) : TimeMachine.today().plusDays(7);
                        break;
                    default:
                        throw new IllegalStateException(String.format("Unknown Schedule type [%s]", campaignEntity.getScheduleType()));
                }
                if (timesSent == 0) {
                    promoCodeService.update(new UpdatePromoCodeCommand()
                        .setPromoCodeId(communicationEntity.getPromoCodeId())
                        .setEffectiveFrom(TimeMachine.today())
                        .setMaxTimesToApply((long) clientNumbers.size())
                        .setEffectiveTo(expireDate)
                        .setDescription(promoCodeOffer.getDescription())
                        .setRateInPercent(promoCodeOffer.getRateInPercent())
                        .setClientNumbers(clientNumbers)
                    );
                    promoCodeService.activate(communicationEntity.getPromoCodeId());
                } else {
                    PromoCode offer = promoCodeService.getRequiredEntity(communicationEntity.getPromoCodeId());
                    String base = offer.getCode();
                    Matcher m = Pattern.compile("^(\\w+)[DMW]\\d+\\w{2}$").matcher(base);
                    if (m.find()) {
                        base = m.group(1);
                    }
                    Long codeId = promoCodeService.create(new CreatePromoCodeCommand()
                        .setCode(String.format("%s%s%s%s", base, campaignEntity.getScheduleType().name().charAt(0), timesSent + 1, randomAlphabetic(2).toUpperCase()))
                        .setEffectiveFrom(TimeMachine.today())
                        .setEffectiveTo(expireDate)
                        .setRateInPercent(offer.getRateInPercent())
                        .setMaxTimesToApply((long) clientNumbers.size())
                        .setClientNumbers(clientNumbers)
                        .setDescription(offer.getDescription()));
                    promoCodeService.activate(codeId);
                    communicationEntity.setPromoCodeId(codeId);
                    communicationRepository.save(communicationEntity);
                }
            }
        }

    }

}
