package fintech.spain.unnax

import fintech.spain.unnax.config.UnnaxClientConfig
import org.springframework.boot.web.client.RestTemplateBuilder
import org.springframework.test.web.client.MockRestServiceServer
import org.springframework.web.client.RestTemplate
import spock.lang.Specification

class UnnaxClientSpecification extends Specification {

    static final String API_ID = "1"
    static final String API_CODE = "code"
    static final String API_URI = "http://localhost";

    UnnaxClientConfig unnaxClientConfig
    RestTemplate restTemplate
    MockRestServiceServer mockServer

    void setup() {
        unnaxClientConfig = new UnnaxClientConfig()
        restTemplate = unnaxClientConfig.unnaxClient(new RestTemplateBuilder(), API_ID, API_CODE, API_URI)
        mockServer = MockRestServiceServer.bindTo(restTemplate).build()
    }


}
