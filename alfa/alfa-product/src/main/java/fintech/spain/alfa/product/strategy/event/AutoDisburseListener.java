package fintech.spain.alfa.product.strategy.event;

import fintech.lending.core.loan.Contract;
import fintech.lending.core.loan.InstallmentStatus;
import fintech.lending.core.loan.InstallmentStatusDetail;
import fintech.lending.core.loan.Loan;
import fintech.lending.core.loan.LoanService;
import fintech.lending.core.loan.ScheduleService;
import fintech.lending.core.loan.db.ContractEntity;
import fintech.lending.core.loan.db.ContractRepository;
import fintech.lending.core.loan.db.InstallmentEntity;
import fintech.lending.core.loan.db.InstallmentRepository;
import fintech.lending.core.loan.events.LoanAdditionalEvent;
import fintech.lending.core.util.TransactionBuilder;
import fintech.spain.alfa.product.AlfaConstants;
import fintech.transactions.AddTransactionCommand;
import fintech.transactions.TransactionService;
import fintech.transactions.TransactionType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

@Component
@Slf4j
class AutoDisburseListener {
    @Autowired
    private LoanService loanService;
    @Autowired
    private InstallmentRepository installmentRepository;
    @Autowired
    private ScheduleService scheduleService;
    @Autowired
    private TransactionBuilder transactionBuilder;
    @Autowired
    private TransactionService transactionService;
    @Autowired
    private ContractRepository contractRepository;

    @EventListener
    @Transactional(propagation = Propagation.MANDATORY)
    public void onEvent(LoanAdditionalEvent event) {

        Loan loan = loanService.getLoan(event.getLoan().getId());


        Contract contract = scheduleService.getCurrentContract(loan.getId());
        LocalDate contractActiveFrom = event.getLoan().getIssueDate();
        LocalDate dueDate = event.getLoan().getPaymentDueDate();
        LocalDate disbursementExportDate = event.getLoan().getIssueDate();


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
        tx.setLoanId(event.getLoan().getId());
        tx.setInstallmentId(installment.getId());
        tx.setPrincipalDisbursed(event.getDisbursedAmount());
        tx.setPrincipalInvoiced(event.getDisbursedAmount());
        tx.setTransactionType(TransactionType.DISBURSEMENT);
        transactionBuilder.addLoanValues(loan, tx);
        tx.setValueDate(disbursementExportDate);

        transactionService.addTransaction(tx);

        ContractEntity contractEntity = contractRepository.findOne(contract.getId());
        contractEntity.setEffectiveDate(contractActiveFrom);
        contractEntity.setMaturityDate(dueDate);

        InstallmentEntity installmentEntity = installmentRepository.getRequired(installment.getId());
        installmentEntity.setDueDate(dueDate);
        log.info("Added disbursements, transactions, installments");
    }
}
