package fintech.lending.revolving.settings;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class RevolvingPenaltySettings {

    private BigDecimal penaltyRatePerDayPercent;

    private long invoiceGracePeriodInDays;

    private long brokenLoanGracePeriodInDays;

    private BigDecimal maxLimitOfPrincipalPercent;

}
