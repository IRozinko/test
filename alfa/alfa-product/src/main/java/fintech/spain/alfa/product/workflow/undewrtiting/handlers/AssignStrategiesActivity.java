package fintech.spain.alfa.product.workflow.undewrtiting.handlers;


import fintech.JsonUtils;
import fintech.decision.DecisionEngineService;
import fintech.decision.model.DecisionEngineRequest;
import fintech.decision.model.DecisionRequestStatus;
import fintech.decision.model.DecisionResult;
import fintech.lending.core.application.LoanApplicationService;
import fintech.lending.core.application.commands.UpdateLoanAppStrategiesCommand;
import fintech.settings.SettingsService;
import fintech.spain.alfa.product.risk.idvalidation.IdValidationValuesService;
import fintech.spain.alfa.product.settings.AlfaSettings;
import fintech.spain.alfa.product.workflow.WorkflowAttributes;
import fintech.spain.alfa.product.workflow.common.Resolutions;
import fintech.spain.alfa.strategy.StrategyType;
import fintech.strategy.CalculationStrategy;
import fintech.strategy.CalculationStrategyService;
import fintech.workflow.spi.ActivityContext;
import fintech.workflow.spi.ActivityHandler;
import fintech.workflow.spi.ActivityResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.Collections;
import java.util.Optional;
import java.util.function.Function;


@Slf4j
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
@Component
public class AssignStrategiesActivity implements ActivityHandler {

    public static final String REASON_DETAIL_ENGINE_UNRESPONSIVE = "DecisionEngineUnresponsive";
    public static final String REASON_ENGINE_RESPOND_ERROR_RESPOND_STATUS = "DecisionEngineRespondErrorRespondStatus";
    public static final String REASON_ENGINE_RESPOND_EMPTY_ARRAY_STRATEGIES = "DecisionEngineRespondEmptyStrategiesArray";
    public static final String REASON_ENGINE_RESPOND_NOT_VALID_STRATEGIES = "DecisionEngineRespondNotValidStrategies";
    public static final String REASON_UPDATED_STRATEGIES_FROM_DE = "UpdatedStrategiesFromDecisionEngine";
    public static final String REASON_ERROR_DURING_PROCESS_ENGINE_REQUEST = "ErrorDuringProcessDecisionEngineRequest:500";

    public static final String REGEX_PATTERN_CHECK_STRATEGY_FORMAT = "[A-Z]{2}[0-9]{3}";

    private final Function<AlfaSettings.ScenarioStrategiesSettings, String> scenarioNameProvider;

    @Autowired
    private DecisionEngineService decisionEngineService;

    @Autowired
    private SettingsService settingsService;

    @Autowired
    private LoanApplicationService loanApplicationService;

    @Autowired
    private CalculationStrategyService calculationStrategyService;

    @Autowired
    private IdValidationValuesService idValidationValuesService;


    public AssignStrategiesActivity(Function<AlfaSettings.ScenarioStrategiesSettings, String> scenarioNameProvider) {
        this.scenarioNameProvider = scenarioNameProvider;
    }

    @Override
    public ActivityResult handle(ActivityContext context) {

        try {
            Long clientId = context.getWorkflow().getClientId();
            DecisionResult result = getStrategies(context, clientId);
            if (result.getStatus() != DecisionRequestStatus.OK) {
                return failHandler(context, REASON_ENGINE_RESPOND_ERROR_RESPOND_STATUS);
            }

            if (CollectionUtils.isEmpty(result.getArrayResult())) {
                return ActivityResult.resolution(Resolutions.SKIP, REASON_ENGINE_RESPOND_EMPTY_ARRAY_STRATEGIES);
            }

            UpdateLoanAppStrategiesCommand updateLoanAppStrategiesCommand = processResultDecisionEngine(context, result);
            if (updateLoanAppStrategiesCommand == null) {
                return failHandler(context, REASON_ENGINE_RESPOND_NOT_VALID_STRATEGIES);
            }

            loanApplicationService.updateStrategies(updateLoanAppStrategiesCommand);

            return ActivityResult.resolution(Resolutions.OK, REASON_UPDATED_STRATEGIES_FROM_DE);

        } catch (Exception ex) {
            return failHandler(context, REASON_ERROR_DURING_PROCESS_ENGINE_REQUEST);
        }
    }


    private DecisionResult getStrategies(ActivityContext context, Long clientId) {
        AlfaSettings.ScenarioStrategiesSettings settings = settingsService.getJson(AlfaSettings.DECISION_ENGINE_STRATEGIES_SCENARIO, AlfaSettings.ScenarioStrategiesSettings.class);
        Long scoringModelId = idValidationValuesService.collectValues(clientId, context.getWorkflow().getApplicationId(), null).getId();
        String scenarioName = scenarioNameProvider.apply(settings);
        return decisionEngineService.getStrategies(new DecisionEngineRequest(scenarioName, clientId, scoringModelId, Collections.emptyList()));
    }


    private UpdateLoanAppStrategiesCommand processResultDecisionEngine(ActivityContext context, DecisionResult result) {
        if (CollectionUtils.isEmpty(result.getArrayResult())) {
            return null;
        }
        context.setAttribute(WorkflowAttributes.DECISION_ENGINE_STRATEGIES_ARRAY, JsonUtils.writeValueAsString(result.getArrayResult()));
        UpdateLoanAppStrategiesCommand updateLoanAppStrategiesCommand = new UpdateLoanAppStrategiesCommand(context.getWorkflow().getApplicationId());
        result.getArrayResult().stream()
            .map(this::getAlfaStrategy)
            .filter(Optional::isPresent)
            .map(Optional::get)
            .forEach(strategy -> setStrategyId(strategy, updateLoanAppStrategiesCommand));
        return updateLoanAppStrategiesCommand;
    }


    private Optional<CalculationStrategy> getAlfaStrategy(String strategy) {

        if (strategy == null || !strategy.matches(REGEX_PATTERN_CHECK_STRATEGY_FORMAT)) {
            return Optional.empty();
        }

        String strategyType = strategy.substring(0, 1);
        String calculationType = strategy.substring(1, 2);
        String versionType = strategy.substring(2);
        return calculationStrategyService.getStrategy(strategyType, calculationType, versionType, true);
    }


    private void setStrategyId(CalculationStrategy calculationStrategy, UpdateLoanAppStrategiesCommand updateLoanAppStrategiesCommand) {
        if (calculationStrategy.getStrategyType().equals(StrategyType.EXTENSION.getType())) {
            updateLoanAppStrategiesCommand.setExtensionStrategyId(calculationStrategy.getId());
        } else if (calculationStrategy.getStrategyType().equals(StrategyType.PENALTY.getType())) {
            updateLoanAppStrategiesCommand.setPenaltyStrategyId(calculationStrategy.getId());
        } else if (calculationStrategy.getStrategyType().equals(StrategyType.INTEREST.getType())) {
            updateLoanAppStrategiesCommand.setInterestStrategyId(calculationStrategy.getId());
        } else if (calculationStrategy.getStrategyType().equals(StrategyType.FEE.getType())) {
            updateLoanAppStrategiesCommand.setFeeStrategyId(calculationStrategy.getId());
        }
    }


    private ActivityResult failHandler(ActivityContext context, String message) {
        if (checkMaxAttemptsExceeded(context)) {
            return ActivityResult.resolution(Resolutions.SKIP, REASON_DETAIL_ENGINE_UNRESPONSIVE);
        }
        return ActivityResult.resolution(Resolutions.FAIL, message);
    }
}


