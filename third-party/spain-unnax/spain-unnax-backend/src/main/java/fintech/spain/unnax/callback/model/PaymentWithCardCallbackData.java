package fintech.spain.unnax.callback.model;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class PaymentWithCardCallbackData implements CallbackData {

    private String pan;
    private String bin;
    private String currency;
    private String transactionType;
    private String expirationDate;
    private String expireMonth;
    private String expireYear;
    private String cardHolder;
    private String cardBrand;
    private String cardType;
    private String cardCountry;
    private String cardBank;
    private String orderCode;
    private String token;
    private String date;
    private Integer amount;
    private String concept;
    private Integer state;

}
