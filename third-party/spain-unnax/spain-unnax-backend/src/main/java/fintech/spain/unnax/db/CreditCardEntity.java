package fintech.spain.unnax.db;

import fintech.db.BaseEntity;
import fintech.spain.unnax.model.CreditCard;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.envers.AuditOverride;
import org.hibernate.envers.Audited;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Index;
import javax.persistence.Table;

@Getter
@Setter
@ToString(callSuper = true, of = {"clientNumber"})
@Entity
@Audited
@AuditOverride(forClass = BaseEntity.class)
@Table(name = "credit_card", schema = Entities.SCHEMA, indexes = {
    @Index(columnList = "clientNumber", name = "idx_credit_card_client_number"),
})
public class CreditCardEntity extends BaseEntity {

    @Column(nullable = false)
    private String clientNumber;

    @Column(nullable = false)
    private String callbackTransactionId;

    private boolean active;

    private String cardToken;
    private Long cardExpireYear;
    private Long cardExpireMonth;
    private String cardHolderName;
    private String cardBrand;
    private String cardBank;
    private String orderCode;
    private String errorDetails;
    private CreditCardStatus status;
    private Boolean automaticPaymentEnabled;
    private String pan;
    private Long bin;

    public CreditCard toValueObject() {
        CreditCard value = new CreditCard();
        value.setId(this.getId());
        value.setClientNumber(this.getClientNumber());
        value.setCallbackTransactionId(this.getCallbackTransactionId());
        value.setActive(this.isActive());
        value.setCardToken(this.getCardToken());
        value.setCardExpireMonth(this.getCardExpireMonth());
        value.setCardExpireYear(this.getCardExpireYear());
        value.setCardHolderName(this.getCardHolderName());
        value.setCardBrand(this.getCardBrand());
        value.setCardBank(this.getCardBank());
        value.setOrderCode(this.getOrderCode());
        value.setErrorDetails(this.getErrorDetails());
        value.setStatus(this.getStatus());
        value.setAutomaticPaymentEnabled(this.getAutomaticPaymentEnabled());
        value.setPan(this.getPan());
        value.setBin(this.getBin());
        return value;
    }

}
