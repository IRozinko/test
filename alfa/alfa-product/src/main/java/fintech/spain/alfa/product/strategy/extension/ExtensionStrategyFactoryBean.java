package fintech.spain.alfa.product.strategy.extension;

import com.fasterxml.jackson.databind.JsonNode;
import fintech.lending.core.loan.Loan;
import fintech.spain.alfa.product.extension.discounts.ExtensionDiscountService;
import fintech.strategy.CalculationStrategy;
import fintech.strategy.spi.ExtensionStrategy;
import fintech.strategy.spi.ExtensionStrategyFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
class ExtensionStrategyFactoryBean implements ExtensionStrategyFactory {

    @Autowired
    private ExtensionDiscountService discountService;

    @Override
    public ExtensionStrategy createFor(CalculationStrategy strategy, JsonNode properties, Loan loan) {
        if (AlfaExtensionStrategy.CALCULATION_TYPE.name().equals(strategy.getCalculationType())) {
            return new AlfaExtensionStrategy(loan, properties, discountService);
        }
        throw new IllegalArgumentException("Unsupported calculation type [" + strategy.getCalculationType() + "]");
    }
}
