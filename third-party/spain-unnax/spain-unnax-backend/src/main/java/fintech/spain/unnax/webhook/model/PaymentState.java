package fintech.spain.unnax.webhook.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum PaymentState {

    NEW(1),
    COMPLETED(3),
    ERROR(4),
    CANCELLED(5),
    PARTIALLY_REFUNDED(6),
    REFUNDED(7),
    PENDING_CONFIRMATION(10),
    FRAUD(12);

    private final int value;

}
