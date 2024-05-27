package fintech.bo.api.model.loan;

import lombok.AllArgsConstructor;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

@Data
@AllArgsConstructor
public class UseOverpaymentRequest {

    @NotNull
    private Long loanId;

    @NotNull
    private BigDecimal amount;

    private String comments;

}
