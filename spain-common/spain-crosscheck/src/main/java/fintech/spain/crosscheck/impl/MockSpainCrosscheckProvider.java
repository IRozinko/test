package fintech.spain.crosscheck.impl;

import fintech.Validate;
import fintech.spain.crosscheck.model.SpainCrosscheckInput;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Setter
@Slf4j
@Component(MockSpainCrosscheckProvider.NAME)
public class MockSpainCrosscheckProvider implements SpainCrosscheckProvider {

    public static final String NAME = "mock-spain-crosscheck-provider";

    private boolean throwError = false;

    private SpainCrosscheckResponse response;

    public MockSpainCrosscheckProvider() {
        this.response = notFoundResponse();
    }

    @Override
    public SpainCrosscheckResponse request(SpainCrosscheckInput input) {
        if (throwError) {
            throw new RuntimeException("Mock crosscheck request failed");
        }
        Validate.notNull(response);
        log.warn("Returning mock crosscheck response: [{}]", response);
        return response;
    }

    public static SpainCrosscheckResponse notFoundResponse() {
        return new SpainCrosscheckResponse()
            .setError(false)
            .setResponseBody("{mock: true}")
            .setResponseStatusCode(200)
            .setAttributes(new SpainCrosscheckResponse.Attributes()
                .setBlacklisted(false)
                .setClientNumber("MOCK")
                .setFound(false)
                .setMaxDpd(0)
                .setOpenLoans(0)
                .setRepeatedClient(false)
            );
    }
}
