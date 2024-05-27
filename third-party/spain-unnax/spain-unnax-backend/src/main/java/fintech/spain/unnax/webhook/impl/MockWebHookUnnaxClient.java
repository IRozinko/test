package fintech.spain.unnax.webhook.impl;

import fintech.TimeMachine;
import fintech.spain.unnax.model.UnnaxResponse;
import fintech.spain.unnax.webhook.WebHookUnnaxClient;
import fintech.spain.unnax.webhook.model.*;
import org.springframework.stereotype.Component;

import java.time.ZoneId;
import java.util.*;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

@Component(MockWebHookUnnaxClient.NAME)
public class MockWebHookUnnaxClient implements WebHookUnnaxClient {

    public static final String NAME = "mock-webhook-unnax-client";

    private final List<WebHookListResponse.WebHookResult> webHooks;
    private final AtomicLong seq;

    public MockWebHookUnnaxClient() {
        this.webHooks = new ArrayList<>();
        this.seq = new AtomicLong();
    }

    @Override
    public UnnaxResponse<CreateWebHookResponse> createWebHook(CreateWebHookRequest request) {
        CreateWebHookResponse webHookResponse = new CreateWebHookResponse().setClient(request.getClient())
            .setId(seq.incrementAndGet())
            .setTarget(request.getTarget())
            .setEvent(request.getEvent())
            .setState(WebHookState.ENABLED.getIndex())
            .setCreatedAt(TimeMachine.now())
            .setUpdatedAt(TimeMachine.now());
        webHooks.add(toWebHookResult(webHookResponse));
        return new UnnaxResponse<>(webHookResponse);
    }

    @Override
    public UnnaxResponse<WebHookListResponse> webHooks(WebHookListRequest request) {
        WebHookListResponse listResponse = new WebHookListResponse()
            .setCount(webHooks.size())
            .setResults(webHooks.stream()
                .filter(hook -> !Optional.ofNullable(request.getEvent()).isPresent()
                    || hook.getEvent().equals(Optional.ofNullable(request.getEvent()).get()))
                .filter(hook -> !Optional.ofNullable(request.getClient()).isPresent()
                    || hook.getClient().equals(Optional.ofNullable(request.getClient()).get()))
                .limit(request.getLimit())
                .collect(Collectors.toList()));
        return new UnnaxResponse<>(listResponse);
    }

    private WebHookListResponse.WebHookResult toWebHookResult(CreateWebHookResponse response) {
        return new WebHookListResponse.WebHookResult()
            .setId(response.getId())
            .setTarget(response.getTarget())
            .setCreatedAt(response.getCreatedAt().atZone(ZoneId.systemDefault()))
            .setUpdatedAt(response.getUpdatedAt().atZone(ZoneId.systemDefault()))
            .setClient(response.getClient())
            .setEvent(response.getEvent());
    }
}
