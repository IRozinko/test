package fintech.decision.model;

import fintech.scoring.values.model.ScoringValue;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import org.hibernate.validator.constraints.NotEmpty;

import javax.validation.constraints.NotNull;
import java.util.List;

@Data
@Accessors(chain = true)
@NoArgsConstructor
public class DecisionEngineRequest {

    @NotEmpty
    private String scenario;

    @NotNull
    private Long clientId;

    @NotNull
    private Long scoringModelId;
    private List<ScoringValue> values;

    public DecisionEngineRequest(String scenario, Long clientId, Long scoringModelId) {
        this.scenario = scenario;
        this.clientId = clientId;
        this.scoringModelId = scoringModelId;
    }

    public DecisionEngineRequest(String scenario, Long clientId, Long scoringModelId, List<ScoringValue> values) {
        this.scenario = scenario;
        this.clientId = clientId;
        this.scoringModelId = scoringModelId;
        this.values = values;
    }
}
