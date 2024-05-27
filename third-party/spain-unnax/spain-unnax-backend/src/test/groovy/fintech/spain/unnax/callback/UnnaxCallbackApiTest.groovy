package fintech.spain.unnax.callback

import fintech.JsonUtils
import fintech.spain.unnax.UnnaxCallbackService
import fintech.spain.unnax.UnnaxPayOutService
import fintech.spain.unnax.callback.model.CallbackRequest
import fintech.spain.unnax.callback.model.TransferAutoCreatedCallbackData
import fintech.spain.unnax.event.TransferAutoCreatedEvent
import fintech.spain.unnax.model.WebHookEvents
import spock.lang.Specification
import spock.lang.Subject

import java.time.LocalDateTime


class UnnaxCallbackApiTest extends Specification {

    UnnaxCallbackService unnaxCallbackService

    @Subject
    UnnaxCallbackApi callbackApi

    void setup() {
        unnaxCallbackService = Mock(UnnaxCallbackService.class)
        callbackApi = new UnnaxCallbackApi(unnaxCallbackService)
    }

    def "Process Callback"() {
        when:
        def response = callbackApi.processCallback(WebHookEvents.EVENT_PAYMENT_TRANSFER_AUTO_CREATED.name,
            new CallbackRequest()
                .setResponseId("123")
                .setDate(LocalDateTime.now())
                .setData(JsonUtils.readTree(
                    new TransferAutoCreatedCallbackData()
                        .setAmount(100)
                        .setDate(LocalDateTime.now().toLocalDate())
                        .setTime(LocalDateTime.now().toLocalTime()))
                )
        )

        then:
        1 * unnaxCallbackService.isSignatureValid(_ as CallbackRequest) >> true
        1 * unnaxCallbackService.publishEvent(_ as TransferAutoCreatedEvent)

        response.statusCodeValue == 204
    }

    def "Proccess callback failed. Unknown event"() {
        when:
        callbackApi.processCallback("unknown_event", new CallbackRequest())

        then:
        1 * unnaxCallbackService.isSignatureValid(_ as CallbackRequest) >> true
        def ex = thrown IllegalArgumentException
        ex.message.startsWith("Unnax Callback Event is not supported")
    }
}
