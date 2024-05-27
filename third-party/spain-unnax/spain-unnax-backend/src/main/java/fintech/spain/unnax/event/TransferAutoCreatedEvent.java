package fintech.spain.unnax.event;

import fintech.BigDecimalUtils;
import fintech.spain.unnax.callback.model.CallbackRequest;
import fintech.spain.unnax.callback.model.TransferAutoCreatedCallbackData;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
public class TransferAutoCreatedEvent extends CallbackEvent {

    private String customerAccount;
    private String sourceAccount;
    private String customerId;
    private String orderId;
    private String currency;
    private LocalDateTime timestamp;
    private BigDecimal amount;

    public TransferAutoCreatedEvent(CallbackRequest request) {
        super(request.getResponseId());
        TransferAutoCreatedCallbackData data = request.getDataAsValue(TransferAutoCreatedCallbackData.class);
        this.customerAccount = data.getCustomerAccount();
        this.sourceAccount = data.getSourceAccount();
        this.customerId = data.getCustomerId();
        this.orderId = data.getOrderId();
        this.currency = data.getCurrency();
        this.timestamp = LocalDateTime.of(data.getDate(), data.getTime());
        this.amount = BigDecimalUtils.divideByHundred(data.getAmount());
    }

    public TransferAutoCreatedEvent(String responseId, String orderId) {
        super(responseId);
        this.orderId = orderId;
    }
}
