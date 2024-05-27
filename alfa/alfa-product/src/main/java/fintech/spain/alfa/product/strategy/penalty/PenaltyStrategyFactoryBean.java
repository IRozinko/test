package fintech.spain.alfa.product.strategy.penalty;

import com.fasterxml.jackson.databind.JsonNode;
import fintech.JsonUtils;
import fintech.lending.core.loan.Loan;
import fintech.strategy.CalculationStrategy;
import fintech.strategy.spi.PenaltyStrategy;
import fintech.strategy.spi.PenaltyStrategyFactory;
import fintech.transactions.TransactionService;
import fintech.spain.alfa.strategy.penalty.DailyPenaltyStrategyProperties;
import fintech.spain.alfa.strategy.penalty.DpdPenaltyStrategyProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
class PenaltyStrategyFactoryBean implements PenaltyStrategyFactory {

    @Autowired
    private TransactionService transactionService;

    @Override
    public PenaltyStrategy createFor(CalculationStrategy strategy, JsonNode properties, Loan loan) {
        if (AlfaDailyPenaltyStrategy.CALCULATION_TYPE.name().equals(strategy.getCalculationType())) {
            DailyPenaltyStrategyProperties strategyProperties = JsonUtils.readValue(properties, DailyPenaltyStrategyProperties.class);
            return new AlfaDailyPenaltyStrategy(loan, transactionService, strategyProperties);

        } else if (AlfaDpdPenaltyStrategy.CALCULATION_TYPE.name().equals(strategy.getCalculationType())) {
            DpdPenaltyStrategyProperties strategyProperties = JsonUtils.readValue(properties, DpdPenaltyStrategyProperties.class);
            return new AlfaDpdPenaltyStrategy(loan, transactionService, strategyProperties);

        }
        throw new IllegalArgumentException("Unsupported calculation type [" + strategy.getCalculationType() + "]");
    }
}
