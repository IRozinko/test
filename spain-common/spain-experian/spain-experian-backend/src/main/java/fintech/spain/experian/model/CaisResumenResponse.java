package fintech.spain.experian.model;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class CaisResumenResponse {

    private Long id;
    private Long clientId;
    private Long applicationId;
    private String documentNumber;
    private ExperianStatus status;
    private String error;
    private String requestBody;
    private String responseBody;
    private LocalDateTime createdAt;

    private BigDecimal importeTotalImpagado;
    private BigDecimal maximoImporteImpagado;
    private Integer numeroTotalCuotasImpagadas;
    private Integer numeroTotalOperacionesImpagadas;
    private String peorSituacionPago;
    private String peorSituacionPagoCodigo;
    private String peorSituacionPagoHistorica;
    private String peorSituacionPagoHistoricaCodigo;
    private String provinciaCodigo;
}
