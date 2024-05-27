package fintech.lending.core.promocode;

import lombok.Data;
import lombok.experimental.Accessors;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Set;

@Data
@Accessors(chain = true)
public class UpdatePromoCodeCommand {

    private Long promoCodeId;
    private String description;
    private LocalDate effectiveFrom;
    private LocalDate effectiveTo;
    private BigDecimal rateInPercent;
    private Long maxTimesToApply;
    private Set<String> sources;
    private List<String> clientNumbers;

}
