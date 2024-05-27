package fintech.spain.alfa.product.db;

import fintech.db.BaseEntity;
import fintech.lending.core.loan.db.LoanEntity;
import fintech.spain.alfa.product.lending.LoanRescheduling;
import fintech.spain.alfa.product.lending.LoanReschedulingStatus;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;
import org.hibernate.envers.AuditOverride;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
@ToString(callSuper = true)
@Entity
@Audited
@AuditOverride(forClass = BaseEntity.class)
@Accessors(chain = true)
@Table(name = "loan_rescheduling", indexes = {
    @Index(columnList = "loan_id", name = "idx_loan_rescheduling_loan_id")}, schema = Entities.SCHEMA)
public class LoanReschedulingEntity extends BaseEntity {

    @ManyToOne(optional = false)
    @JoinColumn(name = "loan_id")
    private LoanEntity loan;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private LoanReschedulingStatus status;

    private int numberOfPayments;

    private int repaymentDueDays;

    private int gracePeriodDays;

    @Column(nullable = false)
    private BigDecimal installmentAmount;

    @Column(columnDefinition = "DATE", nullable = false)
    private LocalDate rescheduleDate;

    @Column(columnDefinition = "DATE", nullable = false)
    private LocalDate expireDate;

    public LoanRescheduling toValueObject() {
        LoanRescheduling item = new LoanRescheduling();
        item.setId(this.id);
        item.setLoanId(this.loan.getId());
        item.setStatus(this.status);
        item.setNumberOfPayments(this.numberOfPayments);
        item.setRepaymentDueDays(this.repaymentDueDays);
        item.setGracePeriodDays(this.gracePeriodDays);
        item.setRescheduleDate(this.rescheduleDate);
        item.setExpireDate(this.expireDate);
        return item;
    }
}
