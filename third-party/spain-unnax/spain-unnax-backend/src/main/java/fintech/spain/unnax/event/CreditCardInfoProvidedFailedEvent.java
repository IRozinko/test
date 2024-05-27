package fintech.spain.unnax.event;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class CreditCardInfoProvidedFailedEvent {
    private String clientNumber;
}
