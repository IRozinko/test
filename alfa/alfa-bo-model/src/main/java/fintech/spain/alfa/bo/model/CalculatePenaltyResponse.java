package fintech.spain.alfa.bo.model;

import lombok.Data;
import lombok.experimental.Accessors;

import java.math.BigDecimal;

@Data
@Accessors(chain = true)
public class CalculatePenaltyResponse {

    private boolean penaltyApplicable;
    private BigDecimal principalDue;
    private BigDecimal interestDue;
    private BigDecimal feeDue;
    private BigDecimal penaltyDue;
    private BigDecimal newPenalty;
    private BigDecimal totalPenaltyDue;
    private BigDecimal totalDue;
}
