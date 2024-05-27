package fintech.lending.core.application.db;

import fintech.db.BaseEntity;
import fintech.lending.core.PeriodUnit;
import fintech.lending.core.application.LoanApplication;
import fintech.lending.core.application.LoanApplicationSourceType;
import fintech.lending.core.application.LoanApplicationStatus;
import fintech.lending.core.application.LoanApplicationType;
import fintech.lending.core.db.Entities;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.OptimisticLock;
import org.hibernate.envers.AuditOverride;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import static fintech.BigDecimalUtils.amount;

@Getter
@Setter
@ToString(callSuper = true, of = {"clientId", "status", "statusDetail", "closeReason"})
@Entity
@Audited
@AuditOverride(forClass = BaseEntity.class)
@Table(name = "loan_application", schema = Entities.SCHEMA, indexes = {
    @Index(columnList = "clientId", name = "idx_loan_application_client_id"),
})
public class LoanApplicationEntity extends BaseEntity {

    @Enumerated(EnumType.STRING)
    private LoanApplicationType type;

    @Column(nullable = false)
    private Long productId;

    @Column(nullable = false)
    private Long clientId;

    private String ipAddress;

    private String ipCountry;

    private String referer;

    @Column(nullable = false, unique = true, name = "application_number")
    private String number;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private LoanApplicationStatus status;

    @Column(nullable = false)
    private String statusDetail;

    @Column(nullable = false)
    private LocalDateTime submittedAt;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private PeriodUnit requestedPeriodUnit = PeriodUnit.NA;

    @Column(nullable = false)
    private Long requestedPeriodCount = 0L;

    @Column(nullable = false)
    private Long requestedInstallments = 0L;

    @Column(nullable = false)
    private BigDecimal requestedPrincipal = amount(0);

    @Column(nullable = false)
    private BigDecimal requestedInterestDiscountPercent = amount(0);

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private PeriodUnit offeredPeriodUnit = PeriodUnit.NA;

    @Column(nullable = false)
    private Long offeredPeriodCount = 0L;

    @Column(nullable = false)
    private Long offeredInstallments = 0L;

    @Column(nullable = false)
    private BigDecimal offeredPrincipal = amount(0);

    @Column(nullable = false)
    private BigDecimal offeredInterest = amount(0);

    @Column(nullable = false)
    private BigDecimal offeredInterestDiscountPercent = amount(0);

    @Column(nullable = false)
    private BigDecimal offeredInterestDiscountAmount = amount(0);

    @Column(columnDefinition = "DATE")
    private LocalDate offerDate;

    @Column(columnDefinition = "DATE")
    private LocalDate closeDate;

    private String closeReason;

    private Long loanId;

    private Long loansPaid = 0L;

    private Long workflowId;

    @Column(nullable = false)
    private BigDecimal creditLimit = amount(0);

    private int invoicePaymentDay;

    private String shortApproveCode;

    private String longApproveCode;

    @Column(nullable = false)
    private BigDecimal score = amount(0);

    @Deprecated
    private String scoreBucket;

    private String scoreSource;

    @Column(nullable = false)
    private BigDecimal nominalApr = amount(0);

    @Column(nullable = false)
    private BigDecimal effectiveApr = amount(0);

    @Column(nullable = false)
    private String uuid;

    private String params;

    private LocalDateTime offerApprovedAt;

    private String offerApprovedBy;

    private String offerApprovedFromIpAddress;

    @Enumerated(EnumType.STRING)
    private LoanApplicationSourceType sourceType;

    private String sourceName;

    private Long discountId;

    private Long promoCodeId;

    private Long interestStrategyId;

    private Long penaltyStrategyId;

    private Long extensionStrategyId;

    private Long feeStrategyId;

    @OptimisticLock(excluded = true)
    @ElementCollection
    @MapKeyColumn(name = "key")
    @Column(name = "value")
    @CollectionTable(name = "loan_application_attribute", joinColumns = @JoinColumn(name = "loan_application_id"), schema = Entities.SCHEMA)
    private Map<String, String> attributes = new HashMap<>();

    public LoanApplication toValueObject() {
        LoanApplication val = new LoanApplication();
        val.setId(this.id);
        val.setType(this.type);
        val.setNumber(this.number);
        val.setIpAddress(this.ipAddress);
        val.setIpCountry(this.ipCountry);
        val.setReferer(this.referer);
        val.setClientId(this.clientId);
        val.setLoanId(this.loanId);
        val.setProductId(this.productId);
        val.setStatus(this.status);
        val.setStatusDetail(this.statusDetail);

        val.setInterestStrategyId(this.interestStrategyId);
        val.setPenaltyStrategyId(this.penaltyStrategyId);
        val.setExtensionStrategyId(this.extensionStrategyId);
        val.setFeeStrategyId(this.feeStrategyId);

        val.setRequestedPeriodUnit(this.requestedPeriodUnit);
        val.setRequestedPeriodCount(this.requestedPeriodCount);
        val.setRequestedInstallments(this.requestedInstallments);
        val.setRequestedPrincipal(this.requestedPrincipal);
        val.setRequestedInterestDiscountPercent(this.requestedInterestDiscountPercent);
        val.setSubmittedAt(this.submittedAt);
        val.setInvoicePaymentDay(this.invoicePaymentDay);

        val.setOfferedPeriodUnit(this.offeredPeriodUnit);
        val.setOfferedPeriodCount(this.offeredPeriodCount);
        val.setOfferedInstallments(this.offeredInstallments);
        val.setOfferedInterest(this.offeredInterest);
        val.setOfferedInterestDiscountPercent(this.offeredInterestDiscountPercent);
        val.setOfferedInterestDiscountAmount(this.offeredInterestDiscountAmount);
        val.setOfferedPrincipal(this.offeredPrincipal);
        val.setOfferDate(this.offerDate);

        val.setCloseDate(this.closeDate);
        val.setCloseReason(this.closeReason);
        val.setLoansPaid(this.loansPaid);

        val.setCreatedAt(this.getCreatedAt());
        val.setCreatedBy(this.getCreatedBy());
        val.setUpdatedBy(this.getUpdatedBy());
        val.setUpdatedAt(this.getUpdatedAt());

        val.setWorkflowId(this.workflowId);

        val.setCreditLimit(this.creditLimit);

        val.setShortApproveCode(this.shortApproveCode);
        val.setLongApproveCode(this.longApproveCode);

        val.setScore(this.score);
        val.setScoreBucket(this.scoreBucket);
        val.setScoreSource(this.scoreSource);

        val.setNominalApr(this.nominalApr);
        val.setEffectiveApr(this.effectiveApr);

        val.setUuid(this.uuid);

        val.setSourceType(this.sourceType);
        val.setSourceName(this.sourceName);

        val.setDiscountId(this.discountId);
        val.setPromoCodeId(this.promoCodeId);
        val.setOfferApprovedAt(this.offerApprovedAt);

        return val;
    }

    public void open(String statusDetail) {
        status = LoanApplicationStatus.OPEN;
        this.statusDetail = statusDetail;
    }

    public void close(String statusDetail, LocalDate when) {
        close(statusDetail, when, null);
    }

    public void close(String statusDetail, LocalDate when, String reason) {
        this.status = LoanApplicationStatus.CLOSED;
        this.statusDetail = statusDetail;
        this.closeDate = when;
        this.closeReason = reason;
    }
}
