package fintech.bo.api.model.payments;

import lombok.Data;

import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class AddRepaymentTransactionRequest {

    @NotNull
    private Long paymentId;

    @NotNull
    private BigDecimal paymentAmount;

    @NotNull
    private Long loanId;

    @NotNull
    private BigDecimal overpaymentAmount;

    private String comments;

    @NotNull
    private LocalDate valueDate;
}
