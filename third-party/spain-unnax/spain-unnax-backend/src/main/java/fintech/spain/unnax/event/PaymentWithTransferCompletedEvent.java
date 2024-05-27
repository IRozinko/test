package fintech.spain.unnax.event;

import fintech.spain.unnax.callback.model.CallbackRequest;
import fintech.spain.unnax.callback.model.PaymentWithTransferCompletedData;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
public class PaymentWithTransferCompletedEvent extends CallbackEvent {

    private String customerCode;
    private String orderCode;
    private String bankOrderCode;
    private BigDecimal amount;
    private LocalDateTime date;
    private Boolean success;
    private String signature;
    private Boolean result;
    private String accountNumber;
    private String status;
    private String service;

    public PaymentWithTransferCompletedEvent(CallbackRequest request) {
        super(request.getResponseId());
        PaymentWithTransferCompletedData data = request.getDataAsValue(PaymentWithTransferCompletedData.class);
        if (data != null) {
            this.customerCode = data.getCustomerCode();
            this.orderCode = data.getOrderCode();
            this.bankOrderCode = data.getBankOrderCode();
            this.amount = data.getAmount() == null ? null : BigDecimal.valueOf(data.getAmount(), 2);
            this.date = data.getDate() != null ? LocalDateTime.parse(data.getDate()) : null;
            this.success = data.getSuccess();
            this.signature = data.getSignature();
            this.result = data.getResult();
            this.accountNumber = data.getAccountNumber();
            this.status = data.getStatus();
            this.service = data.getService();
        }
    }

}
