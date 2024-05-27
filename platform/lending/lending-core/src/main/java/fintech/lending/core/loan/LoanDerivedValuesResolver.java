package fintech.lending.core.loan;

import fintech.transactions.Balance;

import java.time.LocalDate;

public interface LoanDerivedValuesResolver {

    void resolveDerivedValues(Long loanId);

    void resolveDerivedValues(Long loanId, String state, String status, LocalDate when);

    void validateLoanBalance(Long loanId, Balance balance);

}
