package fintech.spain.unnax.callback.model;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class CreditCardPreAuthCallbackData implements CallbackData {

    private String pan;
    private String currency;
    private String transactionType;
    private String expirationDate;
    private String concept;
    private String cardHolder;
    private String orderCode;
    private String token;
    private String date;
    private Integer state;
    private Integer amount;
    private String cardBrand;
    private String expireYear;
    private String expireMonth;
    private String cardType;
    private String cardBank;
    private Long bin;
    private String errorMessage;
}
