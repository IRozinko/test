package fintech.web.api.models;

import lombok.Data;
import lombok.experimental.Accessors;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@Accessors(chain = true)
public class AmortizationPayment {
    private int number;
    private LocalDate date;
    private BigDecimal total = BigDecimal.ZERO;
    private BigDecimal principal = BigDecimal.ZERO;
    private BigDecimal interest = BigDecimal.ZERO;
    private BigDecimal remainingPrincipal = BigDecimal.ZERO;
    private LocalDate dueDate;
}
