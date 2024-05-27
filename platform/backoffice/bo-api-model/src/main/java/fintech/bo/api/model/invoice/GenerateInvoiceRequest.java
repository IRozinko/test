package fintech.bo.api.model.invoice;

import lombok.AllArgsConstructor;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.time.LocalDate;

@Data
@AllArgsConstructor
public class GenerateInvoiceRequest {

    @NotNull
    private Long loanId;

    private LocalDate dateTo = LocalDate.now();

}
