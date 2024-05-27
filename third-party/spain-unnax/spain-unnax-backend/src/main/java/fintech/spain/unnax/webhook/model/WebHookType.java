package fintech.spain.unnax.webhook.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum WebHookType {

    EMAIL("email"), CALLBACK("callback");

    private String name;

}
