package fintech.payments.model;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class Payment {

    private Long id;
    private Long accountId;
    private PaymentStatus status;
    private PaymentStatusDetail statusDetail;
    private PaymentType paymentType;
    private LocalDate valueDate;
    private LocalDateTime postedAt;
    private BigDecimal amount;
    private BigDecimal pendingAmount;
    private String details;
    private String reference;
    private String bankOrderCode;
    private String key;
    private String counterpartyName;
    private String counterpartyAccount;
    private String counterpartyAddress;

}
