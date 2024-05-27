package fintech.spain.unnax.webhook.model

import spock.lang.Specification

class WebHookListResponseTest extends Specification {

    def "FindWebHook"() {
        given:
        WebHookListResponse response = new WebHookListResponse()
        response.setResults(Arrays.asList(
            new WebHookListResponse.WebHookResult()
                .setTarget("target")
                .setEvent("event")
        ))

        when:
        def webhook = response.findWebHook("event", "target")

        then:
        webhook.isPresent()
        with(webhook.get()) {
            event ==  "event"
            target == "target"
        }

        when:
        webhook = response.findWebHook("event", "target1")

        then:
        !webhook.isPresent()
    }

}
