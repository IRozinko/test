package fintech.webanalytics

import fintech.webanalytics.db.WebAnalyticsEventRepository
import fintech.webanalytics.model.SaveEventCommand
import fintech.webanalytics.model.WebAnalyticsEventQuery
import org.springframework.beans.factory.annotation.Autowired

class WebAnalyticsServiceTest extends BaseSpecification {

    @Autowired
    WebAnalyticsService service

    @Autowired
    WebAnalyticsEventRepository repository

    def "save event"() {
        expect:
        repository.count() == 0

        when:
        def id = service.saveEvent(new SaveEventCommand(clientId: 1L, applicationId: 2L, loanId: 3L, eventType: "SIGN_UP", ipAddress: "192.168.0.1", utmSource: "facebook"))

        then:
        repository.count() == 1

        and:
        with (repository.getRequired(id)) {
            assert clientId == 1L
            assert applicationId == 2L
            assert loanId == 3L
            assert eventType == "SIGN_UP"
            assert ipAddress == "192.168.0.1"
            assert utmSource == "facebook"
        }
    }

    def "find latest"() {
        given:
        service.saveEvent(new SaveEventCommand(clientId: 1L, applicationId: 2L, loanId: 3L, eventType: "SIGN_UP", ipAddress: "192.168.0.1", utmSource: "facebook"))
        def id2 = service.saveEvent(new SaveEventCommand(clientId: 1L, applicationId: 2L, loanId: 3L, eventType: "SIGN_UP", ipAddress: "192.168.0.1", utmSource: "facebook"))
        def id3 = service.saveEvent(new SaveEventCommand(clientId: 1L, applicationId: 2L, loanId: 3L, eventType: "SIGN_IN", ipAddress: "192.168.0.1", utmSource: "facebook"))
        service.saveEvent(new SaveEventCommand(clientId: 3L, applicationId: 4L, loanId: 3L, eventType: "SIGN_UP", ipAddress: "192.168.0.1", utmSource: "facebook"))

        expect:
        !service.findLatest(new WebAnalyticsEventQuery(clientId: 0L)).isPresent()
        service.findLatest(new WebAnalyticsEventQuery(clientId: 1L)).get().id == id3
        service.findLatest(new WebAnalyticsEventQuery(clientId: 1L, applicationId: 2L, eventTypes: ["SIGN_UP"])).get().id == id2
        service.findLatest(WebAnalyticsEventQuery.byApplicationIdAndEventType(2L, "SIGN_IN")).get().id == id3
    }
}
