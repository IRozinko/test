package fintech.viventor.db;

import fintech.db.BaseEntity;
import fintech.viventor.ViventorLog;
import fintech.viventor.ViventorRequestType;
import fintech.viventor.ViventorResponseStatus;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;

@Getter
@Setter
@Entity
@ToString(callSuper = true, exclude = {"requestBody", "responseBody"})
@Table(name = "log", schema = Entities.SCHEMA, indexes = {
    @Index(columnList = "loanId", name = "idx_log_loan_id")
})
public class ViventorLogEntity extends BaseEntity {

    private Long loanId;

    private String viventorLoanId;

    @Enumerated(EnumType.STRING)
    private ViventorRequestType requestType;

    private String requestUrl;

    private String requestBody;

    private String responseBody;

    private int responseStatusCode;

    @Enumerated(EnumType.STRING)
    private ViventorResponseStatus status;

    public ViventorLog toValueObject() {
        ViventorLog val = new ViventorLog();
        val.setId(id);
        val.setLoanId(loanId);
        val.setRequestType(requestType);
        val.setRequestBody(requestBody);
        val.setResponseBody(responseBody);
        val.setResponseStatusCode(responseStatusCode);
        val.setStatus(status);
        return val;
    }

}
