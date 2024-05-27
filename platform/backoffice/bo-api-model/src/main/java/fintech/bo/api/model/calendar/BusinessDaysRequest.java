package fintech.bo.api.model.calendar;

import lombok.Data;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

@Data
@Accessors(chain = true)
public class BusinessDaysRequest {
    private LocalDateTime origin;
    private Integer amountToAdd;
    private ChronoUnit unit;
}
