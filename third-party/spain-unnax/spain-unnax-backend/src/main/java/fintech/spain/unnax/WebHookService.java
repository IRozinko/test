package fintech.spain.unnax;

import fintech.spain.unnax.db.WebHookEntity;
import fintech.spain.unnax.webhook.model.WebHookType;

public interface WebHookService {

    WebHookEntity registerWebHook(WebHookType type, String event);

}
