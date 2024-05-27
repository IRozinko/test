package fintech.spain.alfa.product.strategy.interest;

import com.fasterxml.jackson.databind.JsonNode;
import fintech.JsonUtils;
import fintech.spain.alfa.strategy.interest.MonthlyInterestStrategyProperties;
import fintech.strategy.CalculationStrategy;
import fintech.strategy.spi.InterestStrategy;
import fintech.strategy.spi.InterestStrategyFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

@Component
class InterestStrategyFactoryBean implements InterestStrategyFactory {

    @Autowired
    private ApplicationContext applicationContext;

    @Override
    public InterestStrategy createFor(CalculationStrategy strategy, JsonNode properties) {
        if (AlfaInterestStrategy.CALCULATION_TYPE.name().equals(strategy.getCalculationType())) {
            return applicationContext.getBean(AlfaInterestStrategy.class, JsonUtils.readValue(properties, MonthlyInterestStrategyProperties.class));
        }
        throw new IllegalArgumentException("Unsupported calculation type [" + strategy.getCalculationType() + "]");
    }
}
