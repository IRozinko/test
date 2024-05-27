package fintech.spain.alfa.product.dc;

public interface StrategyRegistry {

    void add(String companyName, Class<? extends StrategyIdentifier> strategy);

    StrategyIdentifier getStrategy(String companyName);
}
