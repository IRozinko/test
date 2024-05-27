package fintech.spain.alfa.product.strategy.fee;

import com.fasterxml.jackson.databind.JsonNode;
import fintech.JsonUtils;
import fintech.lending.core.loan.Loan;
import fintech.spain.alfa.product.strategy.interest.AlfaInterestStrategy;
import fintech.spain.alfa.strategy.fee.FeeStrategyProperties;
import fintech.strategy.CalculationStrategy;
import fintech.strategy.FeeStrategyFactory;
import fintech.strategy.spi.FeeStrategy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

@Component
class FeeStrategyFactoryBean implements FeeStrategyFactory {

    @Autowired
    private ApplicationContext applicationContext;

    @Override
    public FeeStrategy createFor(CalculationStrategy strategy, JsonNode properties, Loan loan) {
        if (AlfaInterestStrategy.CALCULATION_TYPE.name().equals(strategy.getCalculationType())) {
            return applicationContext.getBean(AlfaFeeStrategy.class, JsonUtils.readValue(properties, FeeStrategyProperties.class));
        }
        throw new IllegalArgumentException("Unsupported calculation type [" + strategy.getCalculationType() + "]");
    }
}
