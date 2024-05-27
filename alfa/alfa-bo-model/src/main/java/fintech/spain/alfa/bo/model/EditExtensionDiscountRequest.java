package fintech.spain.alfa.bo.model;

import lombok.Data;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@Accessors(chain = true)
public class EditExtensionDiscountRequest {

    @NotNull
    private Long extensionDiscountId;

    @NotNull
    private Long loanId;

    @NotNull
    private LocalDate effectiveFrom;

    @NotNull
    private LocalDate effectiveTo;

    @NotNull
    private BigDecimal rateInPercent;
}
