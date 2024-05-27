package fintech.bo.components.loan.promocodes;

import lombok.Data;
import lombok.experimental.Accessors;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@Accessors(chain = true)
public class PromoCodeDetails {

    private Long id;
    private String code;
    private String description;
    private String type;
    private LocalDate effectiveFrom;
    private LocalDate effectiveTo;
    private BigDecimal rateInPercent;
    private Long maxTimesToApply;
    private boolean active;
    private String source;
    private Long timesUsed;

}
