package fintech.spain.alfa.product.utils;

import fintech.transactions.Balance;

import java.math.BigDecimal;

public class CostCalculationUtils {

    public static BigDecimal availableCosts(Balance loanBalance) {
        BigDecimal maxCosts = loanBalance.getPrincipalDisbursed().multiply(BigDecimal.valueOf(1.5));
        return maxCosts.subtract(loanBalance.getInterestApplied());
    }

}
