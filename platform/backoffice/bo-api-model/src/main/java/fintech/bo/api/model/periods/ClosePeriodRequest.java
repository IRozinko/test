package fintech.bo.api.model.periods;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NonNull;

import java.time.LocalDate;

@Data
@AllArgsConstructor
public class ClosePeriodRequest {

    @NonNull
    private LocalDate periodDate;

}
