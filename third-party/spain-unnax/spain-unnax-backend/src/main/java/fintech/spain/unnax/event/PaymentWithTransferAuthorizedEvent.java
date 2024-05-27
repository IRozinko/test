package fintech.spain.unnax.event;

import fintech.spain.unnax.callback.model.CallbackRequest;
import fintech.spain.unnax.callback.model.PaymentWithTransferAuthorizedData;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
public class PaymentWithTransferAuthorizedEvent extends CallbackEvent {

    private String orderCode;
    private String bankOrderCode;
    private BigDecimal amount;
    private String currency;
    private String customerCode;
    private String customerNames;
    private String service;
    private String status;
    private boolean success;
    private String errorMessages;
    private LocalDateTime date;

    public PaymentWithTransferAuthorizedEvent(CallbackRequest request) {
        super(request.getResponseId());
        this.date = request.getDate();

        PaymentWithTransferAuthorizedData data = request.getDataAsValue(PaymentWithTransferAuthorizedData.class);
        if (data != null) {
            this.orderCode = data.getOrderCode();
            this.bankOrderCode = data.getBankOrderCode();
            this.amount = data.getAmount() != null ? BigDecimal.valueOf(data.getAmount(), 2) : null;
            this.currency = data.getCurrency();
            this.customerCode = data.getCustomerCode();
            this.customerNames = data.getCustomerNames();
            this.service = data.getService();
            this.status = data.getStatus();
            this.success = Boolean.TRUE.equals(data.getSuccess());
            this.errorMessages = data.getErrorMessages();
        }
    }

}
