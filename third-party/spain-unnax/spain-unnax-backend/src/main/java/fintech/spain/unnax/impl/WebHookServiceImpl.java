package fintech.spain.unnax.impl;

import com.google.common.annotations.VisibleForTesting;
import fintech.Validate;
import fintech.spain.unnax.WebHookService;
import fintech.spain.unnax.db.WebHookEntity;
import fintech.spain.unnax.db.WebHookRepository;
import fintech.spain.unnax.model.UnnaxResponse;
import fintech.spain.unnax.model.WebHookEvents;
import fintech.spain.unnax.webhook.WebHookUnnaxClient;
import fintech.spain.unnax.webhook.model.CreateWebHookRequest;
import fintech.spain.unnax.webhook.model.CreateWebHookResponse;
import fintech.spain.unnax.webhook.model.WebHookListRequest;
import fintech.spain.unnax.webhook.model.WebHookListResponse;
import fintech.spain.unnax.webhook.model.WebHookType;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.stream.Stream;

import static fintech.spain.unnax.callback.UnnaxCallbackApi.UNNAX_CALLBACK_ENDPOINT;
import static fintech.spain.unnax.db.Entities.webHook;

@Service
@Slf4j
@Transactional
public class WebHookServiceImpl implements WebHookService {

    private final WebHookUnnaxClient webHookClient;
    private final WebHookRepository webHookRepository;

    private final String backendUrl;

    public WebHookServiceImpl(WebHookUnnaxClient webHookClient, WebHookRepository webHookRepository,
                              @Value("${spain.backend.baseUrl:http://localhost:8080}") String backendUrl) {
        this.webHookClient = webHookClient;
        this.webHookRepository = webHookRepository;
        this.backendUrl = backendUrl;
    }

    @EventListener(ApplicationReadyEvent.class)
    public void init() {
        log.info("Setup Unnax webhooks...");
        Stream.of(WebHookEvents.values())
            .forEach(event -> registerWebHook(event.getType(), event.getName()));
    }

    @Override
    @VisibleForTesting
    public WebHookEntity registerWebHook(WebHookType type, String event) {
        return Optional.ofNullable(
            webHookRepository.findOneOrNull(webHook.type.eq(type)
                .and(webHook.event.eq(event))
                .and(webHook.target.eq(webHookTarget(event))))
        ).orElseGet(() -> createWebHook(type, event, webHookTarget(event)));
    }

    @VisibleForTesting
    protected WebHookEntity createWebHook(WebHookType type, String event, String target) {
        WebHookEntity entity = findExistedWebHook(new WebHookListRequest().setClient(type.getName()).setEvent(event), target)
            .map(WebHookEntity::new)
            .orElseGet(() -> {
                CreateWebHookRequest req = new CreateWebHookRequest(type.getName(), event, target);
                UnnaxResponse<CreateWebHookResponse> resp = webHookClient.createWebHook(req);
                Validate.isTrue(!resp.isError(), "Can't create Unnax web hook.");
                return new WebHookEntity(resp.getResponse());
            });
        return webHookRepository.save(entity);
    }

    @VisibleForTesting
    protected String webHookTarget(String event) {
        Validate.isTrue(!StringUtils.isBlank(event), "Unnax Event name can't be empty.");
        return backendUrl + UNNAX_CALLBACK_ENDPOINT + event;
    }

    @VisibleForTesting
    protected Optional<WebHookListResponse.WebHookResult> findExistedWebHook(WebHookListRequest request, String target) {
        return webHookClient.webHooks(request).getResponse()
            .findWebHook(request.getEvent(), target);
    }

}
