package fintech.spain.unnax.webhook.model;

import fintech.constraints.AllowedValues;
import lombok.EqualsAndHashCode;
import lombok.Value;
import org.hibernate.validator.constraints.NotBlank;
import org.hibernate.validator.constraints.URL;

@Value
public class CreateWebHookRequest {

    @NotBlank
    @AllowedValues({"email", "callback"})
    private String client;

    @NotBlank
    private String event;

    @URL
    @NotBlank
    private String target;
}
