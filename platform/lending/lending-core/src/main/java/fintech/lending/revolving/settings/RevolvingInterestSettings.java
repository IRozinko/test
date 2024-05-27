package fintech.lending.revolving.settings;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RevolvingInterestSettings {

    private LocalDate startDate;
    private BigDecimal ratePerYearPercent;
}
