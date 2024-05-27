package fintech.spain.unnax.transfer.impl;

import fintech.spain.unnax.UnnaxClient;
import fintech.spain.unnax.model.TransferAutoUpdateResponse;
import fintech.spain.unnax.model.UnnaxResponse;
import fintech.spain.unnax.transfer.TransferAutoUnnaxClient;
import fintech.spain.unnax.transfer.model.TransferAutoDetails;
import fintech.spain.unnax.transfer.model.TransferAutoRequest;
import fintech.spain.unnax.transfer.model.TransferAutoResponse;
import fintech.spain.unnax.transfer.model.TransferAutoUpdateRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Primary;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import javax.validation.Valid;

@Slf4j
@Component(TransferAutoUnnaxClientImpl.NAME)
@Primary
@ConditionalOnProperty(name = "unnax.mock", havingValue = "false")
public class TransferAutoUnnaxClientImpl extends UnnaxClient implements TransferAutoUnnaxClient {

    public static final String NAME = "out-unnax-client";
    private static final String TRANSFER_OUT_URL = "/api/v3/payment/transfer/auto/";

    public TransferAutoUnnaxClientImpl(@Autowired @Qualifier("unnaxClient") RestTemplate restTemplate) {
        super(restTemplate);
    }

    @Override
    public UnnaxResponse<TransferAutoResponse> transferAuto(@Valid TransferAutoRequest request) {
        ResponseEntity<String> response = restTemplate.postForEntity(TRANSFER_OUT_URL,
            new HttpEntity<>(request), String.class);
        return getIfSuccess(response, 201, TransferAutoResponse.class);
    }

    @Override
    public UnnaxResponse<TransferAutoDetails> getDetails(String orderCode) {
        ResponseEntity<String> response = restTemplate.getForEntity(TRANSFER_OUT_URL + orderCode, String.class);
        return getIfSuccess(response, 200, TransferAutoDetails.class);
    }

    @Override
    public UnnaxResponse<TransferAutoUpdateResponse> update(String orderCode, TransferAutoUpdateRequest requestBody) {
        ResponseEntity<String> response = restTemplate.exchange(TRANSFER_OUT_URL + orderCode + "/", HttpMethod.PUT,
            new HttpEntity<>(requestBody), String.class);
        return getIfSuccess(response, 200, TransferAutoUpdateResponse.class);
    }

}
