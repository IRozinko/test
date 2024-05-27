package fintech.payments.commands;

import fintech.payments.model.PaymentType;
import fintech.spain.unnax.event.TransferAutoProcessedEvent;
import lombok.Data;
import lombok.experimental.Accessors;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Accessors(chain = true)
public class AddPaymentCommand {

    private Long accountId;
    private PaymentType paymentType;
    private LocalDate valueDate;
    private LocalDateTime postedAt;
    private BigDecimal amount;
    private String details;
    private String reference;
    private String bankOrderCode;
    private String key;
    private String counterpartyName;
    private String counterpartyAccount;
    private String counterpartyAddress;

    private boolean requireManualStatus;

    public static AddPaymentCommand fromUnnaxEvent(long accountId, PaymentType type, TransferAutoProcessedEvent event) {
        return fromUnnaxEvent(accountId, type, event, "");
    }

    public static AddPaymentCommand fromUnnaxEvent(long accountId, PaymentType type, TransferAutoProcessedEvent event, String keySuffix) {
        return new AddPaymentCommand()
            .setAccountId(accountId)
            .setPaymentType(type)
            .setValueDate(event.getTimestamp().toLocalDate())
            .setPostedAt(event.getTimestamp())
            .setAmount(event.getAmount())
            .setKey(event.getBankOrderId() + keySuffix)
            .setBankOrderCode(event.getBankOrderId())
            .setDetails(event.getOrderId());
    }
}
