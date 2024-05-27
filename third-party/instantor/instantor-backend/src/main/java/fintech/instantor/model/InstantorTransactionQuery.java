package fintech.instantor.model;

import lombok.Data;
import lombok.experimental.Accessors;

import java.time.LocalDate;

@Data
@Accessors(chain = true)
public class InstantorTransactionQuery {

    private Long responseId;
    private Long clientId;
    private LocalDate dateFrom;
    private LocalDate dateTo;
    private String accountNumber;
}
