package fintech.bo.api.model.loan;

import lombok.Data;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Set;

@Data
@Accessors(chain = true)
public class EditPromoCodeRequest {

    @NotNull
    private Long promoCodeId;

    private String description;

    @NotNull
    private LocalDate effectiveFrom;

    @NotNull
    private LocalDate effectiveTo;

    @NotNull
    private BigDecimal rateInPercent;

    private Set<String> sources;

    @NotNull
    private Long maxTimesToApply;

}
