package fintech.bo.api.model.loan;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class GetExtensionPricesRequest {

    private Long loanId;
    private LocalDate date;
}
