package fintech.spain.alfa.bo.model;

import lombok.Data;
import lombok.experimental.Accessors;

import java.math.BigDecimal;

@Data
@Accessors(chain = true)
public class UpdateLoanUpsellAmountRequest {

    private Long loanApplicationId;

    private BigDecimal amount;

    private Long discountId;
}
