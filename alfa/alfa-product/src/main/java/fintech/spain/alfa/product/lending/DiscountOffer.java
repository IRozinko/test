package fintech.spain.alfa.product.lending;

import lombok.Data;
import lombok.experimental.Accessors;

import java.math.BigDecimal;

@Data
@Accessors(chain = true)
public class DiscountOffer {

    private Long discountId;

    private BigDecimal rateInPercent;
}
