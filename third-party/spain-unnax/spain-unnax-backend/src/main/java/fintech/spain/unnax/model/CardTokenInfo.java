package fintech.spain.unnax.model;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class CardTokenInfo {
    private String cardToken;
    private boolean automaticPaymentEnabled;
}
