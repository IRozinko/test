package fintech.spain.unnax.impl

import com.querydsl.core.types.Predicate
import fintech.spain.unnax.db.WebHookEntity
import fintech.spain.unnax.db.WebHookRepository
import fintech.spain.unnax.model.UnnaxResponse
import fintech.spain.unnax.model.WebHookEvents
import fintech.spain.unnax.webhook.WebHookUnnaxClient
import fintech.spain.unnax.webhook.model.CreateWebHookRequest
import fintech.spain.unnax.webhook.model.CreateWebHookResponse
import fintech.spain.unnax.webhook.model.WebHookListRequest
import fintech.spain.unnax.webhook.model.WebHookListResponse
import fintech.spain.unnax.webhook.model.WebHookType
import spock.lang.Specification
import spock.lang.Subject

import java.time.ZonedDateTime

import static fintech.spain.unnax.callback.UnnaxCallbackApi.UNNAX_CALLBACK_ENDPOINT

class WebHookServiceTest extends Specification {

    @Subject
    WebHookServiceImpl webHookService

    WebHookUnnaxClient webHookUnnaxClient
    WebHookRepository webHookRepository

    String backendUrl = "http://localhost:8080"

    void setup() {
        webHookUnnaxClient = Mock(WebHookUnnaxClient.class)
        webHookRepository = Mock(WebHookRepository.class)

        webHookService = new WebHookServiceImpl(webHookUnnaxClient, webHookRepository, backendUrl)
    }

    def "Init"() {
        given:
        def webHooksCount = WebHookEvents.values().length
        when:
        webHookService.init()

        then:
        webHooksCount * webHookRepository.findOneOrNull(_ as Predicate) >> null
        webHooksCount * webHookUnnaxClient.webHooks(_ as WebHookListRequest) >> new UnnaxResponse<>(new WebHookListResponse())
        webHooksCount * webHookUnnaxClient.createWebHook(_ as CreateWebHookRequest) >> new UnnaxResponse<>(
            new CreateWebHookResponse().setClient(WebHookType.CALLBACK.name))
    }

    def "Init (already exists)"() {
        given:
        def webHooksCount = WebHookEvents.values().length

        when:
        webHookService.init()

        then:
        webHooksCount * webHookRepository.findOneOrNull(_ as Predicate) >> new WebHookEntity()
        0 * webHookUnnaxClient.createWebHook(_ as CreateWebHookRequest)
    }

    def "Init (already exists at Unnax)"() {
        given:
        def webHooksCount = WebHookEvents.values().length

        when:
        webHookService.init()

        then:
        webHooksCount * webHookRepository.findOneOrNull(_ as Predicate) >> null
        webHooksCount * webHookUnnaxClient.webHooks(_ as WebHookListRequest) >> {
            WebHookListRequest req ->
                new UnnaxResponse<>(new WebHookListResponse().setResults(Arrays.asList(newWebHookResult(req.getEvent()))))
        }
        0 * webHookUnnaxClient.createWebHook(_ as CreateWebHookRequest)
        webHooksCount * webHookRepository.save(_ as WebHookEntity)
    }

    def "RegisterWebHook"() {
        when:
        webHookService.registerWebHook(WebHookType.CALLBACK, "new_event")

        then:
        1 * webHookUnnaxClient.webHooks(_ as WebHookListRequest) >> new UnnaxResponse<>(new WebHookListResponse())
        1 * webHookRepository.findOneOrNull(_ as Predicate) >> null
        1 * webHookUnnaxClient.createWebHook(_ as CreateWebHookRequest) >> new UnnaxResponse<>(
            new CreateWebHookResponse().setClient(WebHookType.CALLBACK.name)
                .setEvent("new_event"))
    }

    def "Create WebHook"() {
        given:
        def target = backendUrl + UNNAX_CALLBACK_ENDPOINT + "new_event"

        when:
        webHookService.createWebHook(WebHookType.CALLBACK, "new_event", webHookService.webHookTarget("new_event"))

        then:
        1 * webHookUnnaxClient.webHooks(_ as WebHookListRequest) >> new UnnaxResponse<>(new WebHookListResponse())
        1 * webHookUnnaxClient.createWebHook(_ as CreateWebHookRequest) >> new UnnaxResponse<>(
            new CreateWebHookResponse()
                .setId(1)
                .setClient(WebHookType.CALLBACK.name)
                .setEvent("new_event")
                .setTarget(target))
        1 * webHookRepository.save(_ as WebHookEntity)
    }

    def "Unnax callback target"() {
        when:
        def target = webHookService.webHookTarget("new_event")

        then:
        target == backendUrl + UNNAX_CALLBACK_ENDPOINT + "new_event"

        when:
        webHookService.webHookTarget("")

        then:
        thrown IllegalArgumentException
    }

    def "Find existed web hook"() {
        when:
        webHookService.findExistedWebHook(new WebHookListRequest().setEvent("event"), "target")

        then:
        1 * webHookUnnaxClient.webHooks(_ as WebHookListRequest) >> new UnnaxResponse<>(new WebHookListResponse())
    }

    def newWebHookResult(String event) {
        return new WebHookListResponse.WebHookResult()
            .setId(1)
            .setEvent(event)
            .setClient(WebHookType.CALLBACK.name)
            .setCreatedAt(ZonedDateTime.now())
            .setUpdatedAt(ZonedDateTime.now())
            .setTarget(backendUrl + UNNAX_CALLBACK_ENDPOINT + event)
    }
}
