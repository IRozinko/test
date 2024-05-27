package fintech.lending.core.discount;

import lombok.Data;
import lombok.experimental.Accessors;

import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@Accessors(chain = true)
public class ApplyDiscountCommand {

    @NotNull
    private Long clientId;

    @NotNull
    @DecimalMin("0.01")
    private BigDecimal rateInPercent;

    @NotNull
    private LocalDate effectiveFrom;

    @NotNull
    private LocalDate effectiveTo;

    @NotNull
    private Integer maxTimesToApply = 1;
}
