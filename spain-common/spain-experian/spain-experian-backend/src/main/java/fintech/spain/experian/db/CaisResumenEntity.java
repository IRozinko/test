package fintech.spain.experian.db;


import fintech.db.BaseEntity;
import fintech.spain.experian.model.CaisResumenResponse;
import fintech.spain.experian.model.ExperianStatus;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;
import java.math.BigDecimal;

@Getter
@Setter
@ToString(callSuper = true)
@Entity
@Table(name = "cais_resumen", schema = Entities.SCHEMA, indexes = {
    @Index(columnList = "clientId", name = "idx_cais_resumen_client_id"),
})
@DynamicUpdate
public class CaisResumenEntity extends BaseEntity {

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
     * Total amount of Unpaid
     */
    private BigDecimal importeTotalImpagado;

    /**
     * Maximum unpaid amount.
     */
    private BigDecimal maximoImporteImpagado;

    /**
     * Total Number of unpaid installments (o.w. Number of total monthly payments)
     */
    private Integer numeroTotalCuotasImpagadas;

    /**
     * Number of total unpaid operations.
     */
    private Integer numeroTotalOperacionesImpagadas;

    /**
     * Worst payment status for the past 36 months
     */
    private String peorSituacionPago;

    /**
     * Worst payment status for the past 36 months code
     */
    private String peorSituacionPagoCodigo;

    /**
     * Worst-ever payment status.
     */
    private String peorSituacionPagoHistorica;

    /**
     * Worst-ever payment status code.
     */
    private String peorSituacionPagoHistoricaCodigo;

    /**
     * Province code
     */
    private String provinciaCodigo;

    public CaisResumenResponse toValueObject() {
        CaisResumenResponse val = new CaisResumenResponse();
        val.setId(this.id);
        val.setClientId(this.clientId);
        val.setApplicationId(this.applicationId);
        val.setDocumentNumber(this.documentNumber);
        val.setStatus(this.status);
        val.setError(this.error);
        val.setRequestBody(this.requestBody);
        val.setResponseBody(this.responseBody);
        val.setImporteTotalImpagado(this.importeTotalImpagado);
        val.setMaximoImporteImpagado(this.maximoImporteImpagado);
        val.setNumeroTotalCuotasImpagadas(this.numeroTotalCuotasImpagadas);
        val.setNumeroTotalOperacionesImpagadas(this.numeroTotalOperacionesImpagadas);
        val.setPeorSituacionPago(this.peorSituacionPago);
        val.setPeorSituacionPagoCodigo(this.peorSituacionPagoCodigo);
        val.setPeorSituacionPagoHistoricaCodigo(this.peorSituacionPagoHistoricaCodigo);
        val.setPeorSituacionPagoHistorica(this.peorSituacionPagoHistorica);
        val.setProvinciaCodigo(this.provinciaCodigo);
        val.setCreatedAt(this.createdAt);
        return val;
    }
}
