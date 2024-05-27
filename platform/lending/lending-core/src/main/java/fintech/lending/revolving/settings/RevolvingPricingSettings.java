package fintech.lending.revolving.settings;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static fintech.BigDecimalUtils.amount;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RevolvingPricingSettings {

    private List<RevolvingInterestSettings> interestRatePerYearPercent = new ArrayList<>();
    private BigDecimal servicingFeeRatePerYearPercent = amount(0);
    private BigDecimal firstDisbursementFeePercent = amount(0);
    private BigDecimal disbursementFeePercent = amount(0);
    private boolean isNeededForAccounting = false;

}
