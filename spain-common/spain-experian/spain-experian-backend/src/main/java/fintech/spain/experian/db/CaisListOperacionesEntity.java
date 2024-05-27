package fintech.spain.experian.db;


import fintech.db.BaseEntity;
import fintech.spain.experian.model.CaisListOperacionesResponse;
import fintech.spain.experian.model.ExperianStatus;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;

@Getter
@Setter
@ToString(callSuper = true)
@Entity
@Table(name = "cais_operaciones", schema = Entities.SCHEMA, indexes = {
    @Index(columnList = "clientId", name = "idx_cais_operaciones_client_id"),
})
@DynamicUpdate
public class CaisListOperacionesEntity extends BaseEntity {

    private Long clientId;
    private Long applicationId;

    @Column(nullable = false)
    private String documentNumber;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private ExperianStatus status;

    private String error;

    private String responseBody;

    private String requestBody;

    /**
     * Number of registered Repayment
     */
    private Integer numeroRegistrosDevueltos;

    public CaisListOperacionesResponse toValueObject() {
        CaisListOperacionesResponse val = new CaisListOperacionesResponse();
        val.setId(this.id);
        val.setClientId(this.clientId);
        val.setApplicationId(this.applicationId);
        val.setDocumentNumber(this.documentNumber);
        val.setStatus(this.status);
        val.setError(this.error);
        val.setRequestBody(this.requestBody);
        val.setResponseBody(this.responseBody);
        val.setNumeroRegistrosDevueltos(this.numeroRegistrosDevueltos);
        val.setCreatedAt(this.createdAt);
        return val;
    }
}
