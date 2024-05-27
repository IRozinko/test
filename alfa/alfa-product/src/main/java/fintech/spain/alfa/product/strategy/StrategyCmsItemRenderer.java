package fintech.spain.alfa.product.strategy;

import fintech.cms.Pdf;
import fintech.cms.PdfRenderer;
import fintech.spain.alfa.product.cms.CalculationStrategyModel;
import fintech.spain.alfa.product.cms.AlfaCmsModels;
import fintech.strategy.CalculationStrategyService;
import fintech.strategy.db.CalculationStrategyEntity;
import fintech.strategy.db.CalculationStrategyRepository;
import fintech.spain.alfa.strategy.CalculationStrategyCmsItemKey;
import fintech.spain.alfa.strategy.StrategyType;
import fintech.spain.alfa.strategy.extension.ExtensionStrategyProperties;
import fintech.spain.alfa.strategy.interest.MonthlyInterestStrategyProperties;
import fintech.spain.alfa.strategy.penalty.DailyPenaltyStrategyProperties;
import fintech.spain.alfa.strategy.penalty.DpdPenaltyStrategyProperties;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static fintech.spain.alfa.product.cms.CmsSetup.STRATEGY_PREVIEW_PDF;

@Component
@AllArgsConstructor
public class StrategyCmsItemRenderer {

    private final PdfRenderer pdfRenderer;
    private final CalculationStrategyRepository strategyRepository;
    private final CalculationStrategyService calculationStrategyService;

    public Optional<Pdf> renderStrategy(Long strategyId) {
        CalculationStrategyEntity entity = strategyRepository.getRequired(strategyId);
        Object properties = calculationStrategyService.getStrategyProperties(strategyId);

        String strategyType = entity.getStrategyType();
        String calculationType = entity.getCalculationType();

        String key = new CalculationStrategyCmsItemKey(strategyType, calculationType).get();

        CalculationStrategyModel model = new CalculationStrategyModel();

        if (StrategyType.EXTENSION.getType().equals(strategyType)) {
            model.setExtensionStrategy(CalculationStrategyModel.EXTENSION_STRATEGY_D);
            model.setExtensionStrategyDProperties((ExtensionStrategyProperties) properties);
        }
        if (StrategyType.INTEREST.getType().equals(strategyType)) {
            model.setInterestStrategy(CalculationStrategyModel.INTEREST_STRATEGY_X);
            model.setInterestStrategyXProperties((MonthlyInterestStrategyProperties) properties);
        }
        if (StrategyType.PENALTY.getType().equals(strategyType)
            && CalculationStrategyModel.PENALTY_STRATEGY_A.endsWith(calculationType)) {
            model.setPenaltyStrategy(CalculationStrategyModel.PENALTY_STRATEGY_A);
            model.setPenaltyStrategyAProperties((DailyPenaltyStrategyProperties) properties);
        }
        if (StrategyType.PENALTY.getType().equals(strategyType)
            && CalculationStrategyModel.PENALTY_STRATEGY_AV.endsWith(calculationType)) {
            model.setPenaltyStrategy(CalculationStrategyModel.PENALTY_STRATEGY_AV);
            model.setPenaltyStrategyAVProperties((DpdPenaltyStrategyProperties) properties);
        }

        Map<String, Object> context = new HashMap<>();
        context.put("targetStrategy", key);
        context.put(AlfaCmsModels.SCOPE_CALCULATION_STRATEGY, model);

        return pdfRenderer.render(STRATEGY_PREVIEW_PDF, context, "default");
    }
}
