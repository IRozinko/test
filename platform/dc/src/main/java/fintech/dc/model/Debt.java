package fintech.dc.model;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class Debt {

    private Long id;
    private Long clientId;
    private Long loanId;
    private int dpd;
    private int maxDpd;
    private String agingBucket;
    private String portfolio;
    private String status;
    private int priority;
    private String loanStatus;
    private String loanStatusDetail;
    private String agent;
    private LocalDateTime nextActionAt;
    private String nextAction;
    private LocalDateTime lastActionAt;
    private String lastAction;
    private BigDecimal totalDue;
    private BigDecimal totalOutstanding;
    private BigDecimal totalPaid;

    private int promiseDpd;
    private LocalDate promiseDueDate;

    private String managingCompany;
    private String owningCompany;

    private String subStatus;

    private Long periodCount;
    private LocalDate paymentDueDate;
    private String debtState;
    private String debtStatus;
    private String debtSubStatus;

}
