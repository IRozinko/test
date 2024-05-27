package fintech.spain.alfa.product.lending;

import lombok.Data;
import lombok.experimental.Accessors;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@Accessors(chain = true)
public class Offer {

    private BigDecimal principal;
    private BigDecimal interest;
    private BigDecimal interestDiscountRatePercent;
    private BigDecimal interestDiscountAmount;
    private BigDecimal total;

    private Long termInDays;
    private LocalDate offerDate;
    private LocalDate maturityDate;

    // TAE
    private BigDecimal aprPercent;
    // TIN
    private BigDecimal nominalApr;
    // TIN mensual
    private BigDecimal monthlyInterestRatePercent;

    private Long discountId;
    private Long promoCodeId;
}
