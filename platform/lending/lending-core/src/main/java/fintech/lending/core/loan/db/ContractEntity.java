package fintech.lending.core.loan.db;

import fintech.db.BaseEntity;
import fintech.lending.core.PeriodUnit;
import fintech.lending.core.db.Entities;
import fintech.lending.core.loan.Contract;
import fintech.transactions.TransactionType;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.envers.AuditOverride;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.time.LocalDate;

@Getter
@Setter
@ToString(callSuper = true)
@Entity
@Audited
@AuditOverride(forClass = BaseEntity.class)
@Table(name = "loan_contract", schema = Entities.SCHEMA)
public class ContractEntity extends BaseEntity {

    @Column(nullable = false)
    private Long productId;

    @Column(nullable = false)
    private Long loanId;

    @Column(nullable = false)
    private Long clientId;

    private Long applicationId;

    @Column(nullable = false)
    private LocalDate contractDate;

    @Column(nullable = false)
    private LocalDate effectiveDate;

    @Column(nullable = false)
    private LocalDate maturityDate;

    @Column(nullable = false)
    private boolean current;

    @Column(nullable = false)
    private Long periodCount = 0L;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private PeriodUnit periodUnit = PeriodUnit.NA;

    @Column(nullable = false)
    private Long numberOfInstallments = 0L;

    @Column(nullable = false)
    private boolean closeLoanOnPaid;

    @Column(nullable = false)
    private int baseOverdueDays;

    @Column(nullable = true)
    private Long previousContractId;

    @Column(nullable = true)
    private Long sourceTransactionId;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private TransactionType sourceTransactionType;

    public Contract toValueObject() {
        Contract vo = new Contract();
        vo.setId(this.id);
        vo.setProductId(this.productId);
        vo.setLoanId(this.loanId);
        vo.setClientId(this.clientId);
        vo.setApplicationId(this.applicationId);
        vo.setContractDate(this.contractDate);
        vo.setActiveFrom(this.effectiveDate);
        vo.setEffectiveDate(this.effectiveDate);
        vo.setMaturityDate(this.maturityDate);
        vo.setCurrent(this.current);
        vo.setPeriodCount(this.periodCount);
        vo.setPeriodUnit(this.periodUnit);
        vo.setNumberOfInstallments(this.numberOfInstallments);
        vo.setCloseLoanOnPaid(this.closeLoanOnPaid);
        vo.setBaseOverdueDays(this.baseOverdueDays);
        vo.setPreviousContractId(this.previousContractId);
        vo.setSourceTransactionId(this.sourceTransactionId);
        vo.setSourceTransactionType(this.sourceTransactionType);
        return vo;
    }

}
