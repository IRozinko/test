package fintech.spain.alfa.bo.model;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class PreviewCalculationStrategyCmsItemRequest {
    private Long calculationStrategyId;
}
