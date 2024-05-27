package fintech.strategy.spi;

import com.fasterxml.jackson.databind.JsonNode;
import fintech.strategy.CalculationStrategy;

public interface InterestStrategyFactory {
    InterestStrategy createFor(CalculationStrategy strategy, JsonNode properties);
}
