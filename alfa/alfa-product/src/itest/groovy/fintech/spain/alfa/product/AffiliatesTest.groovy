package fintech.spain.alfa.product

import fintech.TimeMachine
import fintech.affiliate.AffiliateService
import fintech.affiliate.db.AffiliateEventRepository
import fintech.affiliate.db.Entities
import fintech.affiliate.model.EventType
import fintech.affiliate.model.ReportStatus
import fintech.affiliate.model.SavePartnerCommand
import fintech.lending.core.application.LoanApplicationStatusDetail
import fintech.webanalytics.WebAnalyticsService
import fintech.webanalytics.model.WebAnalyticsEventQuery
import org.springframework.beans.factory.annotation.Autowired

class AffiliatesTest extends AbstractAlfaTest {

    @Autowired
    AffiliateService affiliateService

    @Autowired
    AffiliateEventRepository affiliateEventRepository

    @Autowired
    WebAnalyticsService webAnalyticsService

    @Autowired
    AffiliateEventRepository eventRepository

    @Autowired
    AffiliateService service

    def "affiliate - analytics data is provided in reporting"() {
        given:
        final testPartnerName = "ALFA_TEST_AFFILIATE"
        service.savePartner(new SavePartnerCommand(
            name: testPartnerName,
            active: true,
            leadReportUrl: "http://alfa-affiliate.com/lead?utm_source={{webEvent.utmSource}}&utm_campaign={{webEvent.utmCampaign}}&gclid={{webEvent.gclid}}&utm_medium={{webEvent.utmMedium}}&utm_content={{webEvent.utmContent}}&utm_term={{webEvent.utmTerm}}",
            actionReportUrl: "http://alfa-affiliate.com/action?utm_source={{webEvent.utmSource}}&utm_campaign={{webEvent.utmCampaign}}&gclid={{webEvent.gclid}}&utm_medium={{webEvent.utmMedium}}&utm_content={{webEvent.utmContent}}&utm_term={{webEvent.utmTerm}}"
        ))

        def client = fintech.spain.alfa.product.testing.TestFactory.newClient()
        client.signUpForm.affiliate = new fintech.spain.alfa.product.registration.forms.AffiliateData()
            .setAffiliateName(testPartnerName)
            .setAffiliateLeadId("1")
        client.signUpForm.analytics = new fintech.spain.alfa.product.registration.forms.AnalyticsData()
            .setGclid("affiliate-gclid")
            .setUtmCampaign("adservice-campaign")
            .setUtmContent("adservice-content")
            .setUtmMedium("affiliate")
            .setUtmSource("Adservice")
            .setUtmTerm("affiliate-term")

        when:
        client.signUp().toLoanAffiliateWorkflow().runAll()

        then:
        eventRepository.count() == 1
        with(eventRepository.findAll()[0]) {
            eventType == EventType.ACTION
            clientId == client.clientId
            reportStatus == ReportStatus.PENDING
            reportUrl == "http://alfa-affiliate.com/action?utm_source=Adservice&utm_campaign=adservice-campaign&gclid=affiliate-gclid&utm_medium=affiliate&utm_content=adservice-content&utm_term=affiliate-term"
        }
    }

    def "affiliate and web analytics are stored"() {
        given:
        def client = fintech.spain.alfa.product.testing.TestFactory.newClient()
        client.signUpForm.affiliate = new fintech.spain.alfa.product.registration.forms.AffiliateData()
            .setAffiliateName(AlfaConstants.TEST_AFFILIATE_NAME)
            .setAffiliateLeadId("1")
        client.signUpForm.analytics = new fintech.spain.alfa.product.registration.forms.AnalyticsData()
            .setGclid("affiliate-gclid")
            .setUtmCampaign("adservice-campaign")
            .setUtmContent("adservice-content")
            .setUtmMedium("affiliate")
            .setUtmSource("Adservice")
            .setUtmTerm("affiliate-term")

        when:
        client.signUp().toLoanAffiliateWorkflow().runAll()

        then:
        def report = affiliateService.findLeadReportByClientIdAndApplicationId(client.getClientId(), client.applicationId)
        assert report.isPresent()
        with(report.get()) {
            assert reportedEventTypes.contains(EventType.LEAD)
            assert reportedEventTypes.contains(EventType.ACTION)
        }

        and:
        with(webAnalyticsService.findLatest(new WebAnalyticsEventQuery()
            .setClientId(client.getClientId())
            .setEventTypes([AlfaConstants.WEB_ANALYTICS_SIGN_UP_EVENT])).get()) {
            it.gclid == "affiliate-gclid"
            it.utmCampaign == "adservice-campaign"
            it.utmContent == "adservice-content"
            it.utmMedium == "affiliate"
            it.utmSource == "Adservice"
            it.utmTerm == "affiliate-term"
        }
    }

    def "ACTION/LEAD event is not stored for second issued loan if that is not affiliated"() {
        given:
        def client = fintech.spain.alfa.product.testing.TestFactory.newClient()
        client.signUpForm.affiliate = new fintech.spain.alfa.product.registration.forms.AffiliateData()
            .setAffiliateName(AlfaConstants.TEST_AFFILIATE_NAME)
            .setAffiliateLeadId("1")

        when:
        client
            .signUp()
            .toLoanAffiliateWorkflow()
            .runAll()
            .exportDisbursement()
            .toLoan()
            .repayAll(TimeMachine.today())

        then:
        def leadEvents = affiliateEventRepository.findAll(Entities.event.clientId.eq(client.getClientId()).and(Entities.event.eventType.eq(EventType.LEAD)))
        def actionEvents = affiliateEventRepository.findAll(Entities.event.clientId.eq(client.getClientId()).and(Entities.event.eventType.eq(EventType.ACTION)))

        assert leadEvents.size() == 1
        assert actionEvents.size() == 1

        when:
        client
            .submitApplicationAndStartFirstLoanWorkflow(100.00, 15, TimeMachine.today())
            .toLoanWorkflow().runAll()

        then:
        def afterRepeatedLoanLeadEvents = affiliateEventRepository.findAll(Entities.event.clientId.eq(client.getClientId()).and(Entities.event.eventType.eq(EventType.LEAD)))
        def afterRepeatedLoanActionEvents = affiliateEventRepository.findAll(Entities.event.clientId.eq(client.getClientId()).and(Entities.event.eventType.eq(EventType.ACTION)))

        assert afterRepeatedLoanLeadEvents.size() == 1
        assert afterRepeatedLoanActionEvents.size() == 1
    }

    def "ACTION/LEAD event is stored for second issued loan if that is affiliated"() {
        given:
        def client = fintech.spain.alfa.product.testing.TestFactory.newClient()
        client.signUpForm.affiliate = new fintech.spain.alfa.product.registration.forms.AffiliateData()
            .setAffiliateName(AlfaConstants.TEST_AFFILIATE_NAME)
            .setAffiliateLeadId("1")

        when:
        client
            .signUp()
            .toLoanAffiliateWorkflow()
            .runAll()
            .exportDisbursement()
            .toLoan()
            .repayAll(TimeMachine.today())

        then:
        def leadEvents = affiliateEventRepository.findAll(Entities.event.clientId.eq(client.getClientId()).and(Entities.event.eventType.eq(EventType.LEAD)))
        def actionEvents = affiliateEventRepository.findAll(Entities.event.clientId.eq(client.getClientId()).and(Entities.event.eventType.eq(EventType.ACTION)))

        assert leadEvents.size() == 1
        assert actionEvents.size() == 1

        when:
        client
            .submitApplicationAndStartAffiliateWorkflow(100.00, 15, TimeMachine.today())
            .toLoanAffiliateWorkflow().runAll()

        then:
        def afterRepeatedLoanLeadEvents = affiliateEventRepository.findAll(Entities.event.clientId.eq(client.getClientId()).and(Entities.event.eventType.eq(EventType.LEAD)))
        def afterRepeatedLoanActionEvents = affiliateEventRepository.findAll(Entities.event.clientId.eq(client.getClientId()).and(Entities.event.eventType.eq(EventType.ACTION)))

        assert afterRepeatedLoanLeadEvents.size() == 2
        assert afterRepeatedLoanActionEvents.size() == 2
    }

    def "ACTION/LEAD event is not fired for repeated lead"() {
        given:
        def client = fintech.spain.alfa.product.testing.TestFactory.newClient()
        client.signUpForm.affiliate = new fintech.spain.alfa.product.registration.forms.AffiliateData()
            .setAffiliateName(AlfaConstants.TEST_AFFILIATE_NAME)
            .setAffiliateLeadId("1")

        when:
        client
            .signUp()
            .toLoanAffiliateWorkflow()
            .runAll()
            .exportDisbursement()
            .toLoan()
            .repayAll(TimeMachine.today())

        then:
        def leadEvents = affiliateEventRepository.findAll(Entities.event.clientId.eq(client.getClientId()).and(Entities.event.eventType.eq(EventType.LEAD)))
        def actionEvents = affiliateEventRepository.findAll(Entities.event.clientId.eq(client.getClientId()).and(Entities.event.eventType.eq(EventType.ACTION)))

        assert leadEvents.size() == 1
        assert actionEvents.size() == 1

        with(leadEvents[0]) {
            !it.lead.repeatedClient
            it.reportStatus == ReportStatus.PENDING
        }
        with(actionEvents[0]) {
            !it.lead.repeatedClient
            it.reportStatus == ReportStatus.PENDING
        }

        when:
        client
            .submitApplicationAndStartAffiliateWorkflow(100.00, 15, TimeMachine.today())
            .toLoanAffiliateWorkflow().runAll()

        then:
        def afterRepeatedLoanLeadEvents = affiliateEventRepository.findAll(Entities.event.clientId.eq(client.getClientId()).and(Entities.event.eventType.eq(EventType.LEAD)))
        def afterRepeatedLoanActionEvents = affiliateEventRepository.findAll(Entities.event.clientId.eq(client.getClientId()).and(Entities.event.eventType.eq(EventType.ACTION)))

        assert afterRepeatedLoanLeadEvents.size() == 2
        assert afterRepeatedLoanActionEvents.size() == 2

        with(afterRepeatedLoanLeadEvents.find { it.id != leadEvents[0].id }) {
            it.lead.repeatedClient
            it.reportStatus == ReportStatus.IGNORED
        }
        with(afterRepeatedLoanActionEvents.find { it.id != actionEvents[0].id }) {
            it.lead.repeatedClient
            it.reportStatus == ReportStatus.IGNORED
        }
    }

    def "If client creates second time application directly and first application was rejected, then action event is not issued"() {
        given:
        def client = fintech.spain.alfa.product.testing.TestFactory.newClient()
        client.signUpForm.affiliate = new fintech.spain.alfa.product.registration.forms.AffiliateData()
            .setAffiliateName(AlfaConstants.TEST_AFFILIATE_NAME)
            .setAffiliateLeadId("1")

        when:
        client
            .signUp()
            .toLoanAffiliateWorkflow()
            .runBeforeActivity(fintech.spain.alfa.product.workflow.undewrtiting.UnderwritingWorkflows.Activities.APPROVE_LOAN_OFFER)
            .completeActivity(fintech.spain.alfa.product.workflow.undewrtiting.UnderwritingWorkflows.Activities.APPROVE_LOAN_OFFER, fintech.spain.alfa.product.workflow.common.Resolutions.REJECT)

        then:
        def actionEvents = affiliateEventRepository.findAll(Entities.event.clientId.eq(client.getClientId()).and(Entities.event.eventType.eq(EventType.ACTION)))

        assert actionEvents.size() == 0

        when:

        def application = client.submitApplicationAndStartFirstLoanWorkflow(100.00, 15, TimeMachine.today())
            .toLoanWorkflow().runAll().toApplication()

        then:
        def afterRepeatedLoanActionEvents = affiliateEventRepository.findAll(Entities.event.clientId.eq(client.getClientId()).and(Entities.event.eventType.eq(EventType.ACTION)))

        assert application.getStatusDetail() == LoanApplicationStatusDetail.APPROVED
        assert afterRepeatedLoanActionEvents.size() == 0
    }

    def "If client creates second time application through affiliate and first application was cancelled, then action event is issued"() {
        given:
        def client = fintech.spain.alfa.product.testing.TestFactory.newClient()
        client.signUpForm.affiliate = new fintech.spain.alfa.product.registration.forms.AffiliateData()
            .setAffiliateName(AlfaConstants.TEST_AFFILIATE_NAME)
            .setAffiliateLeadId("1")

        when:
        client
            .signUp()
            .toLoanAffiliateWorkflow()
            .runBeforeActivity(fintech.spain.alfa.product.workflow.undewrtiting.UnderwritingWorkflows.Activities.APPROVE_LOAN_OFFER)
            .completeActivity(fintech.spain.alfa.product.workflow.undewrtiting.UnderwritingWorkflows.Activities.APPROVE_LOAN_OFFER, fintech.spain.alfa.product.workflow.common.Resolutions.CANCEL)

        then:
        def actionEvents = affiliateEventRepository.findAll(Entities.event.clientId.eq(client.getClientId()).and(Entities.event.eventType.eq(EventType.ACTION)))

        assert actionEvents.size() == 0

        when:

        def application = client.submitApplicationAndStartAffiliateWorkflow(100.00, 15, TimeMachine.today())
            .toLoanAffiliateWorkflow().runAll().toApplication()

        then:
        def afterRepeatedLoanActionEvents = affiliateEventRepository.findAll(Entities.event.clientId.eq(client.getClientId()).and(Entities.event.eventType.eq(EventType.ACTION)))

        assert application.getStatusDetail() == LoanApplicationStatusDetail.APPROVED
        assert afterRepeatedLoanActionEvents.size() == 1
    }

}
