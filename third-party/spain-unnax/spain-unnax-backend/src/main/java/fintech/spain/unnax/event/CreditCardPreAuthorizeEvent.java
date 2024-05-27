package fintech.spain.unnax.event;

import fintech.BigDecimalUtils;
import fintech.spain.unnax.callback.model.CallbackRequest;
import fintech.spain.unnax.callback.model.CreditCardPreAuthCallbackData;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static com.google.common.base.Strings.nullToEmpty;

@Getter
@Setter
public class CreditCardPreAuthorizeEvent extends CallbackEvent {

    private boolean success;
    private boolean cancelled;
    private String errorCode;
    private String errorMessage;


    private String pan;
    private String currency;
    private String transactionType;
    private String expirationDate;
    private String concept;
    private String cardHolder;
    private String orderCode;
    private String token;
    private LocalDateTime date;
    private Integer state;
    private BigDecimal amount;
    private String cardBrand;
    private String expireYear;
    private String expireMonth;
    private String cardType;
    private String cardBank;
    private Long bin;


    public String errorDetails() {
        return nullToEmpty(errorCode) + " " + nullToEmpty(errorMessage);
    }

    public CreditCardPreAuthorizeEvent(CallbackRequest request) {
        super(request.getResponseId());
        CreditCardPreAuthCallbackData data = request.getDataAsValue(CreditCardPreAuthCallbackData.class);
        this.pan = data.getPan();
        this.currency = data.getCurrency();
        this.transactionType = data.getTransactionType();
        this.expirationDate = data.getExpirationDate();
        this.concept = data.getConcept();
        this.cardHolder = data.getCardHolder();
        this.orderCode = data.getOrderCode();
        this.token = data.getToken();
        this.date = LocalDateTime.parse(data.getDate());
        this.amount = BigDecimalUtils.divideByHundred(data.getAmount());
        this.expireYear = data.getExpireYear();
        this.cardBrand = data.getCardBrand();
        this.expireMonth = data.getExpireMonth();
        this.cardType = data.getCardType();
        this.cardBank = data.getCardBank();
        this.errorMessage = data.getErrorMessage();
        this.bin = data.getBin();
        this.state = data.getState();
    }

}
