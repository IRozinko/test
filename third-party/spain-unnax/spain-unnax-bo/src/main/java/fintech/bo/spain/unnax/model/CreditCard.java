package fintech.bo.spain.unnax.model;

import lombok.Data;

@Data
public class CreditCard {

    private Long id;
    private String clientNumber;
    private String callbackTransactionId;
    private boolean active;
    private String cardToken;
    private Long cardExpireYear;
    private Long cardExpireMonth;
    private String cardHolderName;
    private String cardBrand;
    private String cardBank;
    private String orderCode;
    private String errorDetails;
    private String status;
    private Boolean automaticPaymentEnabled;
    private String pan;
    private Long bin;

}
