package fintech.payxpert.db;

import fintech.db.BaseEntity;
import fintech.payxpert.PayxpertRebill;
import fintech.payxpert.RebillStatus;
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
@ToString(callSuper = true, of = {"clientId", "loanId", "invoiceId", "errorCode"})
@Entity
@Audited
@AuditOverride(forClass = BaseEntity.class)
@Table(name = "rebill", schema = Entities.SCHEMA, indexes = {
    @Index(columnList = "clientId", name = "idx_rebill_client_id"),
})
public class PayxpertRebillEntity extends BaseEntity {

    @Column(nullable = false)
    private Long clientId;

    private Long loanId;

    private Long invoiceId;

    @ManyToOne(optional = false)
    @JoinColumn(name = "credit_card_id")
    private PayxpertCreditCardEntity creditCard;

    @Column(nullable = false)
    private BigDecimal amount;

    @Column(nullable = false)
    private String currency;

    private String errorCode;

    private String errorMessage;

    private String responseTransactionId;

    private String responseStatementDescriptor;

    @NotNull
    @Enumerated(EnumType.STRING)
    private RebillStatus status;

    private Long paymentId;

    @Column(columnDefinition = "TIMESTAMP WITH TIME ZONE")
    private LocalDateTime paymentCreatedAt;

    public PayxpertRebill toValueObject() {
        PayxpertRebill vo = new PayxpertRebill();
        vo.setId(this.id);
        vo.setCreatedAt(this.createdAt);
        vo.setClientId(this.clientId);
        vo.setAmount(this.amount);
        vo.setCurrency(this.currency);
        vo.setLoanId(this.loanId);
        vo.setInvoiceId(this.invoiceId);
        vo.setStatus(this.status);
        vo.setErrorCode(this.errorCode);
        vo.setErrorMessage(this.errorMessage);
        vo.setResponseTransactionId(this.responseTransactionId);
        return vo;
    }
}
