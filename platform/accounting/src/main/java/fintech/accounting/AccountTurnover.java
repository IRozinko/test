package fintech.accounting;

import lombok.Data;

import java.math.BigDecimal;

import static fintech.BigDecimalUtils.amount;

@Data
public class AccountTurnover {

    private String code;
    private BigDecimal debit = amount(0);
    private BigDecimal credit = amount(0);
}
