package fintech.spain.unnax.webhook.impl;

import fintech.spain.unnax.UnnaxClient;
import fintech.spain.unnax.model.UnnaxResponse;
import fintech.spain.unnax.webhook.WebHookUnnaxClient;
import fintech.spain.unnax.webhook.model.CreateWebHookRequest;
import fintech.spain.unnax.webhook.model.CreateWebHookResponse;
import fintech.spain.unnax.webhook.model.WebHookListRequest;
import fintech.spain.unnax.webhook.model.WebHookListResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Primary;
import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import javax.validation.Valid;

@Slf4j
@Component(WebHookUnnaxClientImpl.NAME)
@Primary
@ConditionalOnProperty(name = "unnax.mock", havingValue = "false")
public class WebHookUnnaxClientImpl extends UnnaxClient implements WebHookUnnaxClient {

    public static final String NAME = "webhook-unnax-client";

    public WebHookUnnaxClientImpl(@Autowired @Qualifier("unnaxClient") RestTemplate restTemplate) {
        super(restTemplate);
    }

    @Override
    public UnnaxResponse<CreateWebHookResponse> createWebHook(@Valid CreateWebHookRequest request) {
        ResponseEntity<String> response = restTemplate.postForEntity("/api/v3/webhooks/",
            new HttpEntity<>(request), String.class);

        return getIfSuccess(response, 201, CreateWebHookResponse.class);
    }

    @Override
    public UnnaxResponse<WebHookListResponse> webHooks(WebHookListRequest request) {
        ResponseEntity<String> response = restTemplate.getForEntity("/api/v3/webhooks/", String.class, request.toParams());
        return getIfSuccess(response, 200, WebHookListResponse.class);
    }

}
