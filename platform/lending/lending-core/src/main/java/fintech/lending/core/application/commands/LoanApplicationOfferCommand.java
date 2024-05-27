package fintech.lending.core.application.commands;

import fintech.lending.core.PeriodUnit;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

import static fintech.BigDecimalUtils.amount;

@Data
public class LoanApplicationOfferCommand {

    private Long id;
    private BigDecimal principal = amount(0);
    private BigDecimal interest = amount(0);
    private BigDecimal interestDiscountPercent = amount(0);
    private BigDecimal interestDiscountAmount = amount(0);
    private LocalDate offerDate;
    private BigDecimal nominalApr;
    private BigDecimal effectiveApr;
    private Long periodCount;
    private PeriodUnit periodUnit;
    private Long discountId;

}
