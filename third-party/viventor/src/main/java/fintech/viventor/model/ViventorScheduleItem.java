package fintech.viventor.model;

import fintech.viventor.ViventorScheduleItemStatus;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class ViventorScheduleItem {

    private int number;

    private LocalDate date;

    private BigDecimal total;

    private BigDecimal principal;

    private BigDecimal interest;

    private BigDecimal remainingPrincipal;

    private ViventorScheduleItemStatus status;
}
