package fintech.spain.unnax.model;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class CreditCardQuery {

    private Long id;
    private String orderCode;
    private String clientNumber;
    private Boolean enableAutoRepayments;
    private Long expireYear;
    private Long expireMonth; 

    public static CreditCardQuery byOrderCode(String orderCode) {
        return new CreditCardQuery()
            .setOrderCode(orderCode);
    }

    public static CreditCardQuery byClientNumber(String clientNumber) {
        return new CreditCardQuery()
            .setClientNumber(clientNumber);
    }

    public static CreditCardQuery forAutomaticCharge(String clientNumber) {
        return new CreditCardQuery()
            .setClientNumber(clientNumber)
            .setEnableAutoRepayments(Boolean.TRUE);
    }

    public static CreditCardQuery byId(Long creditCardId) {
        return new CreditCardQuery()
            .setId(creditCardId);
    }

}
