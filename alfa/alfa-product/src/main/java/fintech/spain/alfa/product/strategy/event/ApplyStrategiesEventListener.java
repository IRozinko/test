package fintech.spain.alfa.product.strategy.event;

import fintech.lending.core.loan.LoanService;
import fintech.lending.core.loan.commands.UpdateStrategiesCommand;
import fintech.lending.core.loan.events.LoanApplyStrategiesEvent;
import fintech.spain.alfa.strategy.StrategyType;
import fintech.strategy.CalculationStrategyService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
@Slf4j
class ApplyStrategiesEventListener {
    @Autowired
    private CalculationStrategyService strategyService;
    @Autowired
    private LoanService loanService;

    @EventListener
    public void onEvent(LoanApplyStrategiesEvent event) {
        log.info("Applying strategies to loan");
            Long feeStrategyId = strategyService.getDefaultStrategyId(StrategyType.FEE.getType()).orElse(null);
            Long penaltyStrategyId = strategyService.getDefaultStrategyId(StrategyType.PENALTY.getType()).orElse(null);
            loanService.updateStrategies(new UpdateStrategiesCommand().setLoanId(event.getLoanId())
                .setFeeStrategyId(feeStrategyId)
                .setPenaltyStrategyId(penaltyStrategyId));
    }
}
