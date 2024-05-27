package fintech.marketing

import fintech.JsonUtils
import fintech.TimeMachine
import fintech.cms.CmsContextBuilder
import fintech.cms.CmsModels
import fintech.cms.NotificationRenderer
import fintech.crm.client.ClientService
import fintech.crm.client.CreateClientCommand
import fintech.crm.client.UpdateClientCommand
import fintech.crm.client.db.ClientRepository
import fintech.email.db.EmailLogEntity
import fintech.email.db.EmailLogRepository
import fintech.lending.core.PeriodUnit
import fintech.lending.core.application.LoanApplicationService
import fintech.lending.core.application.commands.SubmitLoanApplicationCommand
import fintech.lending.core.promocode.CreatePromoCodeCommand
import fintech.lending.core.promocode.PromoCodeService
import fintech.lending.core.promocode.db.PromoCodeRepository
import fintech.marketing.db.Entities
import fintech.marketing.db.MarketingCampaignRepository
import fintech.marketing.db.MarketingCommunicationRepository
import fintech.marketing.db.MarketingTemplateRepository
import fintech.notification.NotificationService
import fintech.notification.db.NotificationRepository
import fintech.spain.notification.NotificationBuilder
import fintech.spain.notification.NotificationBuilderFactory
import fintech.spain.notification.NotificationConfig
import fintech.testing.integration.AbstractBaseSpecification
import org.apache.commons.lang3.RandomStringUtils
import org.spockframework.spring.SpringBean
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.transaction.support.TransactionTemplate

import javax.xml.bind.DatatypeConverter
import java.time.LocalDateTime

import static fintech.marketing.MarketingCommunicationStatus.CANCELLED
import static fintech.marketing.MarketingCommunicationStatus.QUEUED
import static fintech.marketing.MarketingCommunicationStatus.SENT
import static javax.xml.bind.DatatypeConverter.parseBase64Binary

class MarketingCampaignServiceTest extends AbstractBaseSpecification {

    private static final byte[] onePxGifImg = parseBase64Binary("R0lGODlhAQABAIAAAP///wAAACH5BAEAAAAALAAAAAABAAEAAAICRAEAOw==");

    @Autowired
    MarketingCampaignService campaignService

    @Autowired
    PromoCodeService promoCodeService

    @Autowired
    MarketingCommunicationRepository communicationRepository

    @Autowired
    MarketingCampaignRepository campaignRepository

    @Autowired
    MarketingTemplateRepository templateRepository

    @SpringBean
    CmsContextBuilder contextBuilder = Stub() {
        companyLocale() >> 'es'
    }

    @SpringBean
    CmsModels cmsModels = Stub() {
        clientContext((Long) _) >> new HashMap<String, Object>()
    }

    @Autowired
    NotificationRenderer notificationRenderer

    @Autowired
    NotificationService notificationService

    @Autowired
    ClientRepository clientRepository
    @Autowired
    PromoCodeRepository promoCodeRepository

    @Autowired
    MarketingService marketingService

    @Autowired
    LoanApplicationService loanApplicationService

    @Autowired
    NotificationRepository notificationRepository

    @SpringBean
    NotificationBuilderFactory notificationFactory = Mock()

    @Autowired
    TransactionTemplate txTemplate

    @SpringBean
    MarketingSettingsProvider marketingSettingsProvider = Stub()
        {
            getMarketingNotificationConfig() >> new NotificationConfig(
                emailFrom: "emailFrom@email.com",
                emailFromName: "emailFromName@email.com",
                smsSenderId: "smsSenderId",
                emailReplyTo: "emailReplyTo@email.com")
        }


    @Autowired
    ClientService clientService

    @Autowired
    EmailLogRepository emailLogRepository

    def setup() {
        notificationFactory.newNotification((Long) _, (NotificationConfig) _) >>
            { clientId, cfg ->
                new NotificationBuilder(contextBuilder, notificationRenderer, notificationService)
                    .clientId(clientId as Long)
                    .emailTo("${clientId}@email.com")
                    .emailFrom(cfg.emailFrom)
                    .emailFromName(cfg.emailFromName)
                    .emailReplyTo(cfg.emailReplyTo)
                    .smsSenderId(cfg.smsSenderId)
            }
    }

    def cleanup() {
        txTemplate.execute({
            communicationRepository.deleteAll()
            campaignRepository.deleteAll()
            templateRepository.deleteAll()
            notificationRepository.deleteAll()
            emailLogRepository.deleteAll()
            promoCodeRepository.findAll().each { promoCodeService.deactivate(it.id); promoCodeService.delete(it.id) }
            clientRepository.deleteAll()
        })
    }

    def "preview sms"() {
        given:
        def codeId = promoCodeService.create(new CreatePromoCodeCommand(code: 'MarketingMainTestCode', effectiveFrom: TimeMachine.today(), effectiveTo: TimeMachine.today(), rateInPercent: 15, maxTimesToApply: 100))
        when:
        def preview = campaignService.previewSms(codeId, "Use {{marketing.promoCode.promoCode}} to get {{marketing.promoCode.discountInPercent | numberformat(currencyFormat)}}% discount")
        then:
        preview == "Use MarketingMainTestCode to get 15% discount"
    }

    def "preview template"() {
        given:
        def templateId = campaignService.saveTemplate(new SaveMarketingTemplateCommand(name: "template_name",
            emailBody: "body",
            htmlTemplate: "{{marketing.mainImageBase64String}}",
            mainCampaignImage: onePxGifImg
        ))
        when:
        def preview = campaignService.previewTemplate(templateId)
        then:
        preview == DatatypeConverter.printBase64Binary(onePxGifImg)
    }

    def "preview campaign"() {
        given:
        def templateId = campaignService.saveTemplate(new SaveMarketingTemplateCommand(name: "template_name",
            emailBody: "body",
            htmlTemplate: "{{marketing.content}}",
            mainCampaignImage: onePxGifImg
        ))
        def codeId = promoCodeService.create(new CreatePromoCodeCommand(code: 'MarketingMainTestCode', effectiveFrom: TimeMachine.today(), effectiveTo: TimeMachine.today(), rateInPercent: 15, maxTimesToApply: 100))

        when:
        def preview = campaignService.previewCampaignEmail(null,
            onePxGifImg,
            templateId,
            codeId,
            "{{marketing.mainImageBase64String}} {{marketing.promoCode.promoCode}} {{marketing.promoCode.discountInPercent | numberformat(currencyFormat)}}",
            false)
        then:
        preview == "${DatatypeConverter.printBase64Binary(onePxGifImg)} MarketingMainTestCode 15"
    }

    def "toggle campaign status"() {
        when:
        def campaignId = saveDefaultCampaign(MarketingCampaignScheduleType.WEEKLY)

        then: "main communication automatically creates"
        def communicationId = communicationRepository.findAll()[0].id
        communicationRepository.getRequired(communicationId).status == QUEUED

        when:
        campaignService.toggleCampaignStatus(campaignId)

        then:
        communicationRepository.getRequired(communicationId).status == CANCELLED

        when:
        campaignService.toggleCampaignStatus(campaignId)

        then: "new communication should be created"
        with(communicationRepository.findAll(Entities.marketingCommunication.createdAt.desc()).first()) {
            id > communicationId
            status == QUEUED
        }
    }


    def "dynamic promo codes for automated campaigns"() {
        given:
        def clientId = submitLoanApplication()
        def mainCodeId = promoCodeService.create(new CreatePromoCodeCommand(code: 'MarketingMainTestCode', effectiveFrom: TimeMachine.today(), effectiveTo: TimeMachine.today(), rateInPercent: 15, maxTimesToApply: 100))
        def remindCodeId = promoCodeService.create(new CreatePromoCodeCommand(code: 'MarketingRemindTestCode', effectiveFrom: TimeMachine.today(), effectiveTo: TimeMachine.today(), rateInPercent: 25, maxTimesToApply: 100))

        when:
        saveDefaultCampaign(MarketingCampaignScheduleType.WEEKLY, mainCodeId, remindCodeId)

        then: "main communication automatically creates"
        def communicationId = communicationRepository.findAll()[0].id

        when:
        campaignService.triggerCommunication(communicationId)

        then:
        promoCodeService.getRequiredEntity(mainCodeId).effectiveTo == TimeMachine.today().plusDays(7)
        with(promoCodeService.getPromoCodeOffer('MarketingMainTestCode', clientId).get()) {
            assert it.promoCodeId == mainCodeId
            assert it.promoCode == 'MarketingMainTestCode'
            assert it.discountInPercent == 15
        }

        and: "reminder communication automatically creates"
        communicationRepository.findAll().size() == 2
        def remindCommunicationId = communicationRepository.findAll(Entities.marketingCommunication.createdAt.desc()).first().id
        when:
        campaignService.triggerCommunication(remindCommunicationId)

        then:
        promoCodeService.getRequiredEntity(remindCodeId).effectiveTo == TimeMachine.today().plusDays(1)
        with(promoCodeService.getPromoCodeOffer('MarketingRemindTestCode', clientId).get()) {
            assert it.promoCodeId == remindCodeId
            assert it.promoCode == 'MarketingRemindTestCode'
            assert it.discountInPercent == 25
        }
        def automatedMainCommunicationId = communicationRepository.findAll(Entities.marketingCommunication.createdAt.desc()).first().id

        when:
        campaignService.triggerCommunication(automatedMainCommunicationId)

        then:
        def newMainPromoCodeId = communicationRepository.getRequired(automatedMainCommunicationId).promoCodeId
        newMainPromoCodeId > mainCodeId
        def promoCodeNew = promoCodeService.getRequiredEntity(newMainPromoCodeId)
        with(promoCodeNew) {
            assert it.code ==~ /^MarketingMainTestCodeW2\w{2}$/
            assert it.rateInPercent == 15
            assert !it.newClientsOnly
            assert it.effectiveTo == TimeMachine.today().plusDays(7)
        }

        with(promoCodeService.getPromoCodeOffer(promoCodeNew.code, clientId).get()) {
            assert it.promoCodeId == promoCodeNew.id
            assert it.discountInPercent == 15
        }

        def automatedRemindCommunicationId = communicationRepository.findAll(Entities.marketingCommunication.createdAt.desc()).first().id
        when:
        campaignService.triggerCommunication(automatedRemindCommunicationId)

        then:
        def newRemindPromoCodeId = communicationRepository.getRequired(automatedRemindCommunicationId).promoCodeId
        newRemindPromoCodeId > remindCodeId
        def newRemindPromoCode = promoCodeService.getRequiredEntity(newRemindPromoCodeId)
        with(newRemindPromoCode) {
            assert it.code ==~ /^MarketingRemindTestCodeW2\w{2}$/
            assert it.rateInPercent == 25
            assert !it.newClientsOnly
            assert it.effectiveTo == TimeMachine.today().plusDays(1)
        }

        with(promoCodeService.getPromoCodeOffer(newRemindPromoCode.code, clientId).get()) {
            assert it.promoCodeId == newRemindPromoCode.id
            assert it.discountInPercent == 25
        }
        emailLogRepository.findAll().size() == 4
    }

    def "dynamic promo codes for one time sending campaigns"() {
        given:
        def clientId = submitLoanApplication()
        def mainCodeId = promoCodeService.create(new CreatePromoCodeCommand(code: 'MarketingMainTestCode', effectiveFrom: TimeMachine.today(), effectiveTo: TimeMachine.today(), rateInPercent: 15, maxTimesToApply: 100))
        def remindCodeId = promoCodeService.create(new CreatePromoCodeCommand(code: 'MarketingRemindTestCode', effectiveFrom: TimeMachine.today(), effectiveTo: TimeMachine.today(), rateInPercent: 25, maxTimesToApply: 100))

        when:
        saveDefaultCampaign(null, mainCodeId, remindCodeId)

        then: "main communication automatically creates"
        def communicationId = communicationRepository.findAll()[0].id

        when:
        campaignService.triggerCommunication(communicationId)

        then:
        promoCodeService.getRequiredEntity(mainCodeId).effectiveTo == TimeMachine.today().plusDays(7)
        !promoCodeService.getRequiredEntity(mainCodeId).newClientsOnly
        with(promoCodeService.getPromoCodeOffer('MarketingMainTestCode', clientId).get()) {
            assert it.promoCodeId == mainCodeId
            assert it.promoCode == 'MarketingMainTestCode'
            assert it.discountInPercent == 15
        }

        and: "reminder communication automatically creates"
        communicationRepository.findAll().size() == 2
        def remindCommunicationId = communicationRepository.findAll().stream()
            .filter({ c -> c.isReminder() })
            .findFirst().get().id

        when:
        campaignService.triggerCommunication(remindCommunicationId)

        then:
        promoCodeService.getRequiredEntity(remindCodeId).effectiveTo == TimeMachine.today().plusDays(1)
        !promoCodeService.getRequiredEntity(remindCodeId).newClientsOnly
        with(promoCodeService.getPromoCodeOffer('MarketingRemindTestCode', clientId).get()) {
            assert it.promoCodeId == remindCodeId
            assert it.promoCode == 'MarketingRemindTestCode'
            assert it.discountInPercent == 25
        }
    }

    def "happy marketing flow"() {
        given:
        def clientId = submitLoanApplication()

        when:
        def campaignId = saveDefaultCampaign()

        then: "template is saved"
        templateRepository.findAll().size() == 1

        and: "communication automatically creates"
        campaignRepository.findAll().size() == 1
        communicationRepository.findAll().size() == 1
        def communicationId = communicationRepository.findAll()[0].id

        when:
        campaignService.triggerCommunication(communicationId)

        then: "reminder communication automatically creates"
        communicationRepository.findAll().size() == 2
        def remindCommunicationId = communicationRepository.findAll().stream()
            .filter({ c -> c.isReminder() })
            .findFirst().get().id

        and: "email is sent"
        emailLogRepository.findAll().size() == 1
        EmailLogEntity emailLog = emailLogRepository.findAll()[0]
        with(emailLog) {
            assert to == "${clientId}@email.com"
            assert from == 'emailFrom@email.com'
            assert fromName == 'emailFromName@email.com'
            assert subject == 'subject'
            assert replyTo == 'emailReplyTo@email.com'
        }

        def emailBody = emailLog.body
        def dataMatcher = emailBody =~ /<p id="content">(.+)<\/p>/
        dataMatcher.find()
        def data = dataMatcher.group(1).split(";")
        communicationId == data[0] as Long
        campaignId == data[1] as Long
        def clickUuid = data[2]
        def viewUuid = data[3]

        and:
        with(communicationRepository.getRequired(communicationId)) {
            assert it.clickRate == null
            assert it.targetedUsers == 1
            assert it.status == SENT
            assert it.viewRate == null
        }

        when:
        marketingService.trackClicks(clickUuid)

        then:
        with(communicationRepository.getRequired(communicationId)) {
            assert it.clickRate == 1.0
            assert it.targetedUsers == 1
            assert it.viewRate == null
        }

        when:
        marketingService.trackViews(viewUuid)

        then:
        with(communicationRepository.getRequired(communicationId)) {
            assert it.clickRate == 1.0
            assert it.targetedUsers == 1
            assert it.viewRate == 1.0
        }

        when:
        campaignService.triggerCommunication(remindCommunicationId)

        then: "reminder email sent"
        emailLogRepository.findAll().size() == 2
    }

    def saveDefaultCampaign(scheduleType = null, mainPromoCodeId = null, remindPromoCodeId = null) {
        campaignService.saveTemplate(new SaveMarketingTemplateCommand(name: "template_name",
            emailBody: "body",
            htmlTemplate: """<html>
                <body>
                <div>
                <p><img width=\"1024\" height=\"512\" src=\"data:image/jpeg;base64,{{marketing.mainImageBase64String}}\"/> </p>
                </div>
                {% autoescape false %}
                <p id="content">{{marketing.content}}</p>
                {% endautoescape %}
                </body>
                </html>""",
            mainCampaignImage: onePxGifImg
        ))
        return campaignService.saveCampaign(new SaveMarketingCampaignCommand(
            name: 'name',
            emailBody: '{{marketing.communicationId}};{{marketing.campaignId}};{{marketing.trackClickUuid}};{{marketing.trackViewUuid}}',
            emailSubject: 'subject',
            remindEmailBody: 'test',
            remindEmailSubject: 'test',
            scheduleType: scheduleType,
            remindIntervalHours: 12,
            triggerDate: LocalDateTime.now(),
            audienceSettingsJson: JsonUtils.writeValueAsString(new MarketingAudienceSettings()),
            mainPromoCodeId: mainPromoCodeId,
            remindPromoCodeId: remindPromoCodeId,
            hasMainPromoCodeId: mainPromoCodeId != null,
            hasRemindPromoCodeId: remindPromoCodeId != null,
            mainCampaignImage: onePxGifImg,
            remindCampaignImage: onePxGifImg,
            mainMarketingTemplateId: templateRepository.findAll()[0].id,
            remindMarketingTemplateId: templateRepository.findAll()[0].id,
            enableRemind: true
        ))
    }

    def submitLoanApplication() {
        def clientId = clientService.create(new CreateClientCommand("C${RandomStringUtils.randomNumeric(8)}"))
        clientService.update(new UpdateClientCommand(clientId: clientId, acceptMarketing: true))

        def cmd = new SubmitLoanApplicationCommand(clientId: clientId,
            applicationNumber: RandomStringUtils.randomAlphabetic(8),
            productId: 100,
            principal: 100,
            submittedAt: TimeMachine.now(),
            periodCount: 30,
            periodUnit: PeriodUnit.DAY,
            loansPaid: 0)
        loanApplicationService.submit(cmd)
        return clientId
    }

}
