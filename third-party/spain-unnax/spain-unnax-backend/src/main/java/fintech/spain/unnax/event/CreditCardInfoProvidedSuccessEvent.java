package fintech.spain.unnax.event;

import fintech.spain.unnax.model.CreditCard;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class CreditCardInfoProvidedSuccessEvent {
    
    private CreditCard lastActive;
    private String clientNumber;
}
