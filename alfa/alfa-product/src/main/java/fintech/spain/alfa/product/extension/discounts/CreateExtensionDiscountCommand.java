package fintech.spain.alfa.product.extension.discounts;

import lombok.Data;
import lombok.experimental.Accessors;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@Accessors(chain = true)
public class CreateExtensionDiscountCommand {
    private Long loanId;
    private LocalDate effectiveFrom;
    private LocalDate effectiveTo;
    private BigDecimal rateInPercent;
}
