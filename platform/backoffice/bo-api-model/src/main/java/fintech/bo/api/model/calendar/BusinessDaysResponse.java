package fintech.bo.api.model.calendar;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NonNull;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class BusinessDaysResponse {
    @NonNull
    private LocalDateTime businessTime;
}
