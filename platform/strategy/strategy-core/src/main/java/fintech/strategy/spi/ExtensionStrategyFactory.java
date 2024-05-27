package fintech.strategy.spi;

import com.fasterxml.jackson.databind.JsonNode;
import fintech.lending.core.loan.Loan;
import fintech.strategy.CalculationStrategy;

public interface ExtensionStrategyFactory {
    ExtensionStrategy createFor(CalculationStrategy strategy, JsonNode properties, Loan loan);
}
