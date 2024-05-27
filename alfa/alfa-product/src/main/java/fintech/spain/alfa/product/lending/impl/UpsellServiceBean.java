package fintech.spain.alfa.product.lending.impl;

import com.google.common.base.Preconditions;
import fintech.lending.core.application.LoanApplication;
import fintech.lending.core.application.LoanApplicationService;
import fintech.lending.core.application.commands.ApproveLoanApplicationCommand;
import fintech.lending.core.loan.Contract;
import fintech.lending.core.loan.Installment;
import fintech.lending.core.loan.Loan;
import fintech.lending.core.loan.LoanService;
import fintech.lending.core.loan.ScheduleService;
import fintech.lending.core.loan.events.LoanDisbursedEvent;
import fintech.lending.core.util.TransactionBuilder;
import fintech.payments.DisbursementService;
import fintech.payments.commands.AddDisbursementCommand;
import fintech.payments.model.Disbursement;
import fintech.payments.model.Institution;
import fintech.spain.alfa.product.lending.UpsellService;
import fintech.spain.alfa.product.payments.AlfaDisbursementConstants;
import fintech.transactions.AddTransactionCommand;
import fintech.transactions.TransactionService;
import fintech.transactions.TransactionType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

import static fintech.spain.alfa.product.AlfaConstants.DISBURSEMENT_REFERENCE_ENDING;
import static fintech.spain.alfa.product.AlfaConstants.DISBURSEMENT_REFERENCE_LENGTH;
import static fintech.spain.alfa.product.AlfaConstants.DISBURSEMENT_REFERENCE_PREFIX;

@Component
@Transactional
class UpsellServiceBean implements UpsellService {

    @Autowired
    private LoanApplicationService loanApplicationService;

    @Autowired
    private LoanService loanService;

    @Autowired
    private InstitutionFinder institutionFinder;

    @Autowired
    private DisbursementService disbursementService;

    @Autowired
    private TransactionBuilder transactionBuilder;

    @Autowired
    private TransactionService transactionService;

    @Autowired
    private ApplicationEventPublisher eventPublisher;

    @Autowired
    private ScheduleService scheduleService;

    @Override
    public Long issueUpsell(Long loanApplicationId, LocalDate issueDate) {
        LoanApplication loanApplication = loanApplicationService.get(loanApplicationId);

        approveLoanApplication(loanApplication, issueDate);
        addIssueLoanUpsellTransaction(loanApplication, issueDate);
        Long disbursementId = addUpsellDisbursement(loanApplication, issueDate);

        loanService.resolveLoanDerivedValues(loanApplication.getLoanId(), issueDate);
        loanService.startUpsellDisbursement(loanApplication.getLoanId());
        return disbursementId;
    }

    private void approveLoanApplication(LoanApplication loanApplication, LocalDate issueDate) {
        ApproveLoanApplicationCommand approveLoanApplication = new ApproveLoanApplicationCommand();
        approveLoanApplication.setId(loanApplication.getId());
        approveLoanApplication.setApproveDate(issueDate);
        approveLoanApplication.setLoanId(loanApplication.getLoanId());

        loanApplicationService.approve(approveLoanApplication);
    }

    private void addIssueLoanUpsellTransaction(LoanApplication loanApplication, LocalDate issueDate) {
        Contract contract = scheduleService.getCurrentContract(loanApplication.getLoanId());
        Loan loan = loanService.getLoan(loanApplication.getLoanId());

        AddTransactionCommand addTransaction = new AddTransactionCommand();
        addTransaction.setTransactionType(TransactionType.ISSUE_LOAN);
        addTransaction.setTransactionSubType("UPSELL");
        addTransaction.setValueDate(issueDate);
        addTransaction.setContractId(contract.getId());
        transactionBuilder.addLoanValues(loan, addTransaction);

        transactionService.addTransaction(addTransaction);
    }

    private Long addUpsellDisbursement(LoanApplication loanApplication, LocalDate issueDate) {
        Institution institution = institutionFinder.findDisbursementInstitution(loanApplication.getClientId());

        AddDisbursementCommand addDisbursement = new AddDisbursementCommand();
        addDisbursement.setDisbursementType(AlfaDisbursementConstants.DISBURSEMENT_TYPE_UPSELL);
        addDisbursement.setClientId(loanApplication.getClientId());
        addDisbursement.setLoanId(loanApplication.getLoanId());
        addDisbursement.setApplicationId(loanApplication.getId());
        addDisbursement.setInstitutionId(institution.getId());
        addDisbursement.setInstitutionAccountId(institution.getPrimaryAccount().getId());
        addDisbursement.setAmount(loanApplication.getOfferedPrincipal());
        addDisbursement.setValueDate(issueDate);
        addDisbursement.setReference(disbursementService.generateReference(DISBURSEMENT_REFERENCE_PREFIX,
            DISBURSEMENT_REFERENCE_ENDING, DISBURSEMENT_REFERENCE_LENGTH));
        return disbursementService.add(addDisbursement);
    }

    @Override
    public Long disburseUpsell(Long disbursementId) {
        Disbursement disbursement = disbursementService.getDisbursement(disbursementId);

        Preconditions.checkState(AlfaDisbursementConstants.DISBURSEMENT_TYPE_UPSELL.equals(disbursement.getDisbursementType()));

        LoanApplication loanApplication = loanApplicationService.get(disbursement.getApplicationId());

        Long transactionId = addDisbursementUpsellTransaction(disbursement, loanApplication);

        loanService.resolveLoanDerivedValues(loanApplication.getLoanId(), disbursement.getExportedAt().toLocalDate());
        eventPublisher.publishEvent(new LoanDisbursedEvent(loanService.getLoan(loanApplication.getLoanId())));
        loanService.endUpsellDisbursement(loanApplication.getLoanId());
        return transactionId;
    }

    private Long addDisbursementUpsellTransaction(Disbursement disbursement, LoanApplication loanApplication) {
        Loan loan = loanService.getLoan(disbursement.getLoanId());
        Installment installment = scheduleService.getFirstActiveInstallment(loanApplication.getLoanId());

        AddTransactionCommand addTransaction = new AddTransactionCommand();
        addTransaction.setTransactionType(TransactionType.DISBURSEMENT);
        addTransaction.setTransactionSubType("UPSELL");
        addTransaction.setValueDate(disbursement.getExportedAt().toLocalDate());
        addTransaction.setInstallmentId(installment.getId());
        addTransaction.setPrincipalDisbursed(disbursement.getAmount());
        addTransaction.setPrincipalInvoiced(disbursement.getAmount());
        addTransaction.setInterestApplied(loanApplication.getOfferedInterest());
        addTransaction.setInterestInvoiced(loanApplication.getOfferedInterest());
        transactionBuilder.addDisbursementValues(disbursement.getId(), addTransaction);
        transactionBuilder.addLoanValues(loan, addTransaction);

        return transactionService.addTransaction(addTransaction);
    }
}
