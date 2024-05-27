package fintech.lending.core.loan;

import fintech.filestorage.CloudFile;
import fintech.lending.core.invoice.commands.GenerateInvoiceCommand;
import fintech.lending.core.loan.commands.*;
import fintech.transactions.Balance;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface LoanService {

    Loan getLoan(Long loanId);

    List<Loan> findLoans(LoanQuery query);

    Optional<Loan> findLastLoan(LoanQuery query);

    Optional<Loan> findLastLoanByIssueDate(LoanQuery query);

    Optional<Loan> findLoanByNumber(String loanNumber);

    Long issueLoan(IssueLoanCommand command);

    Long issueLoan(CreateLoanCommand command);
    void updateStrategies(UpdateStrategiesCommand command);
    void withdraw(WithdrawCommand command);

    void voidLoan(VoidLoanCommand command);

    List<Long> repayLoan(RepayLoanCommand command);

    Long disburseLoan(DisburseLoanCommand command);

    Long applyInterest(ApplyInterestCommand command);

    Long applyPenalty(ApplyPenaltyCommand command);

    Long applyFee(ApplyFeeCommand command);

    Long settleDisbursement(SettleDisbursementCommand command);

    Long generateInvoice(GenerateInvoiceCommand command);

    void breakLoan(BreakLoanCommand command);

    void unBreakLoan(UnBreakLoanCommand command);

    void writeOffAmount(WriteOffAmountCommand command);

    Long updateCreditLimit(UpdateCreditLimitCommand command);

    Long updateAvailableCreditLimit(UpdateAvailableCreditLimitCommand command);

    void resolveLoanDerivedValues(Long loanId, LocalDate when);

    void resolveLoanDerivedValues(Long loanId,String debtState,String debtStatus, LocalDate when);

    void validateLoanBalance(Long loanId, Balance balance);

    void extendMaturityDate(Long loanId, LocalDate when);

    void setPenaltySuspended(Long loanId, boolean penaltySuspended);

    Long addInstallment(AddInstallmentCommand command);

    void cancelInstallment(CancelInstallmentCommand command);

    void startUpsellDisbursement(Long loanId);

    void endUpsellDisbursement(Long loanId);

    void closePaidLoan(ClosePaidLoanCommand closePaidLoanCommand);

    CloudFile exportAgreements(List<Long> loanIds);

}

