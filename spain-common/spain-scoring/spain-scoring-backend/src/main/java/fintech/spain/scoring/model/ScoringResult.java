package fintech.spain.scoring.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.Accessors;

import java.math.BigDecimal;

@Data
@Accessors(chain = true)
@NoArgsConstructor
@ToString(of = {"id", "clientId", "applicationId", "score", "error"})
public class ScoringResult {

    private Long id;
    private Long decisionEngineRequestId;
    private Long clientId;
    private Long applicationId;
    private Long loanId;

    private ScoringModelType type;
    private String scenario;
    private ScoringRequestStatus status;
    private String error;
    private String responseBody;
    private String requestAttributes;
    private BigDecimal score;
    private String decision;
    private String rating;

}
