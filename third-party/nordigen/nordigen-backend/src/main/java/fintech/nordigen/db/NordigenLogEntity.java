package fintech.nordigen.db;

import fintech.JsonUtils;
import fintech.db.BaseEntity;
import fintech.nordigen.json.NordigenJson;
import fintech.nordigen.model.NordigenResult;
import fintech.nordigen.model.NordigenStatus;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;
import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@ToString(callSuper = true, exclude = {"requestBody", "responseBody"})
@Table(name = "log", schema = Entities.SCHEMA, indexes = {
    @Index(columnList = "clientId", name = "idx_log_client_id"),
})
public class NordigenLogEntity extends BaseEntity {

    private Long clientId;
    private Long applicationId;
    private Long loanId;
    private Long instantorResponseId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private NordigenStatus status;

    private String requestBody;
    private String responseBody;
    private int responseStatusCode;
    private String error;

    @Column(nullable = false)
    private LocalDateTime requestedAt;

    public NordigenResult toValueObject() {
        NordigenResult val = new NordigenResult();
        val.setId(this.id);
        val.setClientId(this.clientId);
        val.setApplicationId(this.applicationId);
        val.setLoanId(this.loanId);
        val.setStatus(this.status);
        val.setError(this.error);
        val.setResponseBody(this.responseBody);
        val.setInstantorResponseId(this.instantorResponseId);
        if (this.status == NordigenStatus.OK) {
            NordigenJson json = JsonUtils.readValue(this.responseBody, NordigenJson.class);
            val.setJson(json);
        }
        return val;
    }
}
