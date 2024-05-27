package fintech.payxpert.db;

import com.payxpert.connect2pay.constants.PaymentType;
import com.payxpert.connect2pay.constants.TransactionOperation;
import fintech.db.BaseEntity;
import fintech.payxpert.PaymentRequestStatus;
import fintech.payxpert.PayxpertPaymentRequest;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.envers.AuditOverride;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@ToString(callSuper = true, of = {"clientId", "orderId", "status", "errorCode"})
@Entity
@Audited
@AuditOverride(forClass = BaseEntity.class)
@Table(name = "payment_request", schema = Entities.SCHEMA, indexes = {
    @Index(columnList = "clientId", name = "idx_payment_request_client_id"),
})
public class PayxpertPaymentRequestEntity extends BaseEntity {

    @Column(nullable = false)
    private Long clientId;

    private Long loanId;

    private Long invoiceId;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private PaymentType paymentType;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private TransactionOperation operation;

    @Column(nullable = false)
    private BigDecimal amount;

    @Column(nullable = false)
    private String currency;

    @Column(nullable = false, unique = true)
    private String orderId;

    @NotNull
    @Enumerated(EnumType.STRING)
    private PaymentRequestStatus status;

    private String statusDetail;

    private boolean saveCreditCard;

    private boolean enableRecurringPayments;

    @Column(nullable = false)
    private String ctrlCallbackUrl;

    private String ctrlRedirectUrl;

    @Column(nullable = false)
    private String customerRedirectUrl;

    private String merchantToken;

    private String customerToken;

    private String callbackTransactionId;

    private String errorCode;

    private String errorMessage;

    @Column(columnDefinition = "TIMESTAMP WITH TIME ZONE")
    private LocalDateTime callbackReceivedAt;

    @Column(columnDefinition = "TIMESTAMP WITH TIME ZONE")
    private LocalDateTime lastStatusCheckAt;

    private Long statusCheckAttempts;

    private String cardNumber;
    private Long cardExpireYear;
    private Long cardExpireMonth;
    private String cardHolderName;
    private String cardBrand;
    private Boolean cardIs3DSecure;

    public PayxpertPaymentRequest toValueObject() {
        PayxpertPaymentRequest vo = new PayxpertPaymentRequest();
        vo.setId(this.id);
        vo.setClientId(this.clientId);
        vo.setAmount(this.amount);
        vo.setStatus(this.status);
        vo.setStatusDetail(this.statusDetail);
        vo.setCustomerRedirectUrl(this.customerRedirectUrl);
        vo.setCallbackTransactionId(this.callbackTransactionId);
        vo.setCurrency(this.currency);
        vo.setMerchantToken(this.merchantToken);
        vo.setCustomerToken(this.customerToken);
        vo.setOrderId(this.orderId);
        vo.setCardBrand(this.cardBrand);
        vo.setCardExpireMonth(this.cardExpireMonth);
        vo.setCardExpireYear(this.cardExpireYear);
        vo.setCardHolderName(this.cardHolderName);
        vo.setCardIs3DSecure(this.cardIs3DSecure);
        vo.setCardNumber(this.cardNumber);
        vo.setStatusCheckAttempts(this.statusCheckAttempts);
        vo.setSaveCreditCard(this.saveCreditCard);
        return vo;
    }
}
