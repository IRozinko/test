package fintech.bo.api.model.loan;

import lombok.Data;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDate;

@Accessors(chain = true)
@Data
public class WriteOffLoanAmountRequest {

    @NotNull
    private Long loanId;

    @NotNull
    private BigDecimal principal = BigDecimal.ZERO;

    @NotNull
    private BigDecimal interest = BigDecimal.ZERO;

    @NotNull
    private BigDecimal penalty = BigDecimal.ZERO;

    @NotNull
    private BigDecimal fee = BigDecimal.ZERO;

    @NotNull
    private LocalDate when;

    private String comment;

    private String subType;
}
