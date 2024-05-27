package fintech.spain.unnax.webhook;

import fintech.spain.unnax.model.UnnaxResponse;
import fintech.spain.unnax.webhook.model.CreateWebHookRequest;
import fintech.spain.unnax.webhook.model.CreateWebHookResponse;
import fintech.spain.unnax.webhook.model.WebHookListRequest;
import fintech.spain.unnax.webhook.model.WebHookListResponse;

import javax.validation.Valid;

public interface WebHookUnnaxClient {

    UnnaxResponse<CreateWebHookResponse> createWebHook(@Valid CreateWebHookRequest request);

    UnnaxResponse<WebHookListResponse> webHooks(WebHookListRequest request);
}
