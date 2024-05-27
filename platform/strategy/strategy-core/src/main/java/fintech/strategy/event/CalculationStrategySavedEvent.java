package fintech.strategy.event;

import fintech.strategy.CalculationStrategy;

public class CalculationStrategySavedEvent {
    private CalculationStrategy strategy;

    public CalculationStrategySavedEvent(CalculationStrategy strategy) {
        this.strategy = strategy;
    }

    public CalculationStrategy getStrategy() {
        return strategy;
    }
}
