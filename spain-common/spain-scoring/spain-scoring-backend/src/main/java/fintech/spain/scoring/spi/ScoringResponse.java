package fintech.spain.scoring.spi;

import fintech.spain.scoring.model.ScoringRequestStatus;
import lombok.Data;
import lombok.experimental.Accessors;

import java.math.BigDecimal;

@Data
@Accessors(chain = true)
public class ScoringResponse {

    private Long decisionEngineRequestId;
    private ScoringRequestStatus status;
    private String error;
    private String responseBody;
    private int responseStatusCode;
    private BigDecimal score;
    private String rating;
    private String decision;


    public static ScoringResponse error(int responseCode, String responseBody, String error) {
        ScoringResponse result = new ScoringResponse();
        result.setStatus(ScoringRequestStatus.ERROR);
        result.setResponseBody(responseBody);
        result.setResponseStatusCode(responseCode);
        result.setError(error);
        result.setScore(BigDecimal.ONE.negate());
        return result;
    }

    public static ScoringResponse ok(int responseCode, String responseBody, BigDecimal score) {
        ScoringResponse result = new ScoringResponse();
        result.setStatus(ScoringRequestStatus.OK);
        result.setResponseBody(responseBody);
        result.setResponseStatusCode(responseCode);
        result.setScore(score);
        return result;
    }
}
