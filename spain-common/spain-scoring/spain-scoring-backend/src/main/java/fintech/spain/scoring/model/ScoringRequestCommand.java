package fintech.spain.scoring.model;

import lombok.Data;
import lombok.experimental.Accessors;

import java.util.HashMap;
import java.util.Map;

@Data
@Accessors(chain = true)
public class ScoringRequestCommand {

    private ScoringModelType type;
    private Long scoreModelId;
    private String scenarioKey;
    private Long clientId;
    private Long applicationId;
    private Long loanId;

    private Map<String, Object> attributes = new HashMap<>();
}
