package fintech.strategy.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.math.BigDecimal;
import java.time.temporal.ChronoUnit;

@Data
@Accessors(chain = true)
@AllArgsConstructor
@NoArgsConstructor
public class ExtensionOffer {

    private ChronoUnit periodUnit;
    private Long periodCount;
    private BigDecimal price;
    private BigDecimal priceWithDiscount;
    private BigDecimal discountPercent;
    private BigDecimal discountAmount;
}
