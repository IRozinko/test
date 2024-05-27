package fintech.dc.model;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class AgentPriority {
    private String agent;
    private Long debtCount;
    private BigDecimal amountDue;
    private double priority;
}
