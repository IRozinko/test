package fintech.spain.alfa.web


import fintech.spain.alfa.product.web.WebLoginService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.http.HttpMethod
import org.springframework.stereotype.Component

import java.time.Duration

@Component
class ClientApiHelper {

    @Autowired
    TestRestTemplate restTemplate

    @Autowired
    WebLoginService webLoginService

    String getClietToken(long clientId) {
        return webLoginService.login(clientId, Duration.ofHours(2))
    }

    String getClientToken(long clientId, String authority) {
        return webLoginService.login(clientId, Duration.ofHours(2), authority)
    }

    fintech.spain.alfa.web.models.ClientInfoResponse getClientInfo(String token = null) {
        if (token == null) {
            return restTemplate.getForEntity("/api/public/web/client", fintech.spain.alfa.web.models.ClientInfoResponse.class).body
        } else {
            return restTemplate.exchange("/api/public/web/client", HttpMethod.GET, ApiHelper.authorized(token, ""), fintech.spain.alfa.web.models.ClientInfoResponse.class).body
        }
    }
}
