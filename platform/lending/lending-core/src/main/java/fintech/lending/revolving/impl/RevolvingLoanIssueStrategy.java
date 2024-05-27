package fintech.lending.revolving.impl;

import fintech.Validate;
import fintech.lending.core.application.LoanApplication;
import fintech.lending.core.application.LoanApplicationService;
import fintech.lending.core.application.commands.ApproveLoanApplicationCommand;
import fintech.lending.core.loan.LoanQuery;
import fintech.lending.core.loan.LoanService;
import fintech.lending.core.loan.LoanStatusDetail;
import fintech.lending.core.loan.ScheduleService;
import fintech.lending.core.loan.commands.AddLoanContractCommand;
import fintech.lending.core.loan.commands.CreateLoanCommand;
import fintech.lending.core.loan.commands.IssueLoanCommand;
import fintech.lending.core.loan.db.LoanEntity;
import fintech.lending.core.loan.db.LoanRepository;
import fintech.lending.core.loan.events.LoanIssuedEvent;
import fintech.lending.core.loan.spi.LoanIssueStrategy;
import fintech.lending.core.util.TransactionBuilder;
import fintech.transactions.AddTransactionCommand;
import fintech.transactions.TransactionService;
import fintech.transactions.TransactionType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

import static fintech.BigDecimalUtils.amount;
import static fintech.TimeMachine.today;

@Slf4j
@Component
public class RevolvingLoanIssueStrategy implements LoanIssueStrategy {

    @Autowired
    private LoanRepository loanRepository;

    @Autowired
    private TransactionBuilder transactionBuilder;

    @Autowired
    private TransactionService transactionService;

    @Autowired
    private ApplicationEventPublisher eventPublisher;

    @Autowired
    private LoanApplicationService loanApplicationService;

    @Autowired
    private LoanService loanService;

    @Autowired
    private ScheduleService scheduleService;

    @Override
    public Long issue(IssueLoanCommand command) {
        log.info("Create loan: [{}]", command);

        LoanApplication application = loanApplicationService.get(command.getLoanApplicationId());
        Validate.isPositive(application.getCreditLimit(), "Credit limit must be positive [%s]", application);

        long loansPaid = loanService.findLoans(LoanQuery.paidLoans(application.getClientId())).size();

        LoanEntity loan = new LoanEntity();
        loan.setClientId(application.getClientId());
        loan.setNumber(command.getLoanNumber());
        loan.open(LoanStatusDetail.ISSUED);
        loan.setLoanApplicationId(command.getLoanApplicationId());
        loan.setProductId(application.getProductId());
        loan.setIssueDate(command.getIssueDate());
        loan.setLoansPaid(loansPaid);
        loan.setInvoicePaymentDay(application.getInvoicePaymentDay());
        //TODO save the credit limit awarded in loan application
        loan.setCreditLimitAwarded(application.getCreditLimit().multiply(amount(2)));
        loan.setPeriodUnit(application.getOfferedPeriodUnit());
        loan.setPeriodCount(application.getOfferedPeriodCount());
        loan.setDiscountId(application.getDiscountId());
        loan.setPromoCodeId(application.getPromoCodeId());
        loan.setExtensionStrategyId(application.getExtensionStrategyId());
        loan.setInterestStrategyId(application.getInterestStrategyId());
        loan.setPenaltyStrategyId(application.getPenaltyStrategyId());
        loan.setFeeStrategyId(application.getFeeStrategyId());
        loan = loanRepository.saveAndFlush(loan);

        LocalDate maturityDate = command.getIssueDate()
            .plus(application.getOfferedPeriodCount(), application.getOfferedPeriodUnit().toTemporalUnit());
        Long contractId = scheduleService.addContract(new AddLoanContractCommand()
            .setLoanId(loan.getId())
            .setProductId(loan.getProductId())
            .setClientId(loan.getClientId())
            .setApplicationId(loan.getLoanApplicationId())
            .setContractDate(loan.getIssueDate())
            .setEffectiveDate(loan.getIssueDate())
            .setMaturityDate(maturityDate)
            .setPeriodCount(application.getOfferedPeriodCount())
            .setPeriodUnit(application.getOfferedPeriodUnit())
            .setNumberOfInstallments(0L)
            .setCloseLoanOnPaid(false)
            .setSourceTransactionType(TransactionType.ISSUE_LOAN)
        );

        AddTransactionCommand tx = new AddTransactionCommand();
        tx.setContractId(contractId);
        tx.setValueDate(command.getIssueDate());
        tx.setTransactionType(TransactionType.ISSUE_LOAN);
        tx.setCreditLimit(application.getCreditLimit());
        transactionBuilder.addLoanValues(loan.toValueObject(), tx);
        Long txId = transactionService.addTransaction(tx);

        ApproveLoanApplicationCommand approvedCommand = new ApproveLoanApplicationCommand();
        approvedCommand.setId(command.getLoanApplicationId());
        approvedCommand.setApproveDate(command.getIssueDate());
        approvedCommand.setLoanId(loan.getId());
        loanApplicationService.approve(approvedCommand);

        loanService.resolveLoanDerivedValues(loan.getId(), today());

        eventPublisher.publishEvent(new LoanIssuedEvent(loan.toValueObject()));

        return loan.getId();
    }

    @Override
    public Long issue(CreateLoanCommand command) {
        return null;
    }
}
