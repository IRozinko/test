package fintech.strategy.bo;

public interface StrategyFormRenderer {
    boolean canRender(String strategyType, String calculationType);

    StrategyForm renderNew();

    StrategyForm renderForStrategy(Long strategyId);
}
