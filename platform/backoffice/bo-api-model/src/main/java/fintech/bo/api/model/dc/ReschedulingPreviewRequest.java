package fintech.bo.api.model.dc;

import lombok.Data;
import lombok.experimental.Accessors;

import java.time.LocalDate;

@Data
@Accessors(chain = true)
public class ReschedulingPreviewRequest {

    private Long loanId;
    private int numberOfPayments;
    private LocalDate when;
}
