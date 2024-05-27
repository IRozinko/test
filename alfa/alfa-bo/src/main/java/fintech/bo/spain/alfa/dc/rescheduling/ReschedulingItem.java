package fintech.bo.spain.alfa.dc.rescheduling;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

import static java.math.BigDecimal.ZERO;

@Data
public class ReschedulingItem {

    private LocalDate dueDate;

    private BigDecimal principal = ZERO;

    private BigDecimal interest = ZERO;

    private BigDecimal penalty = ZERO;

    private BigDecimal total = ZERO;

}
