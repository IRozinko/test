package fintech.spain.alfa.strategy;

public class CalculationStrategyCmsItemKey {
    public static final String KEY_TEMPLATE = "_strategy_%s_%s";

    private String strategyType;
    private String calculationType;

    public CalculationStrategyCmsItemKey(String strategyType, String calculationType) {
        this.strategyType = strategyType;
        this.calculationType = calculationType;
    }

    public String get() {
        return String.format(KEY_TEMPLATE, strategyType, calculationType);
    }
}
