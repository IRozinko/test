package fintech.spain.alfa.product.risk.rules.basic;

import lombok.Data;
import lombok.experimental.Accessors;

import java.math.BigDecimal;

@Data
@Accessors(chain = true)
public class BasicRuleParams {
    private int applicationCountWithin30Days;
    private int rejectionCountIn30Days;
    private int rejectionCountIn7Days;
    private BigDecimal principalDisbursed;
    private BigDecimal principalSold;
    private BigDecimal feePaid;
    private BigDecimal penaltyPaid;
    private BigDecimal cashIn;
    private BigDecimal maxPrincipalRepaid;
    private Long daysSinceLastApplication;
    private Long daysSinceLastApplicationRejection;
    private String lastLoanApplicationRejectionReason;
    private int totalOverdueDays;
    private int maxOverdueDays;
    private int maxOverdueDaysInLast12Months;
    private int lastLoanOverdueDays;
    private int paidLoanCount;
    private long age;
    private BigDecimal income;
}
