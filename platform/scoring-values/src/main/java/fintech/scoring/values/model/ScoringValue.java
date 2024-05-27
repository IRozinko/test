package fintech.scoring.values.model;

import fintech.scoring.values.db.ScoringValueSource;
import lombok.Value;

@Value
public class ScoringValue {

    private String key;
    private Object value;
    private ScoringValueSource source;

}
