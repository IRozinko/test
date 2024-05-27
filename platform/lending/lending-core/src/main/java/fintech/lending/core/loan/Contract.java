package fintech.lending.core.loan;

import fintech.lending.core.PeriodUnit;
import fintech.transactions.TransactionType;
import lombok.Data;
import lombok.experimental.Accessors;

import java.time.LocalDate;

@Data
@Accessors(chain = true)
public class Contract {

    private Long id;
    private Long productId;
    private Long loanId;
    private Long clientId;
    private Long applicationId;
    private LocalDate contractDate;
    private LocalDate activeFrom;
    private LocalDate effectiveDate;
    private LocalDate maturityDate;
    private boolean current;
    private Long periodCount = 0L;
    private PeriodUnit periodUnit = PeriodUnit.NA;
    private Long numberOfInstallments = 0L;
    private boolean closeLoanOnPaid;
    private int baseOverdueDays;
    private Long previousContractId;
    private Long sourceTransactionId;
    private TransactionType sourceTransactionType;

}
