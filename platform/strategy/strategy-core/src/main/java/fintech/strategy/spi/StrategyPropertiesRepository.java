package fintech.strategy.spi;

import com.fasterxml.jackson.databind.JsonNode;

public interface StrategyPropertiesRepository {

    void saveStrategy(Long calculationStrategyId, JsonNode props);

    JsonNode getStrategyPropertiesAsJson(Long calculationStrategyId);

    Object getStrategyProperties(Long calculationStrategyId);

    boolean supports(String strategyType, String calculationType);
}
