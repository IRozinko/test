package fintech.lending.core.loan;

import fintech.lending.core.loan.commands.AddInstallmentCommand;
import fintech.lending.core.loan.commands.AddLoanContractCommand;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface ScheduleService {
    List<Installment> findInstallments(InstallmentQuery query);

    Contract getCurrentContract(Long loanId);

    Contract getContract(Long contractId);

    Installment getFirstActiveInstallment(Long loanId);

    Long addContract(AddLoanContractCommand command);

    void changeContractEffectiveDate(Long contractId, LocalDate effectiveDate);

    void changeContractMaturityDate(Long contractId, LocalDate maturityDate);

    void changeContractCloseLoanOnPaid(Long contractId, boolean newValue);

    List<Contract> getContracts(Long loanId);

    Long addInstallment(AddInstallmentCommand command);

    void saveInstallmentInvoice(Long installmentId, Long fileId, String fileName, LocalDateTime when);

    void installmentInvoiceSent(Long installmentId, LocalDateTime when);

    Installment getInstallment(Long installmentId);

    Optional<Installment> findInstallmentByNumber(String number);
}
