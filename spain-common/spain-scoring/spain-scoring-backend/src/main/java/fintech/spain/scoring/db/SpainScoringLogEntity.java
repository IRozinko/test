package fintech.spain.scoring.db;

import com.google.common.base.Throwables;
import fintech.JsonUtils;
import fintech.db.BaseEntity;
import fintech.spain.scoring.model.ScoringModelType;
import fintech.spain.scoring.model.ScoringRequestCommand;
import fintech.spain.scoring.model.ScoringRequestStatus;
import fintech.spain.scoring.model.ScoringResult;
import fintech.spain.scoring.spi.ScoringResponse;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Index;
import javax.persistence.Table;
import java.math.BigDecimal;

@Getter
@Setter
@Entity
@Table(name = "log", schema = Entities.SCHEMA, indexes = {
    @Index(columnList = "clientId", name = "idx_log_client_id"),
})
@NoArgsConstructor
public class SpainScoringLogEntity extends BaseEntity {

    private Long clientId;
    private Long loanId;
    private Long applicationId;
    private Long decisionEngineRequestId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ScoringModelType type;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ScoringRequestStatus status;

    private String requestAttributes;
    private String responseBody;
    private int responseStatusCode;
    private String error;

    private String decision;
    private String rating;

    @Column(nullable = false)
    private BigDecimal score = BigDecimal.ONE.negate();

    public SpainScoringLogEntity(ScoringRequestCommand command) {
        clientId = command.getClientId();
        loanId = command.getLoanId();
        applicationId = command.getApplicationId();
        type = command.getType();
        requestAttributes = JsonUtils.writeValueAsString(command.getAttributes());
    }

    public void setResponse(ScoringResponse response) {
        decisionEngineRequestId = response.getDecisionEngineRequestId();
        status = response.getStatus();
        responseBody = response.getResponseBody();
        error = response.getError();
        responseStatusCode = response.getResponseStatusCode();
        score = response.getScore();
        decision = response.getDecision();
        rating = response.getRating();
    }

    public void setError(Exception e) {
        status = ScoringRequestStatus.ERROR;
        responseStatusCode = -1;
        error = Throwables.getRootCause(e).getMessage();
        score = BigDecimal.ONE.negate();
    }

    public ScoringResult toScoringResult() {
        ScoringResult result = new ScoringResult();
        result.setDecisionEngineRequestId(decisionEngineRequestId);
        result.setId(this.id);
        result.setClientId(this.clientId);
        result.setLoanId(this.loanId);
        result.setApplicationId(this.applicationId);
        result.setScore(this.score);
        result.setStatus(this.status);
        result.setType(this.type);
        result.setError(this.error);
        result.setResponseBody(this.responseBody);
        result.setRequestAttributes(requestAttributes);
        result.setRating(rating);
        result.setDecision(decision);
        return result;
    }
}
