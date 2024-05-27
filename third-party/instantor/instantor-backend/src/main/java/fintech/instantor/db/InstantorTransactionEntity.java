package fintech.instantor.db;

import fintech.db.BaseEntity;
import fintech.instantor.model.InstantorTransaction;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.envers.AuditOverride;
import org.hibernate.envers.Audited;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import java.math.BigDecimal;
import java.time.LocalDate;

import static org.hibernate.envers.RelationTargetAuditMode.NOT_AUDITED;

@Getter
@Setter
@Entity
@Table(name = "transaction", schema = Entities.SCHEMA)
@Audited
@AuditOverride(forClass = BaseEntity.class)
@DynamicUpdate
public class InstantorTransactionEntity extends BaseEntity {

    @Audited(targetAuditMode = NOT_AUDITED)
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "response_id")
    private InstantorResponseEntity response;

    private Long clientId;

    @Column(nullable = false)
    private String accountNumber;

    @Column(nullable = false)
    private String accountHolderName;

    @Column(nullable = false)
    private String currency;

    @Column(nullable = false, columnDefinition = "DATE")
    private LocalDate date;

    @Column(nullable = false)
    private BigDecimal amount;

    private BigDecimal balance;

    private String description;

    private String category;

    private String nordigenCategory;

    @Audited(targetAuditMode = NOT_AUDITED)
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "account_id")
    private InstantorAccountEntity account;

    public InstantorTransaction toValueObject() {
        InstantorTransaction vo = new InstantorTransaction();
        vo.setResponseId(response.getId());
        vo.setClientId(clientId);
        vo.setAccountNumber(accountNumber);
        vo.setAccountHolderName(accountHolderName);
        vo.setCurrency(currency);
        vo.setDate(date);
        vo.setAmount(amount);
        vo.setBalance(balance);
        vo.setDescription(description);
        vo.setCategory(category);
        vo.setNordigenCategory(nordigenCategory);
        return vo;
    }
}
