package fintech.payxpert.db;

import fintech.db.BaseEntity;
import fintech.payxpert.PayxpertCreditCard;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.envers.AuditOverride;
import org.hibernate.envers.Audited;

import javax.persistence.*;

@Getter
@Setter
@ToString(callSuper = true, of = {"clientId"})
@Entity
@Audited
@AuditOverride(forClass = BaseEntity.class)
@Table(name = "credit_card", schema = Entities.SCHEMA, indexes = {
    @Index(columnList = "clientId", name = "idx_credit_card_client_id"),
})
public class PayxpertCreditCardEntity extends BaseEntity {

    @Column(nullable = false)
    private Long clientId;

    @ManyToOne(optional = false)
    @JoinColumn(name = "payment_request_id")
    private PayxpertPaymentRequestEntity request;

    @Column(nullable = false)
    private String callbackTransactionId;

    private boolean active;

    private boolean recurringPaymentsEnabled;

    private String cardNumber;
    private Long cardExpireYear;
    private Long cardExpireMonth;
    private String cardHolderName;
    private String cardBrand;
    private Boolean cardIs3DSecure;

    public PayxpertCreditCard toValueObject() {
        PayxpertCreditCard vo = new PayxpertCreditCard();
        vo.setId(this.id);
        vo.setClientId(this.clientId);
        vo.setPaymentRequestId(this.request.getId());
        vo.setActive(this.active);
        vo.setRecurringPaymentsEnabled(this.recurringPaymentsEnabled);
        vo.setCallbackTransactionId(this.callbackTransactionId);
        vo.setCardNumber(this.cardNumber);
        vo.setCardExpireYear(this.cardExpireYear);
        vo.setCardExpireMonth(this.cardExpireMonth);
        vo.setCardHolderName(this.cardHolderName);
        vo.setCardBrand(this.cardBrand);
        vo.setCardIs3DSecure(this.cardIs3DSecure);
        return vo;
    }
}
