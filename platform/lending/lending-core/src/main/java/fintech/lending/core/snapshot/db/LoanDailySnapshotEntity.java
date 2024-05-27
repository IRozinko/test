package fintech.lending.core.snapshot.db;

import fintech.db.BaseEntity;
import fintech.lending.core.db.Entities;
import fintech.lending.core.loan.LoanStatus;
import fintech.lending.core.loan.LoanStatusDetail;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.OptimisticLockType;
import org.hibernate.annotations.OptimisticLocking;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
@ToString(callSuper = true, of = {"clientId", "loanId"})
@Entity
@Table(name = "loan_daily_snapshot", schema = Entities.SCHEMA)
@OptimisticLocking(type = OptimisticLockType.NONE)
public class LoanDailySnapshotEntity extends BaseEntity {

    @Column(nullable = false, columnDefinition = "DATE")
    private LocalDate effectiveFrom;

    @Column(nullable = false, columnDefinition = "DATE")
    private LocalDate effectiveTo;

    @Column(nullable = false)
    private boolean latest;

    // loan fields

    @Column(nullable = false)
    private Long loanId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private LoanStatus status;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private LoanStatusDetail statusDetail;

    @Column(nullable = false)
    private Long productId;

    @Column(nullable = false)
    private Long clientId;

    @Column(nullable = false)
    private Long loanApplicationId;

    @Column(nullable = false, columnDefinition = "DATE")
    private LocalDate issueDate;

    @Column(columnDefinition = "DATE")
    private LocalDate closeDate;

    @Column(columnDefinition = "DATE")
    private LocalDate maturityDate;

    @Column(columnDefinition = "DATE")
    private LocalDate paymentDueDate;

    @Column(nullable = false, name = "loan_number")
    private String number;

    @Column(nullable = false)
    private BigDecimal creditLimit;

    @Column(nullable = false)
    private Long loansPaid;

    @Column(nullable = false, precision = 19, scale = 4)
    private BigDecimal principalDisbursed;

    @Column(nullable = false, precision = 19, scale = 4)
    private BigDecimal principalPaid;

    @Column(nullable = false, precision = 19, scale = 4)
    private BigDecimal principalWrittenOff;

    @Column(nullable = false, precision = 19, scale = 4)
    private BigDecimal principalDue;

    @Column(nullable = false, precision = 19, scale = 4)
    private BigDecimal principalOutstanding;

    @Column(nullable = false, precision = 19, scale = 4)
    private BigDecimal interestApplied;

    @Column(nullable = false, precision = 19, scale = 4)
    private BigDecimal interestPaid;

    @Column(nullable = false, precision = 19, scale = 4)
    private BigDecimal interestWrittenOff;

    @Column(nullable = false, precision = 19, scale = 4)
    private BigDecimal interestDue;

    @Column(nullable = false, precision = 19, scale = 4)
    private BigDecimal interestOutstanding;

    @Column(nullable = false, precision = 19, scale = 4)
    private BigDecimal penaltyApplied;

    @Column(nullable = false, precision = 19, scale = 4)
    private BigDecimal penaltyPaid;

    @Column(nullable = false, precision = 19, scale = 4)
    private BigDecimal penaltyWrittenOff;

    @Column(nullable = false, precision = 19, scale = 4)
    private BigDecimal penaltyDue;

    @Column(nullable = false, precision = 19, scale = 4)
    private BigDecimal penaltyOutstanding;

    @Column(nullable = false, precision = 19, scale = 4)
    private BigDecimal feeApplied;

    @Column(nullable = false, precision = 19, scale = 4)
    private BigDecimal feePaid;

    @Column(nullable = false, precision = 19, scale = 4)
    private BigDecimal feeWrittenOff;

    @Column(nullable = false, precision = 19, scale = 4)
    private BigDecimal feeDue;

    @Column(nullable = false, precision = 19, scale = 4)
    private BigDecimal feeOutstanding;

    @Column(nullable = false, precision = 19, scale = 4)
    private BigDecimal totalDue;

    @Column(nullable = false, precision = 19, scale = 4)
    private BigDecimal totalOutstanding;

    @Column(nullable = false, precision = 19, scale = 4)
    private BigDecimal cashIn;

    @Column(nullable = false, precision = 19, scale = 4)
    private BigDecimal cashOut;

    @Column(nullable = false)
    private int invoicePaymentDay;

    private int overdueDays = 0;

    private int maxOverdueDays = 0;

}
