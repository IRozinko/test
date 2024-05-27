package fintech.payments.db;

import fintech.db.BaseEntity;
import fintech.payments.model.Payment;
import fintech.payments.model.PaymentStatus;
import fintech.payments.model.PaymentStatusDetail;
import fintech.payments.model.PaymentType;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;
import org.hibernate.envers.AuditOverride;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

import static fintech.BigDecimalUtils.amount;


@Getter
@Setter
@ToString(callSuper = true, of = {"paymentType", "status", "amount", "pendingAmount", "valueDate"})
@Entity
@Audited
@AuditOverride(forClass = BaseEntity.class)
@Table(name = "payment", schema = Entities.SCHEMA, indexes = {
    @Index(columnList = "account_id", name = "idx_payment_account_id"),
    @Index(columnList = "bankOrderCode", name = "idx_payment_bank_order_code"),
    @Index(columnList = "key", name = "idx_payment_key", unique = true),
})
@Accessors(chain = true)
public class PaymentEntity extends BaseEntity {

    @ManyToOne(optional = false)
    @JoinColumn(name = "account_id")
    private InstitutionAccountEntity account;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PaymentType paymentType;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PaymentStatus status;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PaymentStatusDetail statusDetail;

    @Column(nullable = false)
    private BigDecimal amount = amount(0);

    @Column(nullable = false)
    private BigDecimal pendingAmount = amount(0);

    @Column(nullable = false, columnDefinition = "DATE")
    private LocalDate valueDate;

    private String counterpartyName;

    private String counterpartyAccount;

    private String counterpartyAddress;

    private LocalDateTime postedAt;

    private String details;

    private String reference;

    private String bankOrderCode;

    @Column(nullable = false)
    private String key;

    public Payment toValueObject() {
        Payment val = new Payment();
        val.setId(this.id);
        val.setAccountId(this.account.getId());
        val.setAmount(this.amount);
        val.setValueDate(this.valueDate);
        val.setPostedAt(this.postedAt);
        val.setDetails(this.details);
        val.setReference(this.reference);
        val.setBankOrderCode(this.bankOrderCode);
        val.setPaymentType(this.paymentType);
        val.setStatus(this.status);
        val.setStatusDetail(this.statusDetail);
        val.setPendingAmount(this.pendingAmount);
        val.setKey(this.key);
        val.setCounterpartyName(this.counterpartyName);
        val.setCounterpartyAccount(this.counterpartyAccount);
        val.setCounterpartyAddress(this.counterpartyAddress);
        return val;
    }

    public void open(PaymentStatusDetail statusDetail) {
        this.status = PaymentStatus.OPEN;
        this.statusDetail = statusDetail;
    }

    public void close(PaymentStatusDetail statusDetail) {
        this.status = PaymentStatus.CLOSED;
        this.statusDetail = statusDetail;
    }
}
