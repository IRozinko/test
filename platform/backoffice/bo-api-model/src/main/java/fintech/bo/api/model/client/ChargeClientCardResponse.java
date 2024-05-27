package fintech.bo.api.model.client;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class ChargeClientCardResponse {

    private String paymentOrderCode;

}
