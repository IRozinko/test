package fintech.spain.unnax.webhook.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum CreditCardState {

    SUCCESS(3), ERROR(4), CANCELED(5);

    private final int value;

}
