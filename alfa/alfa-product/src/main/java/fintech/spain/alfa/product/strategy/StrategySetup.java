package fintech.spain.alfa.product.strategy;

import fintech.spain.alfa.product.strategy.extension.AlfaExtensionStrategy;
import fintech.spain.alfa.product.strategy.fee.AlfaFeeStrategy;
import fintech.spain.alfa.product.strategy.interest.AlfaInterestStrategy;
import fintech.spain.alfa.product.strategy.penalty.AlfaDailyPenaltyStrategy;
import fintech.spain.alfa.strategy.StrategyType;
import fintech.spain.alfa.strategy.extension.ExtensionStrategyProperties;
import fintech.spain.alfa.strategy.fee.FeeStrategyProperties;
import fintech.spain.alfa.strategy.interest.MonthlyInterestStrategyProperties;
import fintech.spain.alfa.strategy.penalty.DailyPenaltyStrategyProperties;
import fintech.strategy.CalculationStrategyService;
import fintech.strategy.SaveCalculationStrategyCommand;
import fintech.strategy.db.CalculationStrategyRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;

import static fintech.BigDecimalUtils.amount;
import static fintech.spain.alfa.strategy.extension.ExtensionStrategyProperties.ExtensionOption;

@Component
@Transactional
public class StrategySetup {

    @Autowired
    private CalculationStrategyService calculationStrategyService;

    @Autowired
    private CalculationStrategyRepository strategyRepository;

    public void setUp() {
        if (strategyRepository.count() == 0L) {
            initExtensionStrategy();
        }
    }

    private void initExtensionStrategy() {
        calculationStrategyService.saveCalculationStrategy(
            new SaveCalculationStrategyCommand()
                .setStrategyType(StrategyType.EXTENSION.getType())
                .setCalculationType(AlfaExtensionStrategy.CALCULATION_TYPE.name())
                .setVersion("001")
                .setDefault(true)
                .setEnabled(true)
                .setProperties(
                    new ExtensionStrategyProperties()
                        .setExtensions(Arrays.asList(
                            new ExtensionOption().setTerm(7L).setRate(amount(10.00)),
                            new ExtensionOption().setTerm(14L).setRate(amount(14.00)),
                            new ExtensionOption().setTerm(30L).setRate(amount(26.00)),
                            new ExtensionOption().setTerm(45L).setRate(amount(35.00))
                        ))
                ));
        calculationStrategyService.saveCalculationStrategy(
            new SaveCalculationStrategyCommand()
                .setStrategyType(StrategyType.INTEREST.getType())
                .setCalculationType(AlfaInterestStrategy.CALCULATION_TYPE.name())
                .setVersion("001")
                .setDefault(true)
                .setEnabled(true)
                .setProperties(
                    new MonthlyInterestStrategyProperties()
                        .setMonthlyInterestRate(amount(35.00))
                        .setUsingDecisionEngine(false)
                        .setScenario("interest_setting"))
                );
        calculationStrategyService.saveCalculationStrategy(
            new SaveCalculationStrategyCommand()
                .setStrategyType(StrategyType.PENALTY.getType())
                .setCalculationType(AlfaDailyPenaltyStrategy.CALCULATION_TYPE.name())
                .setVersion("001")
                .setDefault(true)
                .setEnabled(true)
                .setProperties(
                    new DailyPenaltyStrategyProperties()
                        .setPenaltyRate(amount(1.00))
                ));
        calculationStrategyService.saveCalculationStrategy(
            new SaveCalculationStrategyCommand()
                .setStrategyType(StrategyType.FEE.getType())
                .setCalculationType(AlfaFeeStrategy.CALCULATION_TYPE.name())
                .setVersion("001")
                .setDefault(true)
                .setEnabled(true)
                .setProperties(
                    new FeeStrategyProperties()
                        .setFees(Arrays.asList(
                            new FeeStrategyProperties.FeeOption().setOneTimeFeeRate(amount(10.0)).setCompany("default")
                        ))
                ));
    }
}
