package fintech.spain.unnax.webhook.model

import fintech.testing.integration.JsonBaseSpecification
import org.assertj.core.api.Assertions
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.test.json.JacksonTester
import org.springframework.core.io.Resource

import java.time.ZoneId
import java.time.ZonedDateTime

class WebHookListJsonTest extends JsonBaseSpecification {

    @Autowired
    JacksonTester<WebHookListResponse> webHookListResponseTester

    @Value("classpath:webHookListResponse.json")
    Resource webHookListResponse

    def "WebHookListResponse"() {
        given:
        WebHookListResponse response = new WebHookListResponse()
            .setCount(2)
            .setNext(null)
            .setPrevious(null)
            .addResult(new WebHookListResponse.WebHookResult()
                .setId(1)
                .setClient("email")
                .setEvent("fitnance_read")
                .setTarget("test@mail.com")
                .setCreatedAt(ZonedDateTime.of(2017, 4, 11, 7, 46, 0, 0, ZoneId.systemDefault()))
                .setUpdatedAt(ZonedDateTime.of(2017, 4, 11, 7, 46, 0, 0, ZoneId.systemDefault())))
            .addResult(new WebHookListResponse.WebHookResult()
                .setId(2)
                .setClient("callback")
                .setEvent("fitnance_read")
                .setTarget("https://www.callback.com")
            .setCreatedAt(ZonedDateTime.of(2017, 4, 11, 7, 46, 36, 0, ZoneId.systemDefault()))
            .setUpdatedAt(ZonedDateTime.of(2017, 4, 11, 7, 46, 36, 0, ZoneId.systemDefault())))

        expect:
        Assertions.assertThat(webHookListResponseTester.write(response)).isEqualToJson(webHookListResponse)

        and:
        Assertions.assertThat(webHookListResponseTester.read(webHookListResponse)).isEqualTo(response)
    }

}
