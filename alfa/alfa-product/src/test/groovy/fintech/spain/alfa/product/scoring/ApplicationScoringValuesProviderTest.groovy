package fintech.spain.alfa.product.scoring

import fintech.lending.core.application.LoanApplication
import fintech.lending.core.application.LoanApplicationService
import fintech.lending.core.application.LoanApplicationSourceType
import spock.lang.Specification
import spock.lang.Subject

import java.time.LocalDate

import static ApplicationScoringValuesProvider.*

class ApplicationScoringValuesProviderTest extends Specification {

    @Subject
    ApplicationScoringValuesProvider provider

    def "setup"() {
        def loanApplicationService = Stub(LoanApplicationService)
        provider = new ApplicationScoringValuesProvider(loanApplicationService)
    }

    def "should return empty values when applications null or empty"() {
        expect:
        provider.allPreviousApplicationsValues(applications).every { (it.value as List).isEmpty() }

        where:
        applications << [null, []]
    }

    def "should return valid values for applications"() {
        given:
        def submitted = LocalDate.of(2019, 7, 4).atStartOfDay()

        def applications = [
            new LoanApplication(requestedPrincipal: 100, score: 10, scoreSource: 'FINTECH_MARKET', requestedPeriodCount: 10, submittedAt: submitted),
            new LoanApplication(requestedPrincipal: 200, score: 20, scoreSource: 'FINTECH_MARKET', requestedPeriodCount: 20, submittedAt: submitted.plusDays(2)),
            new LoanApplication(requestedPrincipal: 300, score: 30, scoreSource: 'FINTECH_MARKET', requestedPeriodCount: 30, submittedAt: submitted.plusDays(3)),
            new LoanApplication(requestedPrincipal: 400, score: 40, requestedPeriodCount: 40, submittedAt: submitted.plusDays(4))
        ]

        when:
        def values = provider.allPreviousApplicationsValues(applications)

        then:
        values[ALL_PREVIOUS_APPLICATIONS_PREFIX + '_' + REQUESTED_PRINCIPAL_LIST] == [100, 200, 300, 400]
        values[ALL_PREVIOUS_APPLICATIONS_PREFIX + '_' + SCORES_LIST] == [10, 20, 30]
        values[ALL_PREVIOUS_APPLICATIONS_PREFIX + '_' + REQUESTED_TERM_LIST] == [10, 20, 30, 40]
        values[ALL_PREVIOUS_APPLICATIONS_PREFIX + '_' + HOURS_BETWEEN_SUBMITS] == [48, 24, 24]
    }

    def "Affiliate values"() {
        given:
        def submitted = LocalDate.of(2019, 7, 4).atStartOfDay()
        def applications = [
            new LoanApplication(requestedPrincipal: 300, sourceType: LoanApplicationSourceType.ORGANIC, sourceName: null, requestedPeriodCount: 30, submittedAt: submitted.plusDays(3)),
        ]

        when:
        def noApplications = provider.affiliateValues(applications)

        then:
        noApplications[AFFILIATE_VARIABLES_PREFIX + "_" + FIRST_AFFILIATE] == "ORGANIC"
        noApplications[AFFILIATE_VARIABLES_PREFIX + "_" + FIRST_CLICK] == "ORGANIC"
        noApplications[AFFILIATE_VARIABLES_PREFIX + "_" + LAST_AFFILIATE] == "ORGANIC"
        noApplications[AFFILIATE_VARIABLES_PREFIX + "_" + LAST_CLICK] == "ORGANIC"
        noApplications[AFFILIATE_VARIABLES_PREFIX + "_" + COUNT_AFFILIATE] == 0
        noApplications[AFFILIATE_VARIABLES_PREFIX + "_" + COUNT_DISTINCT_AFFILIATE] == 0
        noApplications[AFFILIATE_VARIABLES_PREFIX + "_" + COUNT_PREVIOUS_APPS] == 0
        noApplications[AFFILIATE_VARIABLES_PREFIX + "_" + COUNT_CREDY2] == 0
        noApplications[AFFILIATE_VARIABLES_PREFIX + "_" + COUNT_SOLCREDITO] == 0


        applications.addAll([
            new LoanApplication(requestedPrincipal: 100, sourceType: LoanApplicationSourceType.AFFILIATE, sourceName: 'credy2', requestedPeriodCount: 10, submittedAt: submitted),
            new LoanApplication(requestedPrincipal: 200, sourceType: LoanApplicationSourceType.AFFILIATE, sourceName: 'solcredito', requestedPeriodCount: 20, submittedAt: submitted.plusDays(2)),
            new LoanApplication(requestedPrincipal: 300, sourceType: LoanApplicationSourceType.ORGANIC, sourceName: null, requestedPeriodCount: 30, submittedAt: submitted.plusDays(4)),
            new LoanApplication(requestedPrincipal: 300, sourceType: LoanApplicationSourceType.AFFILIATE, sourceName: 'solcredito', requestedPeriodCount: 30, submittedAt: submitted.plusDays(5))
        ])

        when:
        def values = provider.affiliateValues(applications)

        then:
        values[AFFILIATE_VARIABLES_PREFIX + "_" + FIRST_AFFILIATE] == "credy2"
        values[AFFILIATE_VARIABLES_PREFIX + "_" + FIRST_CLICK] == "credy2"
        values[AFFILIATE_VARIABLES_PREFIX + "_" + LAST_AFFILIATE] == "solcredito"
        values[AFFILIATE_VARIABLES_PREFIX + "_" + LAST_CLICK] == "solcredito"
        values[AFFILIATE_VARIABLES_PREFIX + "_" + COUNT_AFFILIATE] == 3
        values[AFFILIATE_VARIABLES_PREFIX + "_" + COUNT_DISTINCT_AFFILIATE] == 2
        values[AFFILIATE_VARIABLES_PREFIX + "_" + COUNT_PREVIOUS_APPS] == 4
        values[AFFILIATE_VARIABLES_PREFIX + "_" + COUNT_CREDY2] == 1
        values[AFFILIATE_VARIABLES_PREFIX + "_" + COUNT_SOLCREDITO] == 2
    }

}
