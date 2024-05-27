package fintech.bo.api.model.payments;

import lombok.Data;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@Accessors(chain = true)
public class AddExtensionTransactionRequest {

    @NotNull
    private Long paymentId;

    @NotNull
    private BigDecimal paymentAmount;

    @NotNull
    private Long loanId;

    @NotNull
    private BigDecimal overpaymentAmount;
    
    @NotNull
    private LocalDate valueDate;

    private String comments;
}
