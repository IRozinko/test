package fintech.lending.creditline.impl;

import fintech.Validate;
import fintech.lending.core.application.LoanApplication;
import fintech.lending.core.application.LoanApplicationService;
import fintech.lending.core.application.LoanApplicationStatusDetail;
import fintech.lending.core.loan.Contract;
import fintech.lending.core.loan.Loan;
import fintech.lending.core.loan.LoanService;
import fintech.lending.core.loan.LoanStatus;
import fintech.lending.core.loan.ScheduleService;
import fintech.lending.core.loan.commands.DisburseLoanCommand;
import fintech.lending.core.loan.spi.DisbursementStrategy;
import fintech.lending.core.util.TransactionBuilder;
import fintech.payments.DisbursementService;
import fintech.payments.model.Disbursement;
import fintech.transactions.AddTransactionCommand;
import fintech.transactions.TransactionService;
import fintech.transactions.TransactionType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class CreditLineDisbursementStrategy implements DisbursementStrategy {

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
    private ScheduleService scheduleService;

    @Override
    public Long disburse(DisburseLoanCommand command) {
        Disbursement disbursement = disbursementService.getDisbursement(command.getDisbursementId());
        Loan loan = loanService.getLoan(disbursement.getLoanId());
        LoanApplication application = loanApplicationService.get(loan.getApplicationId());

        Validate.isTrue(loan.getStatus() == LoanStatus.OPEN, "Illegal loan status, [%s]", loan);
        Validate.isEqual(disbursement.getAmount(), application.getOfferedPrincipal(), "Disbursement amount and application offered principal does not match, [%s], [%s]", disbursement, application);
        Validate.isTrue(LoanApplicationStatusDetail.APPROVED.equals(application.getStatusDetail()), "Loan application must be approved: [%s]", application);

        AddTransactionCommand tx = new AddTransactionCommand();
        tx.setPrincipalDisbursed(disbursement.getAmount());
        tx.setTransactionType(TransactionType.DISBURSEMENT);
        transactionBuilder.addDisbursementValues(disbursement.getId(), tx);
        transactionBuilder.addLoanValues(loan, tx);

        Contract contract = scheduleService.getCurrentContract(loan.getId());
        scheduleService.changeContractEffectiveDate(contract.getId(), disbursement.getExportedAt().toLocalDate());

        return transactionService.addTransaction(tx);
    }
}
