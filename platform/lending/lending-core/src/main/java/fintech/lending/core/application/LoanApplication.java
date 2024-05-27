package fintech.lending.core.application;

import fintech.lending.core.PeriodUnit;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class LoanApplication {

    private Long id;
    private LoanApplicationType type;
    private Long productId;
    private Long clientId;
    private String ipAddress;
    private String ipCountry;
    private String referer;
    private Long loanId;
    private Long interestStrategyId;
    private Long penaltyStrategyId;
    private Long extensionStrategyId;
    private Long feeStrategyId;
    private String number;
    private LoanApplicationStatus status;
    private String statusDetail;
    private LocalDateTime submittedAt;
    private BigDecimal requestedPrincipal;
    private PeriodUnit requestedPeriodUnit;
    private Long requestedPeriodCount;
    private Long requestedInstallments;
    private BigDecimal requestedInterestDiscountPercent;
    private LocalDate offerDate;
    private BigDecimal offeredPrincipal;
    private BigDecimal offeredInterest;
    private BigDecimal offeredInterestDiscountPercent;
    private BigDecimal offeredInterestDiscountAmount;
    private PeriodUnit offeredPeriodUnit;
    private Long offeredPeriodCount;
    private Long offeredInstallments;
    private String closeReason;
    private LocalDate closeDate;
    private Long loansPaid;
    private Long workflowId;
    private BigDecimal creditLimit;
    private int invoicePaymentDay;
    private String createdBy;
    private LocalDateTime createdAt;
    private String updatedBy;
    private LocalDateTime updatedAt;
    private String shortApproveCode;
    private String longApproveCode;
    private BigDecimal score;

    @Deprecated
    private String scoreBucket;
    private String scoreSource;

    private BigDecimal nominalApr;
    private BigDecimal effectiveApr;
    private String uuid;
    private LoanApplicationSourceType sourceType;
    private String sourceName;
    private Long discountId;
    private Long promoCodeId;
    private LocalDateTime offerApprovedAt;

}
