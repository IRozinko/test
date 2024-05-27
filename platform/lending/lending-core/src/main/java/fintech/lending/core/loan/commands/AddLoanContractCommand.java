package fintech.lending.core.loan.commands;

import fintech.lending.core.PeriodUnit;
import fintech.transactions.TransactionType;
import lombok.Data;
import lombok.experimental.Accessors;

import java.time.LocalDate;

@Data
@Accessors(chain = true)
public class AddLoanContractCommand {

    private Long loanId;

    private Long productId;

    private Long clientId;

    private Long applicationId;

    private LocalDate contractDate;

    private LocalDate effectiveDate;

    private LocalDate maturityDate;

    private Long periodCount = 0L;

    private PeriodUnit periodUnit = PeriodUnit.NA;

    private Long numberOfInstallments = 0L;

    private boolean closeLoanOnPaid;

    private int baseOverdueDays = 0;

    private Long sourceTransactionId;

    private TransactionType sourceTransactionType;

}
