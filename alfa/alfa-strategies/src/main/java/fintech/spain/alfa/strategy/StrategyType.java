package fintech.spain.alfa.strategy;

public enum StrategyType {
    INTEREST("I"),
    PENALTY("P"),
    EXTENSION("E"),
    FEE("F");

    private final String type;

    StrategyType(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }
}
