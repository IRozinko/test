package fintech.spain.unnax.charge.impl;

import fintech.spain.unnax.charge.ChargeSavedCardUnnaxClient;
import fintech.spain.unnax.charge.model.ChargeClientCardRequest;
import fintech.spain.unnax.model.UnnaxResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class ChargeSavedCardUnnaxClientMock implements ChargeSavedCardUnnaxClient {

    @Override
    public UnnaxResponse<Void> charge(ChargeClientCardRequest request) {
        log.info("Charging payment card: {}", request);
        UnnaxResponse<Void> response = new UnnaxResponse<>(null);
        response.setError(false);
        return response;
    }

}
