package fintech.decision;

import fintech.decision.model.DecisionEngineRequest;
import fintech.decision.model.DecisionResult;

public interface DecisionEngineService {
    DecisionResult getDecision(DecisionEngineRequest request);

    DecisionResult getStrategies(DecisionEngineRequest request);
}
