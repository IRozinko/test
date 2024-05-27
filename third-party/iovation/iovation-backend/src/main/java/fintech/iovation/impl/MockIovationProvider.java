package fintech.iovation.impl;

import lombok.Setter;
import org.springframework.stereotype.Component;

@Setter
@Component(MockIovationProvider.NAME)
public class MockIovationProvider implements IovationProvider {

    public static final String NAME = "mock-iovation-provider";

    public static final String IOVATION_TRACKING_NUMBER = "tracking number";
    public static final String IOVATION_DEVICE_ID = "device identifier";

    private boolean throwError;

    private IovationResponse response = generateResponse("A");

    @Override
    public IovationResponse request(IovationRequest request) {
        if (throwError) {
            throw new RuntimeException("Simulation Iovation error");
        }
        return response;
    }

    public static IovationResponse generateResponse(String result) {
        IovationResponse response = new IovationResponse();
        response.setResult(result);
        response.setReason("MOCK");
        response.setTrackingNumber(IOVATION_TRACKING_NUMBER);
        response.getDetails().put("device.screen", "1440X3440");
        response.getDetails().put("device.alias", IOVATION_DEVICE_ID);
        return response;
    }

    public void setThrowError(boolean throwError) {
        this.throwError = throwError;
    }

    public void setResponse(IovationResponse response) {
        this.response = response;
    }
}
