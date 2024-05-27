package fintech.spain.scoring.impl;

import fintech.decision.DecisionEngineService;
import fintech.decision.model.DecisionEngineRequest;
import fintech.decision.model.DecisionRequestStatus;
import fintech.decision.model.DecisionResult;
import fintech.spain.scoring.model.ScoringRequestCommand;
import fintech.spain.scoring.spi.ScoringResponse;
import fintech.spain.scoring.spi.SpainScoringProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component(FintechMarketScoringProvider.NAME)
@RequiredArgsConstructor
public class FintechMarketScoringProvider implements SpainScoringProvider {

    public static final String NAME = "fintechmarket-scoring-provider";

    private final DecisionEngineService decisionEngineService;

    @Override
    public ScoringResponse request(ScoringRequestCommand command) {
        DecisionEngineRequest request = new DecisionEngineRequest(command.getScenarioKey(),
            command.getClientId(), command.getScoreModelId());

        DecisionResult decision = decisionEngineService.getDecision(request);

        if (decision.getStatus() == DecisionRequestStatus.OK) {
            return ScoringResponse.ok(200, decision.getResponse(), decision.getScore())
                .setDecisionEngineRequestId(decision.getDecisionEngineRequestId())
                .setDecision(decision.getDecision())
                .setRating(decision.getRating());
        } else {
            return ScoringResponse.error(400, decision.getResponse(), decision.getError());
        }
    }
}
