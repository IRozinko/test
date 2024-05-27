package fintech.spain.alfa.product.scoring;


import fintech.ScoringProperties;
import fintech.lending.core.application.LoanApplication;
import fintech.lending.core.application.LoanApplicationService;
import fintech.lending.core.application.LoanApplicationSourceType;
import fintech.lending.core.application.LoanApplicationStatus;
import fintech.scoring.values.spi.ScoringValuesProvider;
import fintech.spain.scoring.model.ScoringModelType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.Properties;
import java.util.stream.Collectors;

import static com.google.common.collect.ImmutableList.of;
import static fintech.lending.core.application.LoanApplicationQuery.byClientId;
import static fintech.lending.core.application.LoanApplicationStatus.CLOSED;
import static fintech.lending.core.application.LoanApplicationStatusDetail.APPROVED;
import static java.time.temporal.ChronoUnit.HOURS;
import static java.util.Collections.emptyList;
import static java.util.Optional.ofNullable;

@Slf4j
@Component
@RequiredArgsConstructor
public class ApplicationScoringValuesProvider implements ScoringValuesProvider {

    static final String THIS_APPLICATION_PREFIX = "this_application";
    static final String ALL_PREVIOUS_APPLICATIONS_PREFIX = "all_previous_applications";
    static final String ALL_PREVIOUS_APPROVED_APPLICATIONS_PREFIX = "all_previous_approved_applications";
    static final String AFFILIATE_VARIABLES_PREFIX = "aff";

    static final String CALENDAR_DAY = "calendar_day";
    static final String REQUESTED_PRINCIPAL = "requested_principal";
    static final String REQUESTED_TERM = "requested_term";
    static final String SCORE = "score";

    static final String REQUESTED_PRINCIPAL_LIST = "requested_principal_list";
    static final String OFFERED_PRINCIPAL_LIST = "offered_principal_list";
    static final String SCORES_LIST = "scores_list";
    static final String REQUESTED_TERM_LIST = "requested_term_list";
    static final String HOURS_BETWEEN_SUBMITS = "hours_between_submits";

    static final String FIRST_AFFILIATE = "first_affiliate";
    static final String FIRST_CLICK = "first_click";
    static final String LAST_AFFILIATE = "last_affiliate";
    static final String LAST_CLICK = "last_click";
    static final String COUNT_AFFILIATE = "count_affiliate";
    static final String COUNT_DISTINCT_AFFILIATE = "count_distinct_affiliate";
    static final String COUNT_PREVIOUS_APPS = "count_previous_apps";
    static final String COUNT_CREDY2 = "count_credy2";
    static final String COUNT_SOLCREDITO = "count_solcredito";

    private static final String SOURCE_ORGANIC = "ORGANIC";
    private static final String SOURCE_CREDY2 = "credy2";
    private static final String SOURCE_SOLCREDITO = "solcredito";

    private final LoanApplicationService applicationService;

    @Override
    public Properties provide(long clientId) {
        List<LoanApplication> applications = applicationService.find(byClientId(clientId));
        List<LoanApplication> approvedApplications = applicationService
            .find(byClientId(clientId, of(APPROVED), CLOSED));

        ScoringProperties properties = new ScoringProperties();
        properties.putAll(allPreviousApplicationsValues(applications));
        properties.putAll(approvedApplicationsValues(approvedApplications));
        properties.putAll(affiliateValues(applications));

        applicationService.findFirst(byClientId(clientId, LoanApplicationStatus.OPEN))
            .map(this::thisApplicationValues)
            .ifPresent(properties::putAll);

        return properties;
    }

    private Properties thisApplicationValues(LoanApplication application) {
        ScoringProperties p = new ScoringProperties(THIS_APPLICATION_PREFIX);
        p.put(CALENDAR_DAY, application.getCreatedAt().getDayOfMonth());
        p.put(REQUESTED_PRINCIPAL, application.getRequestedPrincipal());
        p.put(REQUESTED_TERM, application.getRequestedPeriodCount());
        p.put(SCORE, application.getScore());
        return p;
    }


    Properties allPreviousApplicationsValues(List<LoanApplication> maybeApplications) {
        List<LoanApplication> applications = ofNullable(maybeApplications).orElse(emptyList());
        List<BigDecimal> principals = new ArrayList<>();
        List<BigDecimal> scores = new ArrayList<>();
        List<Long> periods = new ArrayList<>();
        List<Long> timeBetweenSubmitsInHours = new ArrayList<>();

        Optional<LoanApplication> previous = Optional.empty();
        for (LoanApplication app : applications) {
            principals.add(app.getRequestedPrincipal());
            periods.add(app.getRequestedPeriodCount());

            if (ScoringModelType.FINTECH_MARKET.name().equals(app.getScoreSource())) {
                scores.add(app.getScore());
            }
            previous.ifPresent(prev -> timeBetweenSubmitsInHours.add(HOURS.between(prev.getSubmittedAt(), app.getSubmittedAt())));

            previous = Optional.of(app);
        }

        ScoringProperties properties = new ScoringProperties(ALL_PREVIOUS_APPLICATIONS_PREFIX);
        properties.put(REQUESTED_PRINCIPAL_LIST, principals);
        properties.put(SCORES_LIST, scores);
        properties.put(REQUESTED_TERM_LIST, periods);
        properties.put(HOURS_BETWEEN_SUBMITS, timeBetweenSubmitsInHours);
        return properties;
    }

    private Properties approvedApplicationsValues(List<LoanApplication> applications) {

        List<BigDecimal> approvedPrincipals = new ArrayList<>();
        List<BigDecimal> approvedOfferedPrincipals = new ArrayList<>();
        List<BigDecimal> approvedScores = new ArrayList<>();

        for (LoanApplication app : applications) {
            approvedPrincipals.add(app.getRequestedPrincipal());
            approvedOfferedPrincipals.add(app.getOfferedPrincipal());
            if (ScoringModelType.FINTECH_MARKET.name().equals(app.getScoreSource())) {
                approvedScores.add(app.getScore());
            }
        }

        ScoringProperties properties = new ScoringProperties(ALL_PREVIOUS_APPROVED_APPLICATIONS_PREFIX);
        properties.put(REQUESTED_PRINCIPAL_LIST, approvedPrincipals);
        properties.put(OFFERED_PRINCIPAL_LIST, approvedOfferedPrincipals);
        properties.put(SCORES_LIST, approvedScores);
        return properties;
    }

    Properties affiliateValues(List<LoanApplication> allApplications) {
        LinkedList<LoanApplication> applications = allApplications.stream()
            .sorted(Comparator.comparing(LoanApplication::getSubmittedAt))
            .collect(Collectors.toCollection(LinkedList::new));

        LinkedList<LoanApplication> affiliatedApplications = allApplications.stream()
            .filter(a -> a.getSourceType() == LoanApplicationSourceType.AFFILIATE)
            .sorted(Comparator.comparing(LoanApplication::getSubmittedAt))
            .collect(Collectors.toCollection(LinkedList::new));

        ScoringProperties properties = new ScoringProperties(AFFILIATE_VARIABLES_PREFIX);
        properties.put(FIRST_AFFILIATE, first(affiliatedApplications)
            .map(LoanApplication::getSourceName)
            .orElse(SOURCE_ORGANIC));
        properties.put(FIRST_CLICK, first(applications)
            .map(LoanApplication::getSourceName)
            .orElse(SOURCE_ORGANIC));
        properties.put(LAST_AFFILIATE, last(affiliatedApplications)
            .map(LoanApplication::getSourceName)
            .orElse(SOURCE_ORGANIC));
        properties.put(LAST_CLICK, last(applications)
            .map(LoanApplication::getSourceName)
            .orElse(SOURCE_ORGANIC));
        properties.put(COUNT_AFFILIATE, affiliatedApplications.size());
        properties.put(COUNT_DISTINCT_AFFILIATE, affiliatedApplications.stream()
            .map(LoanApplication::getSourceName)
            .distinct()
            .count());
        properties.put(COUNT_PREVIOUS_APPS, applications.size() - 1);
        properties.put(COUNT_CREDY2, affiliatedApplications.stream()
            .filter(a -> SOURCE_CREDY2.equals(a.getSourceName()))
            .count());
        properties.put(COUNT_SOLCREDITO, affiliatedApplications.stream()
            .filter(a -> SOURCE_SOLCREDITO.equals(a.getSourceName()))
            .count());
        return properties;
    }

    private Optional<LoanApplication> first(LinkedList<LoanApplication> loanApplications) {
        if (loanApplications.isEmpty()) {
            return Optional.empty();
        } else {
            return Optional.of(loanApplications.getFirst());
        }
    }

    private Optional<LoanApplication> last(LinkedList<LoanApplication> loanApplications) {
        if (loanApplications.isEmpty()) {
            return Optional.empty();
        } else {
            return Optional.of(loanApplications.getLast());
        }
    }

}
