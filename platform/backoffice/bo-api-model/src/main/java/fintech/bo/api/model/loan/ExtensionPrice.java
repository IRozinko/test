package fintech.bo.api.model.loan;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class ExtensionPrice {

    private String periodUnit;
    private Long periodCount;
    private BigDecimal price;
    private BigDecimal priceWithDiscount;
    private BigDecimal discountAmount;
    private BigDecimal discountPercent;
}
