package fintech.spain.alfa.strategy.interest;

import lombok.Data;
import lombok.experimental.Accessors;

import java.math.BigDecimal;

@Data
@Accessors(chain = true)
public class MonthlyInterestStrategyProperties {
    private BigDecimal monthlyInterestRate;
    private boolean usingDecisionEngine;
    private String scenario;
}
