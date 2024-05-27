package fintech.lending.creditline.settings;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class CreditLinePenaltySettings {

    private BigDecimal penaltyRatePerDayPercent;

    private long invoiceGracePeriodInDays;

    private long brokenLoanGracePeriodInDays;

    private BigDecimal maxLimitOfPrincipalPercent;

}
