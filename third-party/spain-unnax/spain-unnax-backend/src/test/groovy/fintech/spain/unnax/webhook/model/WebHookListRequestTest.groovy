package fintech.spain.unnax.webhook.model

import spock.lang.Specification

class WebHookListRequestTest extends Specification {

    def "ToParams"() {
        given:
        WebHookListRequest request = new WebHookListRequest()

        when:
        request.setEvent("test-event")
        def params = request.toParams()

        then:
        params.containsKey("event")
        params.get("event") == "test-event"
        params.containsKey("limit")
        params.get("limit") == 100
        !params.containsKey("client")

        when:
        request = new WebHookListRequest()
        request.setClient("test-client")
        params = request.toParams()

        then:
        !params.containsKey("event")
        params.containsKey("limit")
        params.get("limit") == 100
        params.containsKey("client")
        params.get("client") == "test-client"

        when:
        request = new WebHookListRequest()
        request.setLimit(150)
        params = request.toParams()

        then:
        !params.containsKey("event")
        !params.containsKey("client")
        params.containsKey("limit")
        params.get("limit") == 150
    }

}
