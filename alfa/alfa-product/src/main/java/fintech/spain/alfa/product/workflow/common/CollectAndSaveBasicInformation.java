package fintech.spain.alfa.product.workflow.common;

import fintech.JsonUtils;
import fintech.TimeMachine;
import fintech.crm.client.Client;
import fintech.crm.client.ClientService;
import fintech.lending.core.application.LoanApplication;
import fintech.lending.core.application.LoanApplicationQuery;
import fintech.lending.core.application.LoanApplicationService;
import fintech.lending.core.application.LoanApplicationStatusDetail;
import fintech.lending.core.application.commands.SaveParamsCommand;
import fintech.lending.core.loan.Loan;
import fintech.lending.core.loan.LoanQuery;
import fintech.lending.core.loan.LoanService;
import fintech.lending.core.loan.LoanStatusDetail;
import fintech.spain.alfa.product.risk.rules.basic.BasicRuleParams;
import fintech.transactions.Balance;
import fintech.transactions.TransactionQuery;
import fintech.transactions.TransactionService;
import fintech.workflow.Activity;
import fintech.workflow.ActivityStatus;
import fintech.workflow.WorkflowService;
import fintech.workflow.spi.ActivityContext;
import fintech.workflow.spi.ActivityHandler;
import fintech.workflow.spi.ActivityResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static fintech.BigDecimalUtils.amount;

@Scope(BeanDefinition.SCOPE_PROTOTYPE)
@Component
public class CollectAndSaveBasicInformation implements ActivityHandler {

    public static final String BASIC_INFORMATION_ATTRIBUTE = "BasicLendingRules";

    @Autowired
    private LoanApplicationService loanApplicationService;

    @Autowired
    private TransactionService transactionService;

    @Autowired
    private LoanService loanService;

    @Autowired
    private ClientService clientService;

    @Autowired
    private WorkflowService workflowService;

    @Override
    public ActivityResult handle(ActivityContext context) {
        List<LoanApplication> loanApplications = loanApplicationService.find(new LoanApplicationQuery().setClientId(context.getClientId()).setSubmittedDateFrom(TimeMachine.today().minusDays(30))).stream().filter(loanApplication -> !loanApplication.getId().equals(context.getWorkflow().getApplicationId())).collect(Collectors.toList());
        Optional<LoanApplication> maybeLastRejectedLoanApplication = loanApplications.stream().filter(loanApplication -> LoanApplicationStatusDetail.REJECTED.equals(loanApplication.getStatusDetail())).max(Comparator.comparing(LoanApplication::getCloseDate));
        Balance balance = transactionService.getBalance(TransactionQuery.byClient(context.getClientId()));
        List<Loan> loans = loanService.findLoans(LoanQuery.allLoans(context.getClientId()));
        List<Loan> paidLoans = loanService.findLoans(LoanQuery.paidLoans(context.getClientId()));
        Client client = clientService.get(context.getClientId());


        // Todo: do not change it!! see ClientScoringValuesProvider
        BasicRuleParams params = new BasicRuleParams()
            .setApplicationCountWithin30Days(loanApplications.size())
            .setRejectionCountIn30Days(loanApplications.stream().filter(loanApplication -> LoanApplicationStatusDetail.REJECTED.equals(loanApplication.getStatusDetail())).collect(Collectors.toList()).size())
            .setRejectionCountIn7Days(loanApplications.stream().filter(loanApplication -> LoanApplicationStatusDetail.REJECTED.equals(loanApplication.getStatusDetail()) && loanApplication.getSubmittedAt().isAfter(LocalDateTime.of(TimeMachine.today().minusDays(7), LocalTime.MIN))).collect(Collectors.toList()).size())
            .setPrincipalDisbursed(balance.getPrincipalDisbursed())
            .setPrincipalSold(loans.stream().filter(loan -> loan.getStatusDetail() == LoanStatusDetail.SOLD).map(Loan::getPrincipalDisbursed).reduce(amount(0), BigDecimal::add))
            .setFeePaid(balance.getFeePaid())

            .setPenaltyPaid(balance.getPenaltyPaid())
            .setCashIn(balance.getCashIn())
            .setMaxPrincipalRepaid(paidLoans.stream().map(Loan::getPrincipalPaid).max(BigDecimal::compareTo).orElse(amount(0)))
            .setDaysSinceLastApplication(loanApplications.stream().max(Comparator.comparing(LoanApplication::getSubmittedAt)).map(loanApplication -> ChronoUnit.DAYS.between(loanApplication.getSubmittedAt(), LocalDateTime.of(TimeMachine.today(), LocalTime.MIN))).orElse(null))
            .setDaysSinceLastApplicationRejection(maybeLastRejectedLoanApplication.map(loanApplication -> ChronoUnit.DAYS.between(loanApplication.getCloseDate(), TimeMachine.today())).orElse(null))
            .setLastLoanApplicationRejectionReason(maybeLastRejectedLoanApplication.flatMap(loanApplication -> Optional.ofNullable(loanApplication.getWorkflowId())).map(workflowId -> workflowService.getWorkflow(workflowId)).flatMap(workflow -> workflow.getActivities().stream().filter(activity -> activity.getStatus() == ActivityStatus.COMPLETED && Resolutions.REJECT.equals(activity.getResolution())).max(Comparator.comparing(Activity::getId))).map(Activity::getName).orElse(null))
            .setTotalOverdueDays(loans.stream().map(Loan::getOverdueDays).reduce(0, Integer::sum))
            .setMaxOverdueDays(loans.stream().map(Loan::getOverdueDays).max(Comparator.naturalOrder()).orElse(0))
            .setMaxOverdueDaysInLast12Months(loans.stream().filter(loan -> loan.getIssueDate().isAfter(TimeMachine.today().minusMonths(12))).map(Loan::getOverdueDays).max(Comparator.naturalOrder()).orElse(0))
            .setLastLoanOverdueDays(loans.stream().max(Comparator.comparing(Loan::getIssueDate)).map(Loan::getOverdueDays).orElse(0))
            .setPaidLoanCount(paidLoans.size())
            .setAge(ChronoUnit.YEARS.between(client.getDateOfBirth(), TimeMachine.today()));

        loanApplicationService.saveParams(new SaveParamsCommand(context.getWorkflow().getApplicationId(), BASIC_INFORMATION_ATTRIBUTE, JsonUtils.writeValueAsString(params)));
        return ActivityResult.resolution(Resolutions.APPROVE, "");
    }
}
