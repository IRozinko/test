package fintech.lending.core.promocode.db;

import lombok.Data;
import lombok.experimental.Accessors;

import java.math.BigDecimal;
import java.time.LocalDate;

@Accessors(chain = true)
@Data
public class PromoCode {

    private Long id;
    private String code;

    private String description;

    private LocalDate effectiveFrom;

    private LocalDate effectiveTo;

    private BigDecimal rateInPercent;

    private Long maxTimesToApply;

    private boolean newClientsOnly;

    private boolean active;
}
