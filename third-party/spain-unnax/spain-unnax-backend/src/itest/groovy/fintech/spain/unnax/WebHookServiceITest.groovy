package fintech.spain.unnax

import fintech.spain.unnax.db.WebHookRepository
import fintech.spain.unnax.webhook.model.WebHookType
import fintech.testing.integration.AbstractBaseSpecification
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.transaction.annotation.Transactional
import spock.lang.Subject

import static fintech.spain.unnax.callback.UnnaxCallbackApi.UNNAX_CALLBACK_ENDPOINT

class WebHookServiceITest extends AbstractBaseSpecification {

    @Subject
    @Autowired
    WebHookService webHookService

    @Autowired
    WebHookRepository webHookRepository

    @Value('${spain.backend.baseUrl:http://localhost:8080}')
    String backendUrl

    def "register web hook"() {
        when:
        def webHook = webHookService.registerWebHook(WebHookType.CALLBACK, "test-event")

        then:
        webHook
        with(webHook) {
            id
            externalId
            type == WebHookType.CALLBACK
            event == "test-event"
            target == backendUrl + UNNAX_CALLBACK_ENDPOINT + "test-event"
            webHookCreatedAt
            webHookUpdatedAt
        }
    }

    def "register web hook - already existed in db"() {
        when:
        def webHook = webHookService.registerWebHook(WebHookType.CALLBACK, "test-event")

        def webHook2 = webHookService.registerWebHook(WebHookType.CALLBACK, "test-event")

        then:
        webHook
        with(webHook) {
            id == webHook2.id
        }
    }

    @Transactional
    def "register web hook - already existed at Unnax"() {
        when:
        def webHook = webHookService.registerWebHook(WebHookType.CALLBACK, "test-event")

        and:
        webHookRepository.delete(webHook.id)
        def webHook2 = webHookService.registerWebHook(WebHookType.CALLBACK, "test-event")

        then:
        with(webHook2) {
            externalId == webHook.externalId
            webHookCreatedAt == webHook.webHookCreatedAt
            webHookUpdatedAt == webHook.webHookUpdatedAt
        }
    }
}
