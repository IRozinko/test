package fintech.spain.alfa.product.lending;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class LoanIssueResult {

    private Long loanId;
    private Long disbursementId;
}
