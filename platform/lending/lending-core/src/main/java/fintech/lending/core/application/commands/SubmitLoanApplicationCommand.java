package fintech.lending.core.application.commands;

import fintech.lending.core.PeriodUnit;
import fintech.lending.core.application.LoanApplicationSourceType;
import fintech.lending.core.application.LoanApplicationType;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static fintech.BigDecimalUtils.amount;

@Data
public class SubmitLoanApplicationCommand {

    private LoanApplicationType type;
    private String loanApplicationStatusDetail;
    private Long clientId;
    private String applicationNumber;
    private String ipAddress;
    private String ipCountry;
    private String referer;
    private Long productId;
    private BigDecimal principal;
    private Long periodCount;
    private PeriodUnit periodUnit;
    private LocalDateTime submittedAt;
    private int invoiceDay;
    private Long loanId;
    private String shortApproveCode;
    private String longApproveCode;
    private String sourceName;
    private LoanApplicationSourceType sourceType;
    private BigDecimal interestDiscountPercent = amount(0);
    private Long discountId;
    private Long promoCodeId;
    private Long loansPaid;

    private Long interestStrategyId;
    private Long penaltyStrategyId;
    private Long extensionStrategyId;
    private Long feeStrategyId;
}
