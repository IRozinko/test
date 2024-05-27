package fintech.spain.unnax.webhook

import fintech.JsonUtils
import fintech.spain.unnax.UnnaxClientSpecification
import fintech.spain.unnax.model.WebHookEvents
import fintech.spain.unnax.webhook.impl.WebHookUnnaxClientImpl
import fintech.spain.unnax.webhook.model.CreateWebHookRequest
import fintech.spain.unnax.webhook.model.CreateWebHookResponse
import fintech.spain.unnax.webhook.model.WebHookListRequest
import fintech.spain.unnax.webhook.model.WebHookListResponse
import fintech.spain.unnax.webhook.model.WebHookType
import org.springframework.test.web.client.match.MockRestRequestMatchers
import org.springframework.test.web.client.response.MockRestResponseCreators

import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo

class WebHookUnnaxClientTest extends UnnaxClientSpecification {

    def "CreateWebHook"() {
        given:
        WebHookUnnaxClientImpl webHookClient = new WebHookUnnaxClientImpl(restTemplate)
        CreateWebHookRequest request = new CreateWebHookRequest(WebHookType.CALLBACK.name,
            WebHookEvents.EVENT_PAYMENT_TRANSFER_AUTO_CREATED.name, "/test")
        CreateWebHookResponse response = new CreateWebHookResponse()
            .setId(1)
            .setState(1)
            .setEvent(request.getEvent())
            .setTarget(request.getTarget())
            .setClient(request.getClient())

        when:
        mockServer.expect(requestTo(UnnaxClientSpecification.API_URI + "/api/v3/webhooks/"))
            .andExpect(MockRestRequestMatchers.header("Authorization",
            "Unnax " + Base64.encoder.encodeToString(String.join(":", UnnaxClientSpecification.API_ID, UnnaxClientSpecification.API_CODE).getBytes())))
            .andRespond(
            MockRestResponseCreators.withCreatedEntity(new URI(""))
                .body(JsonUtils.writeValueAsString(response))
        )

        def responseEntity = webHookClient.createWebHook(request)

        then:
        !responseEntity.isError()
        response == responseEntity.getResponse()
        mockServer.verify()
    }

    def "WebHooks List"() {
        given:
        WebHookUnnaxClientImpl webHookClient = new WebHookUnnaxClientImpl(restTemplate)

        WebHookListRequest request = new WebHookListRequest()
            .setClient("client")
            .setEvent("test-event")
            .setLimit(10)

        WebHookListResponse response = new WebHookListResponse()
            .setCount(1)
            .setResults(Arrays.asList(new WebHookListResponse.WebHookResult().setClient("client").setEvent("test-event")))

        when:
        mockServer.expect(requestTo(UnnaxClientSpecification.API_URI + "/api/v3/webhooks/"))
            .andExpect(MockRestRequestMatchers.header("Authorization",
            "Unnax " + Base64.encoder.encodeToString(String.join(":", UnnaxClientSpecification.API_ID, UnnaxClientSpecification.API_CODE).getBytes())))
            .andRespond(MockRestResponseCreators.withSuccess().body(JsonUtils.writeValueAsString(response))
        )

        def responseEntity = webHookClient.webHooks(request)

        then:
        !responseEntity.isError()
        response == responseEntity.getResponse()
        mockServer.verify()
    }
}
