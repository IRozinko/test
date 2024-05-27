package fintech.bo.components.payments;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class PaymentSummary {

    private Long id;
    private String paymentType;
    private String status;
    private String statusDetail;
    private BigDecimal amount;
    private BigDecimal pendingAmount;
    private LocalDate valueDate;
    private LocalDateTime postedAt;
    private String details;
    private String reference;
    private String key;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String createdBy;
    private String updatedBy;
    private Long institutionId;
    private Long institutionAccountId;
    private String institutionName;
    private String institutionAccountNumber;
    private String counterpartyName;
    private String counterpartyAccount;
    private String counterpartyAddress;
}
