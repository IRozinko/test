package fintech.spain.experian.db;


import fintech.db.BaseEntity;
import fintech.spain.experian.model.CaisDebt;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
@ToString(callSuper = true)
@Entity
@Table(name = "cais_debt", schema = Entities.SCHEMA, indexes = {
    @Index(columnList = "clientId", name = "idx_cais_debt_client_id"),
})
@DynamicUpdate
public class CaisDebtEntity extends BaseEntity {

    private Long clientId;

    private Long applicationId;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "operaciones_response_id")
    private CaisListOperacionesEntity operaciones;

    private String idOperacion;
    private String tipoProductoFinanciadoCodigo;
    private String tipoProductoFinanciadoDescription;
    private String situacionPagoCodigo;
    private String situacionPagoDescription;
    @Column(nullable = false)
    private BigDecimal saldoImpagado;
    private String tipoIntervinienteCodigo;
    private String tipoIntervinienteDescription;
    private Integer numeroCuotasImpagadas;
    private BigDecimal importeCuota;
    private String frecuenciaPagoCodigo;
    private String frecuenciaPagoDescription;
    private LocalDate fechaInicio;
    private LocalDate fechaFin;
    private String informante;

    public CaisDebt toValueObject() {
        CaisDebt vo = new CaisDebt();
        vo.setId(this.id);
        vo.setTipoProductoFinanciadoCodigo(this.tipoProductoFinanciadoCodigo);
        vo.setTipoProductoFinanciadoDescription(this.tipoProductoFinanciadoDescription);
        vo.setSaldoImpagado(this.saldoImpagado);
        vo.setFechaInicio(this.fechaInicio);
        vo.setFechaFin(this.fechaFin);
        vo.setSituacionPagoCodigo(this.situacionPagoCodigo);
        vo.setSituacionPagoDescription(this.situacionPagoDescription);
        return vo;
    }
}
