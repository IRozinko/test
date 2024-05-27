package fintech.spain.alfa.product.scoring;

import com.google.common.annotations.VisibleForTesting;
import fintech.ScoringProperties;
import fintech.TimeMachine;
import fintech.Validate;
import fintech.lending.core.application.LoanApplication;
import fintech.lending.core.application.LoanApplicationQuery;
import fintech.lending.core.application.LoanApplicationService;
import fintech.lending.core.application.LoanApplicationStatus;
import fintech.lending.core.loan.Loan;
import fintech.lending.core.loan.LoanQuery;
import fintech.lending.core.loan.LoanService;
import fintech.scoring.values.spi.ScoringValuesProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Properties;

import static com.querydsl.core.types.Order.DESC;
import static fintech.DateUtils.daysBetween;
import static fintech.lending.core.loan.LoanQuery.LoanSortField.ID;
import static java.lang.Math.abs;
import static java.time.temporal.ChronoUnit.DAYS;
import static java.util.Collections.emptyList;
import static java.util.Optional.ofNullable;

@Slf4j
@Component
@RequiredArgsConstructor
public class LoanScoringValuesProvider implements ScoringValuesProvider {

    private static final String LAST_LOAN_PAID_PRINCIPAL = "last_loan_paid_principal";
    private static final String TIME_SINCE_LAST_CLOSED_LOAN = "time_since_last_closed_loan";

    protected static final String ALL_REPAID_HISTORIC_FEES_BY_LOANS = "all_repaid_historic_fees_by_loans";
    protected static final String ALL_REPAID_HISTORIC_PRINCIPAL_BY_LOANS = "all_repaid_historic_principal_by_loans";
    protected static final String ALL_REPAID_HISTORIC_INTEREST_BY_LOANS = "all_repaid_historic_interest_by_loans";
    protected static final String ALL_REPAID_HISTORIC_PENALTIES_BY_LOANS = "all_repaid_historic_penalties_by_loans";

    protected static final String ALL_LOANS_CREDIT_LIMIT = "all_loans_credit_limit";
    protected static final String ALL_LOANS_ISSUED_PERIOD = "all_loans_issued_period";
    protected static final String ALL_LOANS_ACTUAL_PERIOD = "all_loans_actual_period";

    protected static final String ALL_LOANS_HOURS_BETWEEN_ISSUE_DATE = "all_loans_hours_between_issue_date";
    protected static final String ALL_LOANS_HOURS_BETWEEN_ISSUE_DATE_AND_CLOSE_DATE = "all_loans_hours_between_issue_date_and_close_date";

    private final LoanService loanService;
    private final LoanApplicationService applicationService;

    @Override
    public Properties provide(long clientId) {
        Optional<LoanApplication> application = applicationService.findLatest(LoanApplicationQuery.byClientId(clientId, LoanApplicationStatus.OPEN));
        Optional<Loan> lastPaidLoan = loanService.findLastLoan(LoanQuery.paidLoans(clientId));
        Optional<Loan> lastClosedLoan = loanService.findLastLoan(LoanQuery.closedLoans(clientId));
        List<Loan> closedLoans = loanService.findLoans(LoanQuery.closedLoans(clientId).orderBy(ID, DESC));

        ScoringProperties properties = new ScoringProperties();
        properties.putAll(closedLoanValues(closedLoans));
        lastPaidLoan.ifPresent(loan ->
            properties.put(LAST_LOAN_PAID_PRINCIPAL, loan.getPrincipalPaid()));

        if (lastClosedLoan.isPresent() && application.isPresent()) {
            properties.put(TIME_SINCE_LAST_CLOSED_LOAN,
                daysBetween(lastClosedLoan.get().getCloseDate().atStartOfDay(), application.get().getCreatedAt()));
        }

        return properties;
    }

    @VisibleForTesting
    protected Properties closedLoanValues(List<Loan> maybeLoans) {
        List<Loan> loans = ofNullable(maybeLoans).orElse(emptyList());
        List<BigDecimal> feesPaid = new ArrayList<>(loans.size());
        List<BigDecimal> principalPaid = new ArrayList<>(loans.size());
        List<BigDecimal> interestPaid = new ArrayList<>(loans.size());
        List<BigDecimal> penaltiesPaid = new ArrayList<>(loans.size());
        List<BigDecimal> creditLimits = new ArrayList<>(loans.size());

        List<Long> periods = new ArrayList<>(loans.size());
        List<Long> actualPeriods = new ArrayList<>(loans.size());

        List<Long> hoursBetweenIssueDate = new ArrayList<>();
        List<Long> hoursBetweenIssueDateAndCloseDate = new ArrayList<>();

        Optional<Loan> prev = Optional.empty();
        for (Loan loan : loans) {
            Validate.notNull(loan.getCloseDate(), "Can't calculate Loan scoring properties for loan [%d]: empty close date", loan.getId());

            feesPaid.add(loan.getFeePaid());
            principalPaid.add(loan.getPrincipalPaid());
            interestPaid.add(loan.getInterestPaid());
            penaltiesPaid.add(loan.getPenaltyPaid());
            creditLimits.add(loan.getCreditLimit());

            periods.add(loan.getPeriodCount());
            actualPeriods.add(abs(DAYS.between(loan.getIssueDate(), loan.getCloseDate())));

            LocalDate prevIssueDate = prev.map(Loan::getIssueDate).orElse(TimeMachine.today());
            hoursBetweenIssueDate.add(abs(DAYS.between(prevIssueDate, loan.getIssueDate())) * 24);
            hoursBetweenIssueDateAndCloseDate.add(abs(DAYS.between(prevIssueDate, loan.getCloseDate())) * 24);

            prev = Optional.of(loan);
        }

        ScoringProperties properties = new ScoringProperties();
        properties.put(ALL_REPAID_HISTORIC_FEES_BY_LOANS, feesPaid);
        properties.put(ALL_REPAID_HISTORIC_PRINCIPAL_BY_LOANS, principalPaid);
        properties.put(ALL_REPAID_HISTORIC_INTEREST_BY_LOANS, interestPaid);
        properties.put(ALL_REPAID_HISTORIC_PENALTIES_BY_LOANS, penaltiesPaid);
        properties.put(ALL_LOANS_CREDIT_LIMIT, creditLimits);

        properties.put(ALL_LOANS_ISSUED_PERIOD, periods);
        properties.put(ALL_LOANS_ACTUAL_PERIOD, actualPeriods);

        properties.put(ALL_LOANS_HOURS_BETWEEN_ISSUE_DATE, hoursBetweenIssueDate);
        properties.put(ALL_LOANS_HOURS_BETWEEN_ISSUE_DATE_AND_CLOSE_DATE, hoursBetweenIssueDateAndCloseDate);
        return properties;
    }
}
