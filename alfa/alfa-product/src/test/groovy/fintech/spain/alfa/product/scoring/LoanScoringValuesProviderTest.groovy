package fintech.spain.alfa.product.scoring

import fintech.TimeMachine
import fintech.lending.core.application.LoanApplicationService
import fintech.lending.core.loan.Loan
import fintech.lending.core.loan.LoanService
import spock.lang.Specification
import spock.lang.Subject

import static LoanScoringValuesProvider.*

class LoanScoringValuesProviderTest extends Specification {

    @Subject
    LoanScoringValuesProvider provider

    def "setup"() {
        provider = new LoanScoringValuesProvider(Stub(LoanService), Stub(LoanApplicationService))
    }

    def "should return empty values when loans null or empty"() {
        expect:
        provider.closedLoanValues(loans).every { (it.value as List).isEmpty() }

        where:
        loans << [null, []]
    }

    def "should throw exception when loan close date is null"() {
        given:
        def loans = [new Loan(feePaid: 40, principalPaid: 400, interestPaid: 40, penaltyPaid: 40, creditLimit: 400, periodCount: 40, issueDate: TimeMachine.today())]

        when:
        provider.closedLoanValues(loans).every { (it.value as List).isEmpty() }

        then:
        thrown NullPointerException
    }

    def "should return valid values for loans"() {
        given:
        def closeDate = TimeMachine.today().minusDays(50)
        def issueDate = closeDate.minusDays(10)

        def loans = [
            new Loan(feePaid: 40, principalPaid: 400, interestPaid: 40, penaltyPaid: 40, creditLimit: 400, periodCount: 40, issueDate: closeDate.plusDays(40), closeDate: closeDate.plusDays(55)),
            new Loan(feePaid: 30, principalPaid: 300, interestPaid: 30, penaltyPaid: 30, creditLimit: 300, periodCount: 30, issueDate: closeDate.plusDays(25), closeDate: closeDate.plusDays(30)),
            new Loan(feePaid: 20, principalPaid: 200, interestPaid: 20, penaltyPaid: 20, creditLimit: 200, periodCount: 20, issueDate: closeDate, closeDate: closeDate.plusDays(10)),
            new Loan(feePaid: 10, principalPaid: 100, interestPaid: 10, penaltyPaid: 10, creditLimit: 100, periodCount: 10, issueDate: issueDate, closeDate: closeDate)
        ]

        when:
        def values = provider.closedLoanValues(loans)

        then:
        values[ALL_REPAID_HISTORIC_FEES_BY_LOANS] == [40, 30, 20, 10]
        values[ALL_REPAID_HISTORIC_PRINCIPAL_BY_LOANS] == [400, 300, 200, 100]
        values[ALL_REPAID_HISTORIC_INTEREST_BY_LOANS] == [40, 30, 20, 10]
        values[ALL_REPAID_HISTORIC_PENALTIES_BY_LOANS] == [40, 30, 20, 10]

        values[ALL_LOANS_CREDIT_LIMIT] == [400, 300, 200, 100]
        values[ALL_LOANS_ISSUED_PERIOD] == [40, 30, 20, 10]
        values[ALL_LOANS_ACTUAL_PERIOD] == [15, 5, 10, 10]

        values[ALL_LOANS_HOURS_BETWEEN_ISSUE_DATE] == [240, 360, 600, 240]
        values[ALL_LOANS_HOURS_BETWEEN_ISSUE_DATE_AND_CLOSE_DATE] == [120, 240, 360, 0]
    }
}
