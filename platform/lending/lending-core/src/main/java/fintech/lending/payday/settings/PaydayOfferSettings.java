package fintech.lending.payday.settings;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class PaydayOfferSettings {

    private BigDecimal minAmount;
    private BigDecimal maxAmount;
    private BigDecimal amountStep;
    private BigDecimal defaultAmount;
    private Integer minTerm;
    private Integer maxTerm;
    private Integer termStep;
    private Integer defaultTerm;

    private boolean useCreditLimitAsMaxAmount;
    private boolean setSliderToMaxAmount;
}
