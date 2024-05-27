package fintech.lending.core.discount;

import lombok.Data;
import lombok.experimental.Accessors;

import java.math.BigDecimal;

@Data
@Accessors(chain = true)
public class Discount {

    private Long id;

    private BigDecimal rateInPercent;
}
