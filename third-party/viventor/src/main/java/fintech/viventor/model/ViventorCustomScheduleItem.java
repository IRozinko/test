package fintech.viventor.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class ViventorCustomScheduleItem {

    private int number;

    private LocalDate date;

    private BigDecimal total;

    private BigDecimal principal;

    private BigDecimal interest;

    @JsonProperty("remaining_principal")
    private BigDecimal remainingPrincipal;

}
