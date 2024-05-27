package fintech.spain.alfa.product.lending;

import lombok.Data;
import lombok.experimental.Accessors;

import java.math.BigDecimal;

@Data
@Accessors(chain = true)
public class OfferSettings {

    private BigDecimal minAmount;
    private BigDecimal maxAmount;
    private BigDecimal amountStep;
    private BigDecimal defaultAmount;
    private Integer minTerm;
    private Integer maxTerm;
    private Integer termStep;
    private Integer defaultTerm;
    private BigDecimal interestDiscountPercent;
}
