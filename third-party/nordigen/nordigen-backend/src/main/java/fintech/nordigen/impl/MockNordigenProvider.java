package fintech.nordigen.impl;

import fintech.ClasspathUtils;
import fintech.nordigen.model.NordigenStatus;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import java.util.LinkedHashMap;
import java.util.Map;

@Slf4j
@Component(MockNordigenProvider.NAME)
public class MockNordigenProvider implements NordigenProvider {

    public static final String NAME = "mock-nordigen-provider";

    private NordigenResponse response = okResponse("ES1910240863456024678400");

    private Map<Long, NordigenResponse> responseByClient = new LinkedHashMap<Long, NordigenResponse>() {
        @Override
        protected boolean removeEldestEntry(Map.Entry eldest) {
            return size() > 10;
        }
    };

    private boolean throwError;

    @Override
    public NordigenResponse request(Long clientId, String requestBody) {
        if (throwError) {
            throw new RuntimeException("Simulating Nordigen failure");
        }
        log.info("Returning fake Nordigen response");
        NordigenResponse responseToReturn = responseByClient.getOrDefault(clientId, response);
        return responseToReturn;
    }

    public static NordigenResponse okResponse(String iban) {
        NordigenResponse response = new NordigenResponse();
        String template = ClasspathUtils.resourceToString("nordigen/nordigen-fake-response.json");
        template = StringUtils.replace(template, "#iban#", iban);
        response.setResponseBody(template);
        response.setResponseStatusCode(200);
        response.setStatus(NordigenStatus.OK);
        return response;
    }

    public void setResponse(NordigenResponse response) {
        this.response = response;
    }

    public void addResponseForExpectedRequestBody(Long clientId, NordigenResponse response) {
        responseByClient.put(clientId, response);
    }

    public void setThrowError(boolean throwError) {
        this.throwError = throwError;
    }
}
