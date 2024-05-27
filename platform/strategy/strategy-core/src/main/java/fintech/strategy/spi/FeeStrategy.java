package fintech.strategy.spi;

import java.math.BigDecimal;

public interface FeeStrategy {
    BigDecimal calculate(Long loanId, String company);
}
