package fintech.lending.creditline.settings;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class CreditLineOfferSettings {

    private BigDecimal minAmount;
    private BigDecimal maxAmount;
    private BigDecimal amountStep;
    private BigDecimal defaultAmount;

}
