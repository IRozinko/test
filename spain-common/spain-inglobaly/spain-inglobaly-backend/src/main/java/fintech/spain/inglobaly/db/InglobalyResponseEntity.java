package fintech.spain.inglobaly.db;

import fintech.db.BaseEntity;
import fintech.spain.inglobaly.model.InglobalyResponse;
import fintech.spain.inglobaly.model.InglobalyStatus;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.OptimisticLockType;
import org.hibernate.annotations.OptimisticLocking;

import javax.persistence.*;
import java.time.LocalDate;

@Getter
@Setter
@Entity
@Table(name = "response", schema = Entities.SCHEMA, indexes = {
    @Index(columnList = "clientId", name = "idx_response_client_id"),
})
@OptimisticLocking(type = OptimisticLockType.NONE)
@DynamicUpdate
public class InglobalyResponseEntity extends BaseEntity {

    private Long clientId;
    private Long applicationId;
    private String requestedDocumentNumber;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private InglobalyStatus status;

    private String error;

    private String responseBody;

    @Column(columnDefinition = "DATE")
    private LocalDate dateOfBirth;

    private String firstName;
    private String lastName;
    private String secondLastName;

    public InglobalyResponse toValueObject() {
        InglobalyResponse val = new InglobalyResponse();
        val.setId(this.id);
        val.setClientId(this.clientId);
        val.setApplicationId(this.applicationId);
        val.setRequestedDocumentNumber(this.requestedDocumentNumber);
        val.setStatus(this.status);
        val.setError(this.error);
        val.setDateOfBirth(this.dateOfBirth);
        val.setFirstName(this.firstName);
        val.setLastName(this.lastName);
        val.setSecondLastName(this.secondLastName);
        val.setResponseBody(this.responseBody);
        return val;
    }
}
