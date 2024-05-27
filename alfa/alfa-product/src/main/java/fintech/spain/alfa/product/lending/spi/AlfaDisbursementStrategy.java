package fintech.spain.alfa.product.lending.spi;

import fintech.Validate;
import fintech.lending.core.application.LoanApplication;
import fintech.lending.core.application.LoanApplicationService;
import fintech.lending.core.application.LoanApplicationStatusDetail;
import fintech.lending.core.loan.*;
import fintech.lending.core.loan.commands.DisburseLoanCommand;
import fintech.lending.core.loan.db.ContractEntity;
import fintech.lending.core.loan.db.ContractRepository;
import fintech.lending.core.loan.db.InstallmentEntity;
import fintech.lending.core.loan.db.InstallmentRepository;
import fintech.lending.core.loan.spi.DisbursementStrategy;
import fintech.lending.core.util.TransactionBuilder;
import fintech.payments.DisbursementService;
import fintech.payments.model.Disbursement;
import fintech.spain.alfa.product.AlfaConstants;
import fintech.transactions.AddTransactionCommand;
import fintech.transactions.TransactionService;
import fintech.transactions.TransactionType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Component
public class AlfaDisbursementStrategy implements DisbursementStrategy {

    @Autowired
    private DisbursementService disbursementService;

    @Autowired
    private LoanService loanService;

    @Autowired
    private LoanApplicationService loanApplicationService;

    @Autowired
    private TransactionBuilder transactionBuilder;

    @Autowired
    private TransactionService transactionService;

    @Autowired
    private ContractRepository contractRepository;

    @Autowired
    private InstallmentRepository installmentRepository;

    @Autowired
    private ScheduleService scheduleService;

    @Override
    public Long disburse(DisburseLoanCommand command) {
        Disbursement disbursement = disbursementService.getDisbursement(command.getDisbursementId());
        Loan loan = loanService.getLoan(disbursement.getLoanId());
        LoanApplication application = loanApplicationService.get(disbursement.getApplicationId());

        Validate.isTrue(loan.getStatus() == LoanStatus.OPEN, "Illegal loan status, [%s]", loan);
        Validate.isTrue(loan.getStatusDetail() == LoanStatusDetail.DISBURSING, "Illegal loan status detail, expected DISBURSING, [%s]", loan);
        Validate.isEqual(disbursement.getAmount(), application.getOfferedPrincipal(), "Disbursement amount and application offered principal does not match, [%s], [%s]", disbursement, application);
        Validate.isTrue(LoanApplicationStatusDetail.APPROVED.equals(application.getStatusDetail()), "Loan application must be approved: [%s]", application);


        Contract contract = scheduleService.getCurrentContract(loan.getId());
        LocalDate contractActiveFrom = disbursement.getExportedAt().toLocalDate();
        LocalDate dueDate = contractActiveFrom.plus(contract.getPeriodCount(), contract.getPeriodUnit().toTemporalUnit());
        LocalDate disbursementExportDate = disbursement.getExportedAt().toLocalDate();


        InstallmentEntity installment = installmentRepository.findByLoanId(loan.getId())
            .orElseGet(() -> {
                InstallmentEntity installmentEntity = new InstallmentEntity();
                installmentEntity.setLoanId(loan.getId());
                installmentEntity.setClientId(loan.getClientId());
                installmentEntity.setContractId(contract.getId());
                installmentEntity.setStatus(InstallmentStatus.OPEN);
                installmentEntity.setStatusDetail(InstallmentStatusDetail.PENDING);
                installmentEntity.setPeriodFrom(loan.getIssueDate());
                installmentEntity.setPeriodTo(dueDate);
                installmentEntity.setDueDate(dueDate);
                installmentEntity.setOriginalDueDate(dueDate);
                installmentEntity.setGracePeriodInDays(AlfaConstants.GRACE_PERIOD_IN_DAYS);
                installmentEntity.setInstallmentSequence(1L);
                installmentEntity.setInstallmentNumber(loan.getNumber() + "-1");
                installmentEntity.setValueDate(disbursementExportDate);
                return installmentRepository.save(installmentEntity);
            });


        AddTransactionCommand tx = new AddTransactionCommand();
        tx.setApplicationId(application.getId());
        tx.setInstallmentId(installment.getId());
        tx.setPrincipalDisbursed(disbursement.getAmount());
        tx.setPrincipalInvoiced(disbursement.getAmount());
        tx.setInterestApplied(application.getOfferedInterest());
        tx.setInterestInvoiced(application.getOfferedInterest());
        tx.setTransactionType(TransactionType.DISBURSEMENT);
        transactionBuilder.addDisbursementValues(disbursement.getId(), tx);
        transactionBuilder.addLoanValues(loan, tx);
        // TODO should be done in helper, but this will affect credit line
        tx.setValueDate(disbursementExportDate);

        Long txId = transactionService.addTransaction(tx);

        ContractEntity contractEntity = contractRepository.findOne(contract.getId());
        contractEntity.setEffectiveDate(contractActiveFrom);
        contractEntity.setMaturityDate(dueDate);

        InstallmentEntity installmentEntity = installmentRepository.getRequired(installment.getId());
        installmentEntity.setDueDate(dueDate);

        return txId;
    }
}
