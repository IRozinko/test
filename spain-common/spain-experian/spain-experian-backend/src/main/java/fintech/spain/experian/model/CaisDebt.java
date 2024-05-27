package fintech.spain.experian.model;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class CaisDebt {

    private Long id;
    private String tipoProductoFinanciadoCodigo;
    private String tipoProductoFinanciadoDescription;
    private BigDecimal saldoImpagado;
    private LocalDate fechaInicio;
    private LocalDate fechaFin;
    private String situacionPagoCodigo;
    private String situacionPagoDescription;
}
