package fintech.spain.unnax.event;

import fintech.spain.unnax.callback.model.CallbackRequest;
import fintech.spain.unnax.callback.model.PaymentWithCardCallbackData;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@ToString
public class PaymentWithCardEvent extends CallbackEvent {

    private String pan;
    private String bin;
    private String currency;
    private String transactionType;
    private String expirationDate;
    private Integer expireMonth;
    private Integer expireYear;
    private String cardHolder;
    private String cardBrand;
    private String cardType;
    private String cardCountry;
    private String cardBank;
    private String orderCode;
    private String token;
    private LocalDateTime date;
    private BigDecimal amount;
    private String concept;
    private Integer state;

    public PaymentWithCardEvent(CallbackRequest request) {
        super(request.getResponseId());
        PaymentWithCardCallbackData data = request.getDataAsValue(PaymentWithCardCallbackData.class);
        if (data != null) {
            this.pan = data.getPan();
            this.bin = data.getBin();
            this.currency = data.getCurrency();
            this.transactionType = data.getTransactionType();
            this.expirationDate = data.getExpirationDate();
            if (data.getExpireMonth() != null) {
                this.expireMonth = Integer.parseInt(data.getExpireMonth());
            }
            if (data.getExpireYear() != null) {
                this.expireYear = Integer.parseInt(data.getExpireYear());
            }
            this.cardHolder = data.getCardHolder();
            this.cardBrand = data.getCardBrand();
            this.cardType = data.getCardType();
            this.cardCountry = data.getCardCountry();
            this.cardBank = data.getCardBank();
            this.orderCode = data.getOrderCode();
            this.token = data.getToken();
            if (data.getDate() != null) {
                this.date = LocalDateTime.parse(data.getDate());
            }
            this.amount = data.getAmount() != null ? BigDecimal.valueOf(data.getAmount(), 2) : null;
            this.concept = data.getConcept();
            this.state = data.getState();
        }
    }

    public PaymentWithCardEvent(String responseId) {
        super(responseId);
    }

}
