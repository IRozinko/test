package fintech.spain.alfa.product.lending.impl;

import fintech.BigDecimalUtils;
import fintech.decision.DecisionEngineService;
import fintech.decision.model.DecisionEngineRequest;
import fintech.decision.model.DecisionRequestStatus;
import fintech.decision.model.DecisionResult;
import fintech.scoring.values.ScoringValuesService;
import fintech.scoring.values.model.ScoringModel;
import fintech.settings.SettingsService;
import fintech.spain.alfa.product.lending.CalculateCreditLimitCommand;
import fintech.spain.alfa.product.lending.CreditLimitCalculator;
import fintech.spain.alfa.product.settings.AlfaSettings;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Optional;

import static fintech.BigDecimalUtils.min;


@Slf4j
@Service
@RequiredArgsConstructor
public class CreditLimitCalculatorBean implements CreditLimitCalculator {

    private final DecisionEngineService decisionEngineService;
    private final ScoringValuesService scoringValuesService;
    private final SettingsService settingsService;

    @Override
    public BigDecimal calculateCreditLimit(CalculateCreditLimitCommand command) {
        AlfaSettings.CreditLimitSettings settings = settingsService.getJson(AlfaSettings.LENDING_RULES_BASIC, AlfaSettings.CreditLimitSettings.class);

        BigDecimal calculatedCreditLimit = getCreditLimitUsingDecisionEngine(command, settings)
            .orElse(Optional.ofNullable(command.getLastCreditLimit())
                .orElse(settings.getDefaultCreditLimit()));

        BigDecimal creditLimit = min(calculatedCreditLimit, settings.getMaxCreditLimit())
            .setScale(-1, RoundingMode.HALF_UP);

        log.info("Calculated credit limit {} for client with id {}", creditLimit, command.getClientId());
        return creditLimit;
    }

    private Optional<BigDecimal> getCreditLimitUsingDecisionEngine(CalculateCreditLimitCommand command,
                                                                   AlfaSettings.CreditLimitSettings settings) {
        ScoringModel scoringModel = scoringValuesService.collectValues(command.getClientId());
        DecisionResult decision = decisionEngineService.getDecision(
            new DecisionEngineRequest(settings.getScenario(), command.getClientId(), scoringModel.getId()));

        if (decision.getStatus() == DecisionRequestStatus.OK)
            return Optional.of(BigDecimalUtils.amount(decision.getRating()));

        return Optional.empty();
    }

}
