package fintech.spain.alfa.web

import fintech.crm.security.OneTimeTokenService
import fintech.crm.security.db.TokenType
import fintech.spain.MarketingApi
import fintech.spain.alfa.product.testing.TestFactory
import fintech.spain.web.common.ApiError
import fintech.web.api.models.OkResponse
import org.springframework.beans.factory.annotation.Autowired

import java.time.Duration

class MarketingApiTest extends AbstractAlfaApiTest {

    @Autowired
    OneTimeTokenService oneTimeTokenService

    def "Unsubscribing disables client's marketing consent"() {
        given:
        def client = TestFactory.newClient().registerDirectly()
        assert clientService.get(client.clientId).acceptMarketing

        when: "bad request for invalid token usage"
        def error = restTemplate.postForEntity("/api/public/unsubscribe", new MarketingApi.UnsubscribeRequest(email: client.email, token: "invalid_token"), ApiError.class)

        then:
        assert error.statusCodeValue == 400
        error.body.fieldErrors.get("token").code == "InvalidValue"

        when:
        def token = oneTimeTokenService.generateOrUpdateToken(TokenType.MARKETING_UNSUBSCRIBE, client.clientId, Duration.ofHours(1))
        def result = restTemplate.postForEntity("/api/public/unsubscribe", new MarketingApi.UnsubscribeRequest(email: client.email, token: token), OkResponse.class)

        then:
        assert result.statusCodeValue == 200
        !clientService.get(client.clientId).acceptMarketing
    }
}
