package fintech.spain.unnax.event;

import fintech.BigDecimalUtils;
import fintech.TimeMachine;
import fintech.spain.unnax.callback.model.CallbackRequest;
import fintech.spain.unnax.callback.model.TransferAutoProcessedCallbackData;
import fintech.spain.unnax.transfer.model.TransferAutoDetails;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static com.google.common.base.Strings.nullToEmpty;

@Getter
@Setter
public class TransferAutoProcessedEvent extends CallbackEvent {

    private boolean success;
    private String product;
    private String orderId;
    private String bankOrderId;
    private LocalDateTime timestamp;
    private BigDecimal amount;
    private String currency;
    private String customerId;
    private String customerAccount;
    private String sourceAccount;
    private BigDecimal srcAccountBalance;
    private boolean cancelled;
    private Long sourceBankId;
    private String errorCode;
    private String errorMessage;

    public String errorDetails() {
        return nullToEmpty(errorCode) + " " + nullToEmpty(errorMessage);
    }

    public TransferAutoProcessedEvent(CallbackRequest request) {
        super(request.getResponseId());
        TransferAutoProcessedCallbackData data = request.getDataAsValue(TransferAutoProcessedCallbackData.class);
        this.success = data.isSuccess();
        this.product = data.getProduct();
        this.orderId = data.getOrderId();
        this.bankOrderId = data.getBankOrderId();
        this.timestamp = LocalDateTime.of(data.getDate(), data.getTime());
        this.amount = BigDecimalUtils.divideByHundred(data.getAmount());
        this.currency = data.getCurrency();
        this.customerId = data.getCustomerId();
        this.customerAccount = data.getCustomerAccount();
        this.sourceAccount = data.getSourceAccount();
        this.srcAccountBalance = BigDecimalUtils.divideByHundred(data.getSrcAccountBalance());
        this.cancelled = data.isCancelled();
        this.sourceBankId = data.getSourceBankId();
        this.errorCode = data.getErrorCode();
        this.errorMessage = data.getErrorMessage();
    }

    private TransferAutoProcessedEvent(TransferAutoDetails details) {
        super("Sync");
        this.orderId = details.getOrderCode();
        this.bankOrderId = details.getBankOrderCode();
        this.timestamp = TimeMachine.now();
        this.amount = BigDecimalUtils.divideByHundred(details.getAmount());
        this.currency = details.getCurrency();
        this.customerId = details.getCustomerCode();
        this.customerAccount = details.getDestinationAccount();
        this.sourceAccount = details.getSourceAccount();
    }

    public static TransferAutoProcessedEvent success(TransferAutoDetails details) {
        TransferAutoProcessedEvent event = new TransferAutoProcessedEvent(details);
        event.setSuccess(true);
        return event;
    }

    public static TransferAutoProcessedEvent failed(TransferAutoDetails details) {
        TransferAutoProcessedEvent event = new TransferAutoProcessedEvent(details);
        event.setSuccess(false);
        return event;
    }

    public static TransferAutoProcessedEvent canceled(TransferAutoDetails details) {
        TransferAutoProcessedEvent event = new TransferAutoProcessedEvent(details);
        event.setCancelled(true);
        return event;
    }
}
