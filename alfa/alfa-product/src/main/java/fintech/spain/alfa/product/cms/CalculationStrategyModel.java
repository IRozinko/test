package fintech.spain.alfa.product.cms;

import fintech.spain.alfa.strategy.extension.ExtensionStrategyProperties;
import fintech.spain.alfa.strategy.interest.MonthlyInterestStrategyProperties;
import fintech.spain.alfa.strategy.penalty.DailyPenaltyStrategyProperties;
import fintech.spain.alfa.strategy.penalty.DpdPenaltyStrategyProperties;
import lombok.Data;

@Data
public class CalculationStrategyModel {

    public static final String EXTENSION_STRATEGY_D = "_strategy_E_D";
    public static final String INTEREST_STRATEGY_X = "_strategy_I_X";
    public static final String PENALTY_STRATEGY_A = "_strategy_P_A";
    public static final String PENALTY_STRATEGY_AV = "_strategy_P_AV";

    private String penaltyStrategy;
    private String interestStrategy;
    private String extensionStrategy;

    private ExtensionStrategyProperties extensionStrategyDProperties;
    private MonthlyInterestStrategyProperties interestStrategyXProperties;
    private DailyPenaltyStrategyProperties penaltyStrategyAProperties;
    private DpdPenaltyStrategyProperties penaltyStrategyAVProperties;

}
