package fintech.spain.alfa.product.cms;

import lombok.Data;
import lombok.experimental.Accessors;

import java.math.BigDecimal;
import java.time.LocalDate;

import static fintech.BigDecimalUtils.amount;

@Data
@Accessors(chain = true)
public class ClientRepaymentModel {

    private BigDecimal principalPaid = amount(0);
    private BigDecimal interestPaid = amount(0);
    private BigDecimal penaltyPaid = amount(0);
    private BigDecimal extensionFeePaid = amount(0);
    private BigDecimal prepaymentFeePaid = amount(0);
    private BigDecimal reschedulingFeePaid = amount(0);
    private BigDecimal totalInvoiced = amount(0);
    private BigDecimal totalPaid = amount(0);
    private LocalDate repaymentDate;
    private Long transactionId;
}
