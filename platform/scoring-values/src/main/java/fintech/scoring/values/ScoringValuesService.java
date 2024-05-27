package fintech.scoring.values;

import fintech.scoring.values.model.ScoringModel;
import fintech.scoring.values.model.ScoringValue;

import java.util.List;
import java.util.Map;

public interface ScoringValuesService {

    ScoringModel collectValues(long clientId);

    List<ScoringValue> getValues(long scoringModelId);

    Map<String, Object> getValuesAsMap(long scoringModelId);

    ScoringModel saveValues(long scoringModelId, Map<String, Object> values);

}
