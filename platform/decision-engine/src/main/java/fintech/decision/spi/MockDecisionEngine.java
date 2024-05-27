package fintech.decision.spi;

import fintech.decision.model.DecisionEngineRequest;
import fintech.decision.model.DecisionRequestStatus;
import fintech.decision.model.DecisionResult;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

import static fintech.BigDecimalUtils.amount;
import static fintech.decision.spi.DecisionEngineStrategy.INTEREST_RATE_SCENARIO;

@Component(MockDecisionEngine.NAME)
public class MockDecisionEngine implements DecisionEngine {

    public static final String NAME = "mock-decision-engine";
    public static final String DEFAULT = "default";

    private final Map<String, Supplier<DecisionResult>> responseMap = new HashMap<>();

    public MockDecisionEngine() {
        responseMap.put(DEFAULT, () -> new DecisionResult()
            .setDecision("success")
            .setStatus(DecisionRequestStatus.OK)
            .setRating("300.00")
            .setScore(BigDecimal.TEN)
            .setUsedFields(Collections.emptyList())
            .setVariablesResult(new HashMap<>())
        );

        responseMap.put(DecisionEngineStrategy.ID_VALIDATION_SCENARIO, () -> new DecisionResult()
            .setDecision("Valid")
            .setStatus(DecisionRequestStatus.OK)
            .setScore(BigDecimal.TEN)
            .setUsedFields(Collections.emptyList())
            .setVariablesResult(new HashMap<>())
        );

        responseMap.put(DecisionEngineStrategy.LOC_PTI_VALIDATION_SCENARIO, () -> new DecisionResult()
            .setDecision("APPROVED")
            .setStatus(DecisionRequestStatus.OK)
            .setScore(BigDecimal.ZERO)
            .setUsedFields(Collections.emptyList())
            .setVariablesResult(new HashMap<>())
        );

        responseMap.put(INTEREST_RATE_SCENARIO, () -> new DecisionResult()
            .setDecision("success")
            .setStatus(DecisionRequestStatus.OK)
            .setRating("Approved")
            .setScore(amount(35))
            .setUsedFields(Collections.emptyList())
            .setVariablesResult(new HashMap<>())
        );
    }

    @Override
    public DecisionResult getDecision(DecisionEngineRequest request) {
        if (responseMap.containsKey(request.getScenario())) {
            return responseMap.get(request.getScenario()).get();
        } else {
            return responseMap.get(DEFAULT).get();
        }
    }

    public void setResponseForScenario(String scenario, Supplier<DecisionResult> result) {
        responseMap.put(scenario, result);
    }

}
