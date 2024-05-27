package fintech.dc.db;

import fintech.db.BaseEntity;
import fintech.dc.model.ActionStatus;
import fintech.dc.model.DebtAction;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@ToString(callSuper = true, of = {"loanId", "clientId", "actionName"})
@Entity
@Table(name = "action", schema = Entities.SCHEMA, indexes = {
    @Index(columnList = "clientId", name = "idx_debt_action_client_id"),
    @Index(columnList = "loanId", name = "idx_debt_action_loan_id"),
    @Index(columnList = "debt_id", name = "idx_debt_action_debt_id"),
})
public class DebtActionEntity extends BaseEntity {

    @ManyToOne(optional = false)
    @JoinColumn(name = "debt_id")
    private DebtEntity debt;

    @Column(nullable = false)
    private Long clientId;

    @Column(nullable = false)
    private Long loanId;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private ActionStatus actionStatus;

    private String resolution;

    @Column(nullable = false)
    private String actionName;

    @Column(nullable = false)
    private String debtStatus;

    @Column(nullable = false)
    private String debtStatusBefore;

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

    @Column(nullable = false)
    private String portfolio;

    @Column(nullable = false)
    private String portfolioBefore;

    @Column(nullable = false)
    private int priority;

    private String agent;

    private String assignedToAgent;

    @Column(nullable = false)
    private int dpd;

    @Column(nullable = false)
    private int maxDpd;

    @Column(nullable = false)
    private String agingBucket;

    private String comments;

    @Column(columnDefinition = "TIMESTAMP WITH TIME ZONE")
    private LocalDateTime nextActionAt;

    private String nextAction;

    private LocalDate promiseDueDate;

    private BigDecimal promiseAmount;

    private String managingCompanyBefore;

    private String owningCompanyBefore;

    private String managingCompanyAfter;

    private String owningCompanyAfter;

    public DebtAction toValueObject() {
        DebtAction vo = new DebtAction();
        vo.setId(this.id);
        return vo;
    }
}
