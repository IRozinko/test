package fintech.strategy;

import com.fasterxml.jackson.databind.JsonNode;
import fintech.lending.core.loan.Loan;
import fintech.strategy.spi.FeeStrategy;

public interface FeeStrategyFactory {

    FeeStrategy createFor(CalculationStrategy strategy, JsonNode properties, Loan loan);
}
