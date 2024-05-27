package fintech.spain.dc;

import fintech.Validate;
import fintech.dc.DcService;
import fintech.dc.DcSettingsService;
import fintech.dc.commands.*;
import fintech.dc.model.DcSettings;
import fintech.dc.model.Debt;
import fintech.lending.core.loan.Loan;
import fintech.lending.core.loan.LoanService;
import fintech.lending.core.loan.LoanStatusDetail;
import fintech.lending.core.loan.ScheduleService;
import fintech.lending.core.util.TransactionBuilder;
import fintech.spain.dc.command.PostLoanCommandFactory;
import fintech.spain.dc.command.RepurchaseDebtCommand;
import fintech.spain.dc.model.DcFacadeConfig;
import fintech.spain.dc.model.ReschedulingPreview;
import fintech.spain.dc.util.InstallmentNumberGenerator;
import fintech.transactions.AddTransactionCommand;
import fintech.transactions.Balance;
import fintech.transactions.Transaction;
import fintech.transactions.TransactionService;
import fintech.transactions.VoidTransactionCommand;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Optional;

import static fintech.PojoUtils.copyProperties;
import static fintech.TimeMachine.today;
import static fintech.dc.DcConstants.SOLD_PORTFOLIO;
import static fintech.transactions.TransactionQuery.byLoan;
import static fintech.transactions.TransactionQuery.notVoidedByLoan;
import static fintech.transactions.TransactionType.SOLD_LOAN;
import static java.lang.String.format;
import static org.apache.commons.lang3.Validate.isTrue;

@Slf4j
public abstract class AbstractDcFacadeBean implements AbstractDcFacade {

    @Autowired
    protected TransactionBuilder transactionBuilder;

    @Autowired
    protected LoanService loanService;

    @Autowired
    protected DcService dcService;

    @Autowired
    protected TransactionService transactionService;

    @Autowired
    protected InstallmentNumberGenerator installmentNumberGenerator;

    @Autowired
    protected ScheduleService scheduleService;

    @Autowired
    protected DcSettingsService dcSettingsService;

    private final DcFacadeConfig config;

    public AbstractDcFacadeBean(DcFacadeConfig config) {
        this.config = config;
    }

    @Override
    public Optional<Long> postLoan(Long loanId, String state, String status, boolean triggerActionsImmediately) {
        try {
            return Optional.ofNullable(doPostLoan(loanId, state, status, triggerActionsImmediately));
        } catch (Exception e) {
            log.error(String.format("Failed to post loan [%s] to DC", loanId), e);
            return Optional.empty();
        }
    }
    public void sell(Long debtId, String company) {
        log.info("Selling debt {} to company {}", debtId, company);

        DcSettings.Companies companies = dcSettingsService.getSettings().getCompanies();
        isTrue(companies.owningCompanyDefined(company), "No owning company found");

        Debt debt = dcService.get(debtId);
        isTrue(!isSold(debt), format("Already sold to %s.", debt.getOwningCompany()));
        isTrue(!dcService.isExternalized(debt), format("Already externalized to %s.", debt.getManagingCompany()));
        isTrue(debt.getDpd() >= config.getMinDpdForSell(),
            format("Debt [%s] has less DPD then required. MIN_DPD_FOR_SELL = %d.", debt, config.getMinDpdForSell()));

        Loan loan = loanService.getLoan(debt.getLoanId());
        Balance balance = transactionService.getBalance(byLoan(debt.getLoanId()));

        dcService.changeCompany(new ChangeCompanyCommand()
            .setDebtId(debtId)
            .setOwningCompany(company)
            .setManagingCompany(company)
            .setOperationType("selling")
            .setPortfolio(SOLD_PORTFOLIO)
        );

        dcService.unassignDebt(new UnassignDebtCommand()
            .setDebtId(debtId)
            .setComment("Debt sold")
        );

        AddTransactionCommand tx = AddTransactionCommand.soldLoan(balance);
        transactionBuilder.addLoanValues(loan, tx);
        transactionService.addTransaction(tx);

        sendLoanSoldNotification(debt);

        dcService.triggerActions(debtId);
    }

    @Override
    public void repurchase(RepurchaseDebtCommand command) {
        Debt debt = dcService.get(command.getDebtId());
        isTrue(isSold(debt), "Debt not sold");

        List<Transaction> txs = transactionService.findTransactions(notVoidedByLoan(debt.getLoanId(), SOLD_LOAN));
        isTrue(txs.size() == 1, format("No %s transaction found", SOLD_LOAN));

        transactionService.voidTransaction(VoidTransactionCommand.builder()
            .id(txs.get(0).getId())
            .bookingDate(today())
            .voidedDate(today())
            .build()
        );

        DcSettings.Companies companies = dcSettingsService.getSettings().getCompanies();

        EditDebtCommand editDebtCommand = new EditDebtCommand();
        copyProperties(editDebtCommand, command);
        editDebtCommand.setManagingCompany(companies.getDefaultManagingCompany());
        editDebtCommand.setOwningCompany(companies.getDefaultOwningCompany());
        editDebtCommand.setOperationType("repurchasing");
        dcService.edit(editDebtCommand);

        doAssignDebt(debt.getId(), command.isAutoAssign(), command.getAgent());
        dcService.triggerActions(debt.getId());
        dcService.triggerActionsOnVoidTransaction(debt.getId());
    }

    @Override
    public void recoverExternal(RecoverExternalCommand command) {
        DcSettings.Companies companies = dcSettingsService.getSettings().getCompanies();
        Debt debt = dcService.get(command.getDebtId());

        isTrue(dcService.isExternalized(debt), "Not externalized");

        EditDebtCommand editDebtCommand = new EditDebtCommand();
        copyProperties(editDebtCommand, command);
        editDebtCommand.setManagingCompany(companies.getDefaultManagingCompany());
        editDebtCommand.setOwningCompany(companies.getDefaultOwningCompany());
        editDebtCommand.setPortfolio(config.getReExternalizedPortfolio());
        editDebtCommand.setOperationType("re-externalizing");
        dcService.edit(editDebtCommand);

        doAssignDebt(debt.getId(), command.isAutoAssign(), command.getAgent());
        dcService.triggerActions(debt.getId());
    }
    @Override
    public void changeDebtState(ChangeDebtStateCommand command) {
        dcService.edit(command);
    }
    protected String generateInstallmentNumber(Loan loan, ReschedulingPreview.Item item) {
        return installmentNumberGenerator.generate(loan, item);
    }

    protected boolean isSold(Debt debt) {
        Loan loan = loanService.getLoan(debt.getLoanId());
        return LoanStatusDetail.SOLD == loan.getStatusDetail();
    }

    protected Long doPostLoan(Long loanId, boolean triggerActionsImmediately) {
        Loan loan = loanService.getLoan(loanId);

        if (loan.getMaturityDate() == null) {
            log.info("Loan [{}] has no maturity date, ignoring", loanId);
            return null;
        }

        PostLoanCommand command = PostLoanCommandFactory.fromLoan(loan, triggerActionsImmediately);

        Optional<Transaction> lastPaidTransaction = transactionService.lastPaidTransaction(loanId);
        lastPaidTransaction.ifPresent(transaction -> {
            command.setLastPaymentDate(transaction.getValueDate());
            command.setLastPaid(transaction.getCashIn());
        });

        return dcService.postLoan(command);
    }

    protected Long doPostLoan(Long loanId, String state, String status, boolean triggerActionsImmediately) {
        Loan loan = loanService.getLoan(loanId);

        if (loan.getMaturityDate() == null) {
            log.info("Loan [{}] has no maturity date, ignoring", loanId);
            return null;
        }

        PostLoanCommand command = PostLoanCommandFactory.fromLoan(loan, triggerActionsImmediately);

        Optional<Transaction> lastPaidTransaction = transactionService.lastPaidTransaction(loanId);
        lastPaidTransaction.ifPresent(transaction -> {
            command.setLastPaymentDate(transaction.getValueDate());
            command.setLastPaid(transaction.getCashIn());
        });
        command.setState(state);
        command.setStatus(status);
        return dcService.postLoan(command);
    }

    protected void doAssignDebt(Long debtId, boolean autoAssign, String agent) {
        if (autoAssign) {
            AutoAssignDebtCommand autoAssignDebtCommand = new AutoAssignDebtCommand();
            autoAssignDebtCommand.setDebtId(debtId);
            dcService.autoAssignDebt(autoAssignDebtCommand);
        } else {
            Validate.notNull(agent);
            AssignDebtCommand assignDebtCommand = new AssignDebtCommand();
            assignDebtCommand.setDebtId(debtId);
            assignDebtCommand.setAgent(agent);
            dcService.assignDebt(assignDebtCommand);
        }
    }

    protected abstract void sendLoanSoldNotification(Debt debt);
}
