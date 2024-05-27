package fintech.strategy.spi;

import java.math.BigDecimal;
import java.util.Optional;

public interface InterestStrategy {

    BigDecimal calculateInterest(BigDecimal principal, Long termInDays, Optional<Long> loanApplicationId);
}
