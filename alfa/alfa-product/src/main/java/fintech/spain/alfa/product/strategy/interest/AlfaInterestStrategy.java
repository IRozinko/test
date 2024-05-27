package fintech.spain.alfa.product.strategy.interest;

import fintech.lending.core.application.LoanApplication;
import fintech.lending.core.application.LoanApplicationService;
import fintech.scoring.values.ScoringValuesService;
import fintech.scoring.values.model.ScoringModel;
import fintech.spain.alfa.product.scoring.ScoringRequestAttributes;
import fintech.spain.alfa.product.workflow.WorkflowAttributes;
import fintech.spain.scoring.SpainScoringService;
import fintech.spain.scoring.model.ScoringQuery;
import fintech.spain.scoring.model.ScoringRequestCommand;
import fintech.spain.scoring.model.ScoringRequestStatus;
import fintech.spain.scoring.model.ScoringResult;
import fintech.spain.alfa.strategy.CalculationType;
import fintech.spain.alfa.strategy.interest.MonthlyInterestStrategyProperties;
import fintech.strategy.spi.InterestStrategy;
import fintech.workflow.Activity;
import fintech.workflow.Workflow;
import fintech.workflow.WorkflowService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static fintech.BigDecimalUtils.amount;
import static fintech.spain.scoring.model.ScoringModelType.INTEREST_RATE_MODEL;

@Component
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
public class AlfaInterestStrategy implements InterestStrategy {

    public static final CalculationType CALCULATION_TYPE = CalculationType.X;

    @Autowired
    private LoanApplicationService loanApplicationService;

    @Autowired
    private SpainScoringService scoringService;

    @Autowired
    private ScoringValuesService scoringValuesService;

    @Autowired
    private WorkflowService workflowService;

    private final MonthlyInterestStrategyProperties monthlyInterestStrategyProperties;

    public AlfaInterestStrategy(MonthlyInterestStrategyProperties properties) {
        this.monthlyInterestStrategyProperties = properties;
    }

    @Override
    public BigDecimal calculateInterest(BigDecimal principal, Long termInDays, Optional<Long> loanApplicationId) {
        BigDecimal defaultMonthlyInterestRate = monthlyInterestStrategyProperties.getMonthlyInterestRate();
        String scenario = monthlyInterestStrategyProperties.getScenario();
        BigDecimal monthlyInterestRate = loanApplicationId.map(id -> {
            LoanApplication loanApplication = loanApplicationService.get(id);
            BigDecimal scoringMonthlyInterestRate = null;
            if (monthlyInterestStrategyProperties.isUsingDecisionEngine() && loanApplication.getWorkflowId() != null) {
                Optional<ScoringResult> logResult = scoringService.findLatest(ScoringQuery.byApplicationIdOk(INTEREST_RATE_MODEL, loanApplication.getId()));
                scoringMonthlyInterestRate = logResult.filter(result -> result.getScenario() != null && result.getScenario().equals(scenario))
                    .map(ScoringResult::getScore)
                    .orElseGet(() -> {
                        ScoringResult scoringResult = requestScoreInterestRate(loanApplication, scenario);
                        if (scoringResult.getStatus() != ScoringRequestStatus.OK) {
                            return defaultMonthlyInterestRate;
                        }  else {
                            BigDecimal score = scoringResult.getScore();
                            workflowService.setAttribute(loanApplication.getWorkflowId(), WorkflowAttributes.SCORING_INTEREST_RATE, score.toString());
                            return score;
                        }
                    });
            }
            return scoringMonthlyInterestRate;
        }).orElse(defaultMonthlyInterestRate);
        return principal
            .multiply(amount(termInDays))
            .divide(amount(30), 8, BigDecimal.ROUND_HALF_UP)
            .multiply(monthlyInterestRate.divide(amount(100), 8, BigDecimal.ROUND_HALF_UP))
            .setScale(0, BigDecimal.ROUND_HALF_UP);
    }

    private ScoringResult requestScoreInterestRate(LoanApplication loanApplication, String scenario) {
        Workflow workflow = workflowService.getWorkflow(loanApplication.getWorkflowId());
        ScoringModel scoringModel = scoringValuesService.collectValues(loanApplication.getClientId());

        ScoringRequestCommand command = new ScoringRequestCommand();
        command.setType(INTEREST_RATE_MODEL);
        command.setApplicationId(loanApplication.getId());
        command.setClientId(loanApplication.getClientId());
        command.setScenarioKey(scenario);
        command.setScoreModelId(scoringModel.getId());

        Map<String, Object> attributes = new HashMap<>();
        attributes.put(ScoringRequestAttributes.SCENARIO, scenario);
        attributes.put(ScoringRequestAttributes.APPLICATION_ID, loanApplication.getId());
        attributes.put(ScoringRequestAttributes.CLIENT_ID, command.getClientId());
        attributes.put(ScoringRequestAttributes.MODEL_ID, scoringModel.getId());
        attributes.put(ScoringRequestAttributes.WORKFLOW_ID, workflow.getId());
        Optional<String> currentActivity = workflow.getCurrentActivity().map(Activity::getName);
        currentActivity.ifPresent(name -> attributes.put(ScoringRequestAttributes.WORKFLOW_ACTIVITY, name));
        command.setAttributes(attributes);
        return scoringService.requestScore(command);
    }
}
