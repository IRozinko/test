package fintech.spain.alfa.product.lending;

import lombok.Data;
import lombok.experimental.Accessors;

import java.math.BigDecimal;

@Data
@Accessors(chain = true)
public class CalculateCreditLimitCommand {

    private Long clientId;
    private BigDecimal lastCreditLimit;

}
