package fintech.ga

import fintech.ga.db.GAClientDataRepository
import fintech.ga.db.GARequestLogRepository
import fintech.ga.events.GAEvent
import fintech.testing.integration.AbstractBaseSpecification
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.transaction.support.TransactionTemplate
import org.springframework.web.util.UriComponentsBuilder

class GAServiceTest extends AbstractBaseSpecification {

    @Autowired
    private GARequestLogRepository logRepository

    @Autowired
    private GAService gaService

    @Autowired
    private GAClientDataRepository clientDataRepository

    @Autowired
    private TransactionTemplate txTemplate

    def setup() {
        txTemplate.execute {
            logRepository.deleteAll()
            clientDataRepository.deleteAll()
        }
    }

    def "correctly save request logs"() {
        given:
        def clientId = 123
        def cookie = "GA1.2.123456"
        def userAgent = "Chrome 80"

        initClientData(clientId, cookie, userAgent)
        def event = new GAEvent() {
            Map<String, String> getParams() {
                [ec: "Transaction", el: "vivus", dp: "d_p"]
            }

            Map<String, String> getUnknownCidParams() {
                [:]
            }

            Long getClientId() {
                clientId
            }
        }
        when:
        gaService.sendEvent(event)


        then:
        with(clientDataRepository.findByClientId(clientId).get()) {
            def logs = logRepository.findByClientId(clientId);
            logs.size() == 1
            with(logs.get(0)) {
                def queryParams = UriComponentsBuilder.fromUriString(it.request).build().getQueryParams().toSingleValueMap()
                queryParams.size() == 5
                queryParams.get("ec") == "Transaction"
                queryParams.get("el") == "vivus"
                queryParams.get("dp") == "d_p"
                it.clientId == clientId
                it.response == "OK"
                it.responseCode == 200
            }
        }
    }

    def "correctly save user data"() {
        given:
        def clientId = 123
        def cookie = "GA1.2.123456"
        def userAgent = "Chrome 80"
        when:
        initClientData(clientId, cookie, userAgent)

        then:
        with(clientDataRepository.findByClientId(clientId).get()) {
            it.clientId == 123
            it.cookieUserId == "123456"
            it.userAgent == "Chrome 80"
        }

    }

    private void initClientData(long clientId, String cookie, String userAgent) {
        gaService.saveOrUpdateCookie(clientId, cookie, userAgent)
    }
}
