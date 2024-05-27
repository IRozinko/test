package fintech.fintechmarket.impl;

import fintech.fintechmarket.InquiryFintechMarketClient;
import fintech.fintechmarket.dto.NewInquiryResponse;
import fintech.fintechmarket.dto.StartInquiryRequest;
import fintech.fintechmarket.dto.StartInquiryResponse;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Primary;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

@Component
@Primary
@ConditionalOnProperty(name = "fintechmarket.mock", havingValue = "false")
public class InquiryFintechMarketClientBean implements InquiryFintechMarketClient {

    private final String serverBaseUrl;
    private final RestTemplate restTemplate;
    private final static String ORGANIZATION_HEADER = "X-organization";

    public InquiryFintechMarketClientBean(@Value("${fintechmarket.baseUrl}") String serverBaseUrl,
                                          @Qualifier("fintechMarketRestTemplate") RestTemplate restTemplate) {
        this.serverBaseUrl = serverBaseUrl;
        this.restTemplate = restTemplate;
    }

    @Override
    public NewInquiryResponse newInquiry(String scenarioKey, String brand) {
        String uri = UriComponentsBuilder.fromUriString(serverBaseUrl)
            .path("/scenarios/{scenarioKey}/inquiries/new")
            .buildAndExpand(scenarioKey)
            .toUriString();

        ResponseEntity<NewInquiryResponse> response = restTemplate.exchange(uri,
            HttpMethod.GET, new HttpEntity<>(addOrganizationHeader(brand)), NewInquiryResponse.class);
        return response.getBody();
    }

    @Override
    public StartInquiryResponse startInquiry(String scenarioKey, StartInquiryRequest request, String brand) {
        String uri = UriComponentsBuilder.fromUriString(serverBaseUrl)
            .path("/scenarios/{scenarioKey}/inquiries")
            .buildAndExpand(scenarioKey)
            .toUriString();

        HttpEntity<StartInquiryRequest> entity = new HttpEntity<>(request, addOrganizationHeader(brand));
        ResponseEntity<StartInquiryResponse> response = restTemplate.exchange(uri, HttpMethod.POST, entity, StartInquiryResponse.class);
        return response.getBody();
    }

    private HttpHeaders addOrganizationHeader(String brand) {
        HttpHeaders headers = new HttpHeaders();
        headers.set(ORGANIZATION_HEADER, brand);
        return headers;
    }
}
