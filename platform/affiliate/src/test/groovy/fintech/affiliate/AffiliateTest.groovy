package fintech.affiliate

import fintech.JsonUtils
import fintech.TimeMachine
import fintech.affiliate.db.AffiliateEventRepository
import fintech.affiliate.db.AffiliateRequestRepository
import fintech.affiliate.impl.AffiliateReportSender
import fintech.affiliate.model.AddLeadCommand
import fintech.affiliate.model.AffiliateLead
import fintech.affiliate.model.EventType
import fintech.affiliate.model.LeadReport
import fintech.affiliate.model.ReportEventCommand
import fintech.affiliate.model.ReportStatus
import fintech.affiliate.model.SaveAffiliateRequestCommand
import fintech.affiliate.model.SavePartnerCommand
import org.springframework.beans.factory.annotation.Autowired
import spock.lang.Unroll

class AffiliateTest extends BaseSpecification {

    @Autowired
    AffiliateEventRepository eventRepository

    @Autowired
    AffiliateRequestRepository affiliateRequestRepository

    @Autowired
    AffiliateReportSender reportSender

    def "Report event"() {
        expect:
        eventRepository.count() == 0

        when:
        def partnerId = service.savePartner(new SavePartnerCommand(
            name: "MY_AFFILIATE",
            active: true,
            leadReportUrl: "http://myaffiliate.com/lead?id={{AFFILIATE_LEAD_ID}}",
            actionReportUrl: "http://myaffiliate.com/action?id={{AFFILIATE_LEAD_ID}}"
        ))
        def leadId = service.addLead(new AddLeadCommand(
            clientId: 1001L,
            applicationId: 1002L,
            affiliateName: "MY_AFFILIATE",
            affiliateLeadId: "L1001"
        )).get()
        service.reportEvent(new ReportEventCommand(
            eventType: EventType.LEAD,
            clientId: 1001L,
            applicationId: 1002L,
            loanId: 1003L
        ))
        service.reportEvent(new ReportEventCommand(
            eventType: EventType.ACTION,
            clientId: 1001L,
            applicationId: 1002L,
            loanId: 1003L
        ))

        then:
        eventRepository.count() == 2
        with(eventRepository.findAll()[0]) {
            eventType == EventType.LEAD
            clientId == 1001L
            reportStatus == ReportStatus.PENDING
            reportUrl == "http://myaffiliate.com/lead?id=L1001"
        }
        with(eventRepository.findAll()[1]) {
            eventType == EventType.ACTION
            clientId == 1001L
            reportStatus == ReportStatus.PENDING
            reportUrl == "http://myaffiliate.com/action?id=L1001"
        }

        when:
        def report = service.findLeadReportByClientIdAndApplicationId(1001L, 1002L).get()

        then:
        report.partnerId == partnerId
        report.leadId == leadId
        !report.unknownPartner
        report.reportedEventTypes.contains(EventType.ACTION)
        report.reportedEventTypes.contains(EventType.LEAD)

        and:
        def sameReport = service.findLeadReportByClientId(1001L).get()

        then:
        report == sameReport
    }

    def "No duplicate leads for same application"() {
        given:
        service.savePartner(new SavePartnerCommand(
            name: "MY_AFFILIATE",
            active: true
        ))
        service.addLead(new AddLeadCommand(
            clientId: 1001L,
            applicationId: 1002L,
            affiliateName: "MY_AFFILIATE",
            affiliateLeadId: "L1001"
        ))

        expect:
        !service.addLead(new AddLeadCommand(
            clientId: 1001L,
            applicationId: 1002L,
            affiliateName: "MY_AFFILIATE",
            affiliateLeadId: "L1001"
        )).isPresent()
    }

    def "Leads are created for same client but different application"() {
        given:
        service.savePartner(new SavePartnerCommand(
            name: "MY_AFFILIATE",
            active: true
        ))
        service.addLead(new AddLeadCommand(
            clientId: 1001L,
            applicationId: 1002L,
            affiliateName: "MY_AFFILIATE",
            affiliateLeadId: "L1001"
        ))

        expect:
        service.addLead(new AddLeadCommand(
            clientId: 1001L,
            applicationId: 1003L,
            affiliateName: "MY_AFFILIATE",
            affiliateLeadId: "L1001"
        )).isPresent()
    }

    def "No duplicate events for same application"() {
        given:
        service.savePartner(new SavePartnerCommand(
            name: "MY_AFFILIATE",
            active: true
        ))
        service.addLead(new AddLeadCommand(
            clientId: 1001L,
            applicationId: 1002L,
            affiliateName: "MY_AFFILIATE",
            affiliateLeadId: "L1001"
        ))
        service.reportEvent(new ReportEventCommand(
            eventType: EventType.LEAD,
            applicationId: 1002L,
            clientId: 1001L,
        ))
        service.reportEvent(new ReportEventCommand(
            eventType: EventType.ACTION,
            applicationId: 1002L,
            clientId: 1001L,
        ))

        expect:
        !service.reportEvent(new ReportEventCommand(
            eventType: EventType.LEAD,
            applicationId: 1002L,
            clientId: 1001L,
        )).isPresent()
        !service.reportEvent(new ReportEventCommand(
            eventType: EventType.ACTION,
            applicationId: 1002L,
            clientId: 1001L,
        )).isPresent()
    }

    def "Same type events are created for same client and different applications"() {
        given:
        service.savePartner(new SavePartnerCommand(
            name: "MY_AFFILIATE",
            active: true
        ))
        service.addLead(new AddLeadCommand(
            clientId: 1001L,
            applicationId: 1002L,
            affiliateName: "MY_AFFILIATE",
            affiliateLeadId: "L1001"
        ))
        service.reportEvent(new ReportEventCommand(
            eventType: EventType.LEAD,
            applicationId: 1002L,
            clientId: 1001L,
        ))
        service.reportEvent(new ReportEventCommand(
            eventType: EventType.ACTION,
            applicationId: 1002L,
            clientId: 1001L,
        ))

        when:
        service.addLead(new AddLeadCommand(
            clientId: 1001L,
            applicationId: 1003L,
            affiliateName: "MY_AFFILIATE",
            affiliateLeadId: "L1001"
        ))

        then:
        service.reportEvent(new ReportEventCommand(
            eventType: EventType.LEAD,
            applicationId: 1003L,
            clientId: 1001L,
        )).isPresent()
        service.reportEvent(new ReportEventCommand(
            eventType: EventType.ACTION,
            applicationId: 1003L,
            clientId: 1001L,
        )).isPresent()
    }

    @Unroll
    def "Event report status #_reportStatus: #_active, #_name, #_repeatedClient"() {
        given:
        service.savePartner(new SavePartnerCommand(
            name: "MY_AFFILIATE",
            active: _active,
            leadReportUrl: _reportUrl,
            repeatedClientLeadReportUrl: _repeatedClientReportUrl
        ))
        service.addLead(new AddLeadCommand(
            clientId: 1001L,
            applicationId: 1002L,
            affiliateName: _name,
            affiliateLeadId: "L1001",
            repeatedClient: _repeatedClient
        ))

        when:
        service.reportEvent(new ReportEventCommand(
            eventType: EventType.LEAD,
            applicationId: 1002L,
            clientId: 1001L,
        ))

        then:
        with(eventRepository.findAll()[0]) {
            eventType == EventType.LEAD
            reportStatus == _reportStatus
            reportUrl == _eventReportUrl
            clientId == 1001L
            applicationId == 1002L
        }

        where:
        _active | _repeatedClient | _reportUrl               | _repeatedClientReportUrl          | _name          | _reportStatus        | _eventReportUrl
        true    | false           | "http://myaffiliate.com" | "http://repeated.myaffiliate.com" | "MY_AFFILIATE" | ReportStatus.PENDING | "http://myaffiliate.com"
        false   | false           | "http://myaffiliate.com" | "http://repeated.myaffiliate.com" | "MY_AFFILIATE" | ReportStatus.IGNORED | ""
        true    | false           | ""                       | ""                                | "MY_AFFILIATE" | ReportStatus.IGNORED | ""
        true    | false           | "http://myaffiliate.com" | "http://repeated.myaffiliate.com" | "UNKNOWN"      | ReportStatus.IGNORED | ""
        true    | true            | "http://myaffiliate.com" | "http://repeated.myaffiliate.com" | "MY_AFFILIATE" | ReportStatus.PENDING | "http://repeated.myaffiliate.com"
        false   | true            | "http://myaffiliate.com" | "http://repeated.myaffiliate.com" | "MY_AFFILIATE" | ReportStatus.IGNORED | ""
        true    | true            | ""                       | ""                                | "MY_AFFILIATE" | ReportStatus.IGNORED | ""
        true    | true            | "http://myaffiliate.com" | "http://repeated.myaffiliate.com" | "UNKNOWN"      | ReportStatus.IGNORED | ""
    }

    def "Report request fails"() {
        given:
        service.savePartner(new SavePartnerCommand(
            name: "MY_AFFILIATE",
            active: true,
            leadReportUrl: "invalid http url",
        ))
        service.addLead(new AddLeadCommand(
            clientId: 1001L,
            applicationId: 1002L,
            affiliateName: "MY_AFFILIATE",
            affiliateLeadId: "L1001"
        ))
        def eventId = service.reportEvent(new ReportEventCommand(
            eventType: EventType.LEAD,
            applicationId: 1002L,
            clientId: 1001L,
        )).get()

        when:
        reportSender.send(TimeMachine.now().plusMinutes(1))

        then:
        with(eventRepository.getRequired(eventId)) {
            reportStatus == ReportStatus.PENDING
            reportRetryAttempts == 1
            !reportError.empty
        }

        when:
        reportSender.send(TimeMachine.now().plusHours(1))
        reportSender.send(TimeMachine.now().plusHours(2))
        reportSender.send(TimeMachine.now().plusHours(3))

        then:
        with(eventRepository.getRequired(eventId)) {
            reportStatus == ReportStatus.ERROR
        }
    }

    def "partner with api key"() {
        expect:
        !service.findActivePartnerByApiKey("test").isPresent()

        when:
        service.savePartner(new SavePartnerCommand(
            name: "MY_AFFILIATE",
            active: true,
            apiKey: "test"
        ))
        service.savePartner(new SavePartnerCommand(
            name: "OTHER_AFFILIATE",
            active: true,
            apiKey: "other"
        ))

        then:
        service.findActivePartnerByApiKey("test").get().name == "MY_AFFILIATE"

        when:
        service.savePartner(new SavePartnerCommand(
            name: "MY_AFFILIATE",
            active: false,
            apiKey: "test"
        ))

        then:
        !service.findActivePartnerByApiKey("test").isPresent()

        when:
        service.savePartner(new SavePartnerCommand(
            name: "MY_AFFILIATE",
            active: true,
            apiKey: ""
        ))

        then:
        !service.findActivePartnerByApiKey("test").isPresent()
    }

    def "save affiliate request with response"() {
        given:
        def command = new SaveAffiliateRequestCommand(
            clientId: 1L,
            applicationId: 11L,
            requestType: "TestRequest",
            request: new AffiliateLead(
                id: 9999L
            ),
            response: new LeadReport(
                leadId: 8888L
            )
        )

        when:
        def id = service.saveAffiliateRequest(command)

        then:
        def savedEntity = affiliateRequestRepository.getRequired(id)
        savedEntity != null
        savedEntity.clientId == 1L
        savedEntity.applicationId == 11L

        and: "request is saved correctly"
        JsonUtils.readValue(savedEntity.request, AffiliateLead.class).id == 9999L

        and: "response is saved correctly"
        JsonUtils.readValue(savedEntity.response, LeadReport.class).leadId == 8888L

    }
}
