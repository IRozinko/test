package fintech.spain.alfa.product.workflow.dormants.event;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class LocPreOfferAccepted {
    long clientId;
    String chanel;
}
