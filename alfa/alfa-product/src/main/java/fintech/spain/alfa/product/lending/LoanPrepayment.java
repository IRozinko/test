package fintech.spain.alfa.product.lending;

import lombok.Data;
import lombok.experimental.Accessors;

import java.math.BigDecimal;
import java.time.LocalDate;

import static fintech.BigDecimalUtils.amount;

@Data
@Accessors(chain = true)
public class LoanPrepayment {

    private LocalDate date;
    private boolean prepaymentAvailable = false;
    private BigDecimal principalToPay = amount(0);
    private BigDecimal interestToPay = amount(0);
    private BigDecimal interestToWriteOff= amount(0);
    private BigDecimal prepaymentFeeToPay = amount(0);
    private BigDecimal totalToPay = amount(0);

    public static LoanPrepayment notAvailable(LocalDate date) {
        return new LoanPrepayment().setDate(date);
    }
}
