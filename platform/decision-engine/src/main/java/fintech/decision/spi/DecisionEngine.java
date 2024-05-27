package fintech.decision.spi;

import fintech.decision.model.DecisionEngineRequest;
import fintech.decision.model.DecisionResult;

public interface DecisionEngine {

    DecisionResult getDecision(DecisionEngineRequest request);

}
