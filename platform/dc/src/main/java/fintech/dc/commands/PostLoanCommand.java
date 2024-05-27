package fintech.dc.commands;

import lombok.Data;
import lombok.experimental.Accessors;

import java.math.BigDecimal;
import java.time.LocalDate;

@Accessors(chain = true)
@Data
public class PostLoanCommand {

    private Long loanId;
    private String loanNumber;
    private Long clientId;
    private int dpd;
    private int maxDpd;
    private BigDecimal totalDue;
    private BigDecimal principalDue;
    private BigDecimal interestDue;
    private BigDecimal penaltyDue;
    private BigDecimal feeDue;
    private BigDecimal totalOutstanding;
    private BigDecimal principalOutstanding;
    private BigDecimal interestOutstanding;
    private BigDecimal penaltyOutstanding;
    private BigDecimal feeOutstanding;
    private BigDecimal totalPaid;
    private BigDecimal principalPaid;
    private BigDecimal interestPaid;
    private BigDecimal penaltyPaid;
    private BigDecimal feePaid;
    private LocalDate lastPaymentDate;
    private BigDecimal lastPaid;
    private LocalDate maturityDate;
    private LocalDate paymentDueDate;
    private String loanStatus;
    private String loanStatusDetail;
    private Long periodCount = 0L;

    private boolean triggerActionsImmediately;
    private String status;
    private String state;
}
