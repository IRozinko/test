package fintech.dc.db;

import fintech.TimeMachine;
import fintech.db.BaseEntity;
import fintech.dc.model.Debt;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Index;
import javax.persistence.Table;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

@Getter
@Setter
@ToString(callSuper = true, of = {"loanId", "clientId", "agent"})
@Entity
@DynamicUpdate
@Table(name = "debt", schema = Entities.SCHEMA, indexes = {
    @Index(columnList = "clientId", name = "idx_debt_client_id"),
    @Index(columnList = "loanId", name = "idx_debt_loan_id"),
    @Index(columnList = "executeAt", name = "idx_debt_execute_at"),
})
public class DebtEntity extends BaseEntity {

    @Column(nullable = false)
    private Long clientId;

    @Column(nullable = false)
    private Long loanId;

    @Column(nullable = false, unique = true)
    private String loanNumber;

    @Column(nullable = false)
    private int dpd;

    @Column(nullable = false)
    private int maxDpd;

    @Column(nullable = false)
    private String agingBucket;

    @Column(nullable = false)
    private String portfolio;

    @Column(nullable = false)
    private String status;

    @Column(nullable = false)
    private String debtState;

    @Column(nullable = false)
    private String debtStatus;

    @Column
    private String debtSubStatus;

    @Column(nullable = false)
    private int priority;

    private String agent;

    @Column(columnDefinition = "TIMESTAMP WITH TIME ZONE")
    private LocalDateTime nextActionAt;

    private String nextAction;

    @Column(columnDefinition = "TIMESTAMP WITH TIME ZONE")
    private LocalDateTime lastActionAt;

    private String lastAction;

    @Column(nullable = false)
    private BigDecimal totalDue;

    @Column(nullable = false)
    private BigDecimal principalDue;

    @Column(nullable = false)
    private BigDecimal interestDue;

    @Column(nullable = false)
    private BigDecimal penaltyDue;

    @Column(nullable = false)
    private BigDecimal feeDue;

    @Column(nullable = false)
    private BigDecimal totalOutstanding;

    @Column(nullable = false)
    private BigDecimal principalOutstanding;

    @Column(nullable = false)
    private BigDecimal interestOutstanding;

    @Column(nullable = false)
    private BigDecimal penaltyOutstanding;

    @Column(nullable = false)
    private BigDecimal feeOutstanding;

    @Column(nullable = false)
    private BigDecimal totalPaid;

    @Column(nullable = false)
    private BigDecimal principalPaid;

    @Column(nullable = false)
    private BigDecimal interestPaid;

    @Column(nullable = false)
    private BigDecimal penaltyPaid;

    @Column(nullable = false)
    private BigDecimal feePaid;

    private LocalDate lastPaymentDate;

    private BigDecimal lastPaid;

    @Column(nullable = false)
    private LocalDate paymentDueDate;

    @Column(nullable = false)
    private LocalDate maturityDate;

    private LocalDate promiseDueDate;

    private BigDecimal promiseAmount;

    @Column(nullable = false)
    private String loanStatus;

    @Column(nullable = false)
    private String loanStatusDetail;

    @Column(columnDefinition = "TIMESTAMP WITH TIME ZONE")
    private LocalDateTime executeAt;

    @Column(columnDefinition = "TIMESTAMP WITH TIME ZONE")
    private LocalDateTime lastExecutedAt;

    private String lastExecutionResult;

    @Column(nullable = false)
    private boolean autoAssignmentRequired;

    @Column(nullable = false)
    private boolean batchAssignmentRequired;

    @Column(nullable = false)
    private Long periodCount = 0L;

    private String managingCompany;

    private String owningCompany;

    private String subStatus;

    public Debt toValueObject() {
        Debt vo = new Debt();
        vo.setId(this.id);
        vo.setClientId(this.clientId);
        vo.setLoanId(this.loanId);
        vo.setDpd(this.dpd);
        vo.setMaxDpd(this.maxDpd);
        vo.setAgingBucket(this.agingBucket);
        vo.setPortfolio(this.portfolio);
        vo.setStatus(this.status);
        vo.setPriority(this.priority);
        vo.setAgent(this.agent);
        vo.setNextActionAt(this.nextActionAt);
        vo.setNextAction(this.nextAction);
        vo.setLastActionAt(this.lastActionAt);
        vo.setLastAction(this.lastAction);
        vo.setTotalDue(this.totalDue);
        vo.setTotalOutstanding(this.totalOutstanding);
        vo.setTotalPaid(this.totalPaid);
        vo.setPromiseDueDate(this.promiseDueDate);
        vo.setPromiseDpd(this.promiseDueDate == null ? Integer.MIN_VALUE : (int) ChronoUnit.DAYS.between(this.promiseDueDate, TimeMachine.today()));
        vo.setLoanStatus(this.loanStatus);
        vo.setLoanStatusDetail(this.loanStatusDetail);
        vo.setManagingCompany(this.managingCompany);
        vo.setOwningCompany(this.owningCompany);
        vo.setSubStatus(this.subStatus);
        vo.setPeriodCount(this.periodCount);
        vo.setPaymentDueDate(this.paymentDueDate);
        vo.setDebtState(this.debtState);
        vo.setDebtStatus(this.debtStatus);
        vo.setDebtSubStatus(this.debtSubStatus);

        return vo;
    }

}
