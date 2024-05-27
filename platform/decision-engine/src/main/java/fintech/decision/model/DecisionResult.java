package fintech.decision.model;

import fintech.scoring.values.model.ScoringValue;
import lombok.Data;
import lombok.experimental.Accessors;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@Data
@Accessors(chain = true)
public class DecisionResult {

    private Long decisionEngineRequestId;
    private DecisionRequestStatus status;
    private String error;

    private List<String> arrayResult;
    private String decision;
    private String rating;
    private BigDecimal score;
    private String response;
    private Map<String, Object> variablesResult;
    private List<ScoringValue> usedFields;

    public static DecisionResult error(String error) {
        return new DecisionResult()
            .setStatus(DecisionRequestStatus.ERROR)
            .setError(error);
    }

}
