package fintech.spain.unnax.webhook.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum WebHookState {

    ENABLED(1), DISABLED(2);

    private int index;

}
