package fintech.spain.crosscheck.db;

import fintech.db.BaseEntity;
import fintech.spain.crosscheck.model.SpainCrosscheckResult;
import fintech.spain.crosscheck.model.SpainCrosscheckStatus;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Getter
@Setter
@Entity
@Table(name = "log", schema = Entities.SCHEMA, indexes = {
    @Index(columnList = "clientId", name = "idx_log_client_id"),
    @Index(columnList = "applicationId", name = "idx_log_application_id"),
})
public class SpainCrosscheckLogEntity extends BaseEntity {

    private String dni;
    private Long clientId;
    private Long loanId;
    private Long applicationId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private SpainCrosscheckStatus status;

    @Column(nullable = false)
    private Long maxDpd = 0L;

    @Column(nullable = false)
    private Long openLoans = 0L;

    @Column(nullable = false)
    private boolean blacklisted;

    @Column(nullable = false)
    private boolean repeatedClient;

    @Column(nullable = false)
    private boolean activeRequest;
    private String activeRequestStatus;

    private String responseBody;
    private int responseStatusCode;
    private String error;

    public SpainCrosscheckResult toValueObject() {
        SpainCrosscheckResult val = new SpainCrosscheckResult();
        val.setId(this.id);
        val.setDni(this.dni);
        val.setClientId(this.clientId);
        val.setLoanId(this.loanId);
        val.setApplicationId(this.applicationId);
        val.setStatus(this.status);
        val.setMaxDpd(this.maxDpd);
        val.setOpenLoans(this.openLoans);
        val.setBlacklisted(this.blacklisted);
        val.setRepeatedClient(this.repeatedClient);
        val.setActiveRequest(this.activeRequest);
        val.setActiveRequestStatus(this.activeRequestStatus);
        val.setResponseBody(this.responseBody);
        val.setError(this.error);
        return val;
    }
}
