package fintech.spain.unnax.charge.impl;

import fintech.spain.unnax.UnnaxClient;
import fintech.spain.unnax.charge.ChargeSavedCardUnnaxClient;
import fintech.spain.unnax.charge.model.ChargeClientCardRequest;
import fintech.spain.unnax.model.UnnaxResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Primary;
import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import javax.validation.Valid;

@Primary
@Component
@ConditionalOnProperty(name = "unnax.mock", havingValue = "false")
public class ChargeSavedCardUnnaxClientImpl extends UnnaxClient implements ChargeSavedCardUnnaxClient {

    public static final String CHARGE_URL = "/api/v3/payment/creditcard/charge/";

    public ChargeSavedCardUnnaxClientImpl(@Autowired @Qualifier("unnaxClient") RestTemplate restTemplate) {
        super(restTemplate);
    }

    @Override
    public UnnaxResponse<Void> charge(@Valid ChargeClientCardRequest request) {
        ResponseEntity<String> responseEntity = restTemplate.postForEntity(CHARGE_URL, new HttpEntity<>(request), String.class);
        return getIfSuccess(responseEntity, 200, Void.class);
    }

}
