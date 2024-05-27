package fintech.bo.api.model.accounting;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class AccountTrialBalance {

    private String accountCode;
    private String accountName;
    private BigDecimal openingDebit;
    private BigDecimal openingCredit;
    private BigDecimal turnoverDebit;
    private BigDecimal turnoverCredit;
    private BigDecimal closingDebit;
    private BigDecimal closingCredit;
}
