package fintech.lending.creditline.settings;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class CreditLineWithdrawalSettings {

    private BigDecimal minAmount;

    private BigDecimal amountStep;

}
