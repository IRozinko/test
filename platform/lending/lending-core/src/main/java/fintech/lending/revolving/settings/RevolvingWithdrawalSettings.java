package fintech.lending.revolving.settings;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class RevolvingWithdrawalSettings {

    private BigDecimal defaultAmount;
    private BigDecimal minAmountFirstWithdrawal;
    private BigDecimal minAmount;
    private BigDecimal maxAmount;
    private BigDecimal amountStep;

    private int minWithdrawalTermInMonths;
    private int maxWithdrawalTermInMonths;
    private int defaultWithdrawalTerm;
    private int withdrawalTermStep;
}
