package fintech.spain.alfa.product.lending;

import java.math.BigDecimal;

public interface CreditLimitCalculator {

    BigDecimal calculateCreditLimit(CalculateCreditLimitCommand command);
}
