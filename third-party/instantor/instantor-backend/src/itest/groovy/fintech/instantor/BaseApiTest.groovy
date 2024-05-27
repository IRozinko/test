package fintech.instantor

import fintech.IntegrationApplication
import fintech.db.SystemEnvironment
import fintech.instantor.parser.InstantorParser
import fintech.instantor.parser.impl.InsightInstantorParser
import fintech.testing.integration.AbstractBaseSpecification
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.ContextConfiguration
import org.springframework.util.LinkedMultiValueMap
import org.springframework.util.MultiValueMap

@ActiveProfiles("itest")
@ContextConfiguration
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
    classes = IntegrationApplication.class)
abstract class BaseApiTest extends AbstractBaseSpecification {

    @Autowired
    TestRestTemplate restTemplate

    @Autowired
    SystemEnvironment systemEnvironment

    @Autowired
    InstantorService instantorService

    def setup() {
        testDatabase.cleanDb()
        instantorService.setInstantorParser(instantorParser())
    }

    private InstantorParser instantorParser() {
        return new InsightInstantorParser(new TestingInsightInstantorDataResolver(), systemEnvironment)
    }

    def formPostRequest(Map<String, String> params) {
        def headers = new HttpHeaders()
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED)
        def map = new LinkedMultiValueMap<String, String>()
        params.forEach({ k, v -> map.put(k, [v]) })
        def request = new HttpEntity<MultiValueMap<String, String>>(map, headers)
        return request
    }
}
