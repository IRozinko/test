package fintech.strategy;

import fintech.strategy.spi.ExtensionStrategy;
import fintech.strategy.spi.FeeStrategy;
import fintech.strategy.spi.InterestStrategy;
import fintech.strategy.spi.PenaltyStrategy;

import java.util.Optional;

public interface CalculationStrategyService {

    Long saveCalculationStrategy(SaveCalculationStrategyCommand command);

    void updateCalculationStrategy(UpdateCalculationStrategyCommand command);

    Optional<Long> getDefaultStrategyId(String strategyType);

    Optional<ExtensionStrategy> getExtensionStrategyForLoan(Long loanId);

    Optional<InterestStrategy> getInterestStrategyById(Long strategyId);

    Optional<FeeStrategy> getFeeStrategyForLoan(Long loanId);

    Optional<PenaltyStrategy> getPenaltyStrategyForLoan(Long loanId);

    Object getStrategyProperties(Long strategyId);

    Optional<CalculationStrategy> getStrategy(String strategyType, String calculationType, String versionType, boolean enable);
}
