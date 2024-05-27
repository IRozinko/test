package fintech.bo.spain.alfa.strategy;

import fintech.spain.alfa.strategy.CalculationType;
import fintech.spain.alfa.strategy.StrategyType;
import fintech.strategy.bo.SupportedStrategiesResolver;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

@Component
public class AlfaSupportedStrategiesResolver implements SupportedStrategiesResolver {
    @Override
    public List<SupportedStrategiesResolver.Strategy> resolve() {
        return Arrays.asList(
            new Strategy()
                .setType(StrategyType.FEE.getType())
                .setName(StrategyType.FEE.name())
                .setCalculationTypes(Arrays.asList(
                    CalculationType.FT.name()
                )),
            new Strategy()
                .setType(StrategyType.EXTENSION.getType())
                .setName(StrategyType.EXTENSION.name())
                .setCalculationTypes(Arrays.asList(
                    CalculationType.D.name()
                )),
            new Strategy()
                .setType(StrategyType.INTEREST.getType())
                .setName(StrategyType.INTEREST.name())
                .setCalculationTypes(Arrays.asList(
                    CalculationType.X.name()
                )),
            new Strategy()
                .setType(StrategyType.PENALTY.getType())
                .setName(StrategyType.PENALTY.name())
                .setCalculationTypes(
                    Arrays.asList(
                        CalculationType.A.name(),
                        CalculationType.AV.name()
                    )
                )
        );
    }
}
