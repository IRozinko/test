package fintech.lending.core.loan.commands;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateLoanCommand {

    private Long productId;
    private Long clientId;
    private String loanNumber;
    private LocalDate issueDate;
    private Long interestStrategyId;
    private Long penaltyStrategyId;
    private Long feeStrategyId;
    private LocalDate maturityDate;
    private BigDecimal principalDisbursed;
    private BigDecimal totalOutstanding;
    private BigDecimal totalDue;
    private String portfolio;
    private String company;
    private String debtState;
    private String debtStatus;

}
