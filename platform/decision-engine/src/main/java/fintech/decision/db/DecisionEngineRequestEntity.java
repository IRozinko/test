package fintech.decision.db;

import fintech.JsonUtils;
import fintech.db.BaseEntity;
import fintech.decision.model.DecisionRequestStatus;
import fintech.decision.model.DecisionResult;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Table;
import java.math.BigDecimal;


@Getter
@Setter
@Accessors(chain = true)
@Entity
@Table(name = "request", schema = Entities.SCHEMA)
public class DecisionEngineRequestEntity extends BaseEntity {

    @Column(nullable = false)
    private String scenario;

    @Column(nullable = false)
    private Long clientId;

    @Column(nullable = false)
    private Long scoringModelId;

    @Enumerated(EnumType.STRING)
    private DecisionRequestStatus status;
    private String error;
    private String response;

    private String decision;
    private String rating;
    private BigDecimal score;
    private String variablesResult;


    public DecisionEngineRequestEntity setResult(DecisionResult result) {
        status = result.getStatus();
        error = result.getError();
        response = result.getResponse();
        decision = result.getDecision();
        rating = result.getRating();
        score = result.getScore();
        variablesResult = JsonUtils.writeValueAsString(result.getVariablesResult());
        return this;
    }


}
