package fintech.fintechmarket

import com.fasterxml.jackson.databind.ObjectMapper
import fintech.fintechmarket.dto.StartInquiryRequest
import fintech.fintechmarket.impl.InquiryFintechMarketClientBean
import org.springframework.http.HttpMethod
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.test.web.client.ExpectedCount
import org.springframework.test.web.client.MockRestServiceServer
import org.springframework.test.web.client.match.MockRestRequestMatchers
import org.springframework.web.client.RestTemplate
import spock.lang.Specification

import static fintech.fintechmarket.impl.MockInquiryFintechMarketClient.startInquiryResponse
import static fintech.fintechmarket.impl.MockInquiryFintechMarketClient.newInquiry
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo
import static org.springframework.test.web.client.response.MockRestResponseCreators.withStatus

class InquiryFintechMarketClientTest extends Specification {

    String serverBaseUrl = "https://localhost/api"
    InquiryFintechMarketClient client
    MockRestServiceServer mockServer
    ObjectMapper mapper = new ObjectMapper()


    def "setup"() {
        def restTemplate = new RestTemplate()
        mockServer = MockRestServiceServer.createServer(restTemplate)
        client = new InquiryFintechMarketClientBean(serverBaseUrl, restTemplate)
    }

    def "newInquiry"() {
        given:
        def scenario = 'test_scenario'
        mockServer.expect(ExpectedCount.once(), requestTo(serverBaseUrl + "/scenarios/${scenario}/inquiries/new"))
            .andRespond(withStatus(HttpStatus.OK)
            .contentType(MediaType.APPLICATION_JSON)
            .body(mapper.writeValueAsString(newInquiry(scenario, 2))))

        when:
        client.newInquiry(scenario, "")

        then:
        mockServer.verify()
    }

    def "startInquiry"() {
        given:
        def scenario = 'test_scenario'
        mockServer.expect(ExpectedCount.once(), requestTo(serverBaseUrl + "/scenarios/${scenario}/inquiries/new"))
            .andRespond(withStatus(HttpStatus.OK)
            .contentType(MediaType.APPLICATION_JSON)
            .body(mapper.writeValueAsString(newInquiry(scenario, 2))))

        mockServer.expect(ExpectedCount.once(), requestTo(serverBaseUrl + "/scenarios/${scenario}/inquiries"))
            .andExpect(MockRestRequestMatchers.method(HttpMethod.POST))
            .andRespond(withStatus(HttpStatus.CREATED)
            .contentType(MediaType.APPLICATION_JSON)
            .body(mapper.writeValueAsString(startInquiryResponse())))

        when:
        def inquiry = client.newInquiry(scenario, "")
        def resp = client.startInquiry(scenario,
                new StartInquiryRequest()
                        .setData(new StartInquiryRequest.Data()
                                .setBrandKey("alfa")
                                .setEntityExternalId("1")
                                .setPersonExternalId("2")
                                .setLockVersion(inquiry.data.lockVersion)
                                .setFields(['test_field': 1])), "")

        then:
        mockServer.verify()
    }

    def "startInquiry - wrong lock version"() {
        given:
        def scenario = 'test_scenario'
        mockServer.expect(ExpectedCount.once(), requestTo(serverBaseUrl + "/scenarios/${scenario}/inquiries/new"))
            .andRespond(withStatus(HttpStatus.OK)
            .contentType(MediaType.APPLICATION_JSON)
            .body(mapper.writeValueAsString(newInquiry(scenario, 2))))

        mockServer.expect(ExpectedCount.once(), requestTo(serverBaseUrl + "/scenarios/${scenario}/inquiries"))
            .andExpect(MockRestRequestMatchers.method(HttpMethod.POST))
            .andRespond(withStatus(HttpStatus.CONFLICT))

        when:
        client.newInquiry(scenario, "")
        client.startInquiry(scenario,
                new StartInquiryRequest()
                        .setData(new StartInquiryRequest.Data()
                                .setBrandKey("alfa")
                                .setEntityExternalId("1")
                                .setPersonExternalId("2")
                                .setLockVersion(1)
                                .setFields(['test_field': 1])), "")

        then:
        mockServer.verify()
        thrown Exception
    }




}
