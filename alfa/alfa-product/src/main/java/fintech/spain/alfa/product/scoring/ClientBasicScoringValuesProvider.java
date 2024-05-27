package fintech.spain.alfa.product.scoring;

import fintech.ScoringProperties;
import fintech.TimeMachine;
import fintech.crm.client.Client;
import fintech.crm.client.ClientService;
import fintech.lending.core.application.LoanApplication;
import fintech.lending.core.application.LoanApplicationQuery;
import fintech.lending.core.application.LoanApplicationService;
import fintech.lending.core.application.LoanApplicationStatusDetail;
import fintech.lending.core.loan.Loan;
import fintech.lending.core.loan.LoanQuery;
import fintech.lending.core.loan.LoanService;
import fintech.lending.core.loan.LoanStatusDetail;
import fintech.scoring.values.spi.ScoringValuesProvider;
import fintech.spain.alfa.product.workflow.common.Resolutions;
import fintech.transactions.Balance;
import fintech.transactions.TransactionQuery;
import fintech.transactions.TransactionService;
import fintech.workflow.Activity;
import fintech.workflow.ActivityStatus;
import fintech.workflow.WorkflowService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.transaction.Transactional;
import java.math.BigDecimal;
import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.Properties;

import static fintech.BigDecimalUtils.amount;
import static fintech.lending.core.application.LoanApplicationQuery.byClientId;
import static fintech.lending.core.application.LoanApplicationStatus.CLOSED;
import static java.util.Collections.singletonList;

@Slf4j
@Component
@Transactional
public class ClientBasicScoringValuesProvider implements ScoringValuesProvider {

    private static final String APPLICATION_COUNT_WITHIN_30_DAYS = "application_count_within_30_days";
    private static final String REJECTION_COUNT_IN_30_DAYS = "rejection_count_in_30_days";
    private static final String REJECTION_COUNT_IN_7_DAYS = "rejection_count_in_7_days";
    private static final String PRINCIPAL_DISBURSED = "principal_disbursed";
    private static final String PRINCIPAL_SOLD = "principal_sold";
    private static final String FEE_PAID = "fee_paid";
    private static final String PENALTY_PAID = "penalty_paid";
    private static final String CASH_IN = "cash_in";
    private static final String MAX_PRINCIPAL_REPAID = "max_principal_repaid";
    private static final String DAYS_SINCE_LAST_APPLICATION = "days_since_last_application";
    private static final String DAYS_SINCE_LAST_APPLICATION_REJECTION = "days_since_last_application_rejection";
    private static final String LAST_LOAN_APPLICATION_REJECTION_REASON = "last_loan_application_rejection_reason";
    private static final String TOTAL_OVERDUE_DAYS = "total_overdue_days";
    private static final String MAX_OVERDUE_DAYS = "max_overdue_days";
    private static final String MAX_OVERDUE_DAYS_IN_LAST_12_MONTHS = "max_overdue_days_in_last_12_months";
    private static final String LAST_LOAN_OVERDUE_DAYS = "last_loan_overdue_days";
    private static final String PAID_LOAN_COUNT = "paid_loan_count";
    private static final String AGE = "age";

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
    public Properties provide(long clientId) {
        LoanApplicationQuery previousApplicationsQuery = new LoanApplicationQuery().setClientId(clientId)
            .setSubmittedDateFrom(TimeMachine.today().minusDays(30))
            .setStatuses(singletonList(CLOSED));
        List<LoanApplication> loanApplications = loanApplicationService.find(previousApplicationsQuery);
        Optional<LoanApplication> lastApplication = loanApplicationService.findLatest(byClientId(clientId, CLOSED));
        Optional<LoanApplication> lastRejectedApplication = loanApplicationService.findLatest(byClientId(clientId, singletonList(LoanApplicationStatusDetail.REJECTED)));

        Balance balance = transactionService.getBalance(TransactionQuery.byClient(clientId));
        List<Loan> loans = loanService.findLoans(LoanQuery.allLoans(clientId));
        List<Loan> paidLoans = loanService.findLoans(LoanQuery.paidLoans(clientId));
        Client client = clientService.get(clientId);

        ScoringProperties properties = new ScoringProperties("basic");
        properties.put(APPLICATION_COUNT_WITHIN_30_DAYS, loanApplications.size());
        properties.put(REJECTION_COUNT_IN_30_DAYS, (int) loanApplications.stream()
            .filter(loanApplication -> LoanApplicationStatusDetail.REJECTED.equals(loanApplication.getStatusDetail()))
            .count());

        properties.put(REJECTION_COUNT_IN_7_DAYS, (int) loanApplications.stream()
            .filter(loanApplication -> LoanApplicationStatusDetail.REJECTED.equals(loanApplication.getStatusDetail())
                && loanApplication.getSubmittedAt().isAfter(TimeMachine.today().minusDays(7).atStartOfDay()))
            .count());

        properties.put(PRINCIPAL_DISBURSED, balance.getPrincipalDisbursed());
        properties.put(PRINCIPAL_SOLD, loans.stream().filter(loan -> loan.getStatusDetail() == LoanStatusDetail.SOLD)
            .map(Loan::getPrincipalDisbursed).reduce(amount(0), BigDecimal::add));

        properties.put(FEE_PAID, balance.getFeePaid());
        properties.put(PENALTY_PAID, balance.getPenaltyPaid());
        properties.put(CASH_IN, balance.getCashIn());

        properties.put(MAX_PRINCIPAL_REPAID, paidLoans.stream()
            .map(Loan::getPrincipalPaid)
            .max(BigDecimal::compareTo)
            .orElse(amount(0)));

        properties.put(DAYS_SINCE_LAST_APPLICATION, lastApplication
            .map(loanApplication -> Duration.between(loanApplication.getSubmittedAt(), TimeMachine.today().atStartOfDay()))
            .map(Duration::toDays)
            .orElse(null));

        properties.put(DAYS_SINCE_LAST_APPLICATION_REJECTION, lastRejectedApplication
            .map(loanApplication -> Duration.between(loanApplication.getCloseDate().atStartOfDay(), TimeMachine.today().atStartOfDay()))
            .map(Duration::toDays)
            .orElse(null));

        properties.put(LAST_LOAN_APPLICATION_REJECTION_REASON, lastRejectedApplication
            .map(LoanApplication::getWorkflowId)
            .map(workflowService::getWorkflow)
            .flatMap(wf -> wf.getActivities().stream()
                .filter(activity -> activity.getStatus() == ActivityStatus.COMPLETED && Resolutions.REJECT.equals(activity.getResolution()))
                .max(Comparator.comparing(Activity::getId)))
            .map(Activity::getName)
            .orElse(null));

        properties.put(TOTAL_OVERDUE_DAYS, loans.stream().map(Loan::getOverdueDays).reduce(0, Integer::sum));
        properties.put(MAX_OVERDUE_DAYS, loans.stream().map(Loan::getOverdueDays).max(Comparator.naturalOrder()).orElse(0));
        properties.put(MAX_OVERDUE_DAYS_IN_LAST_12_MONTHS, loans.stream()
            .filter(loan -> loan.getIssueDate().isAfter(TimeMachine.today().minusMonths(12)))
            .map(Loan::getOverdueDays)
            .max(Comparator.naturalOrder())
            .orElse(0));

        properties.put(LAST_LOAN_OVERDUE_DAYS, loans.stream().max(Comparator.comparing(Loan::getIssueDate))
            .map(Loan::getOverdueDays).orElse(0));

        properties.put(PAID_LOAN_COUNT, paidLoans.size());
        properties.put(AGE, ChronoUnit.YEARS.between(client.getDateOfBirth(), TimeMachine.today()));

        return properties;
    }
}
