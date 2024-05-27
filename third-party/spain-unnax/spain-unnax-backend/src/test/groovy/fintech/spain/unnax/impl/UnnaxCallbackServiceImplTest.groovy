package fintech.spain.unnax.impl

import fintech.spain.unnax.callback.model.CallbackRequest
import fintech.spain.unnax.db.UnnaxCallbackRepository
import org.springframework.context.ApplicationEventPublisher
import spock.lang.Specification
import spock.lang.Subject

class UnnaxCallbackServiceImplTest extends Specification {

    @Subject
    UnnaxCallbackServiceImpl callbackService

    UnnaxCallbackRepository unnaxCallbackRepository
    ApplicationEventPublisher eventPublisher
    String appId = "id"
    String apiCode = "code"

    void setup() {
        unnaxCallbackRepository = Mock(UnnaxCallbackRepository.class)
        eventPublisher = Mock(ApplicationEventPublisher.class)
        callbackService = new UnnaxCallbackServiceImpl(unnaxCallbackRepository, eventPublisher, appId, apiCode)
    }

    def "IsSignatureValid"() {
        when:
        def isValid = callbackService.isSignatureValid(new CallbackRequest().setResponseId("1234")
            .setSignature("ea59b3c20c590ba9a889ddb8c7be5c87ee95db85"))

        then:
        isValid

        when:
        isValid = callbackService.isSignatureValid(new CallbackRequest().setResponseId("1234")
            .setSignature("e8f88f223f7a6269966a74f404fa24ae039302e8"))

        then:
        !isValid
    }

}
