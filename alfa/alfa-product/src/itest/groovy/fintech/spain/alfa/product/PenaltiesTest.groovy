package fintech.spain.alfa.product

import fintech.TimeMachine
import fintech.lending.core.loan.LoanStatusDetail
import org.springframework.beans.factory.annotation.Autowired

import java.time.LocalDate

import static fintech.DateUtils.date
import static fintech.spain.alfa.product.testing.TestLoan.expectedExtensionPrice

class PenaltiesTest extends AbstractAlfaTest {

    @Autowired
    private fintech.spain.alfa.product.testing.acceptance.DcTestCases dcTestCases

    def "penalty not applied if loan is not overdue"() {
        given:
        def loan = fintech.spain.alfa.product.testing.TestFactory.newClient().registerDirectly()
            .issueActiveLoan(300.00, 30, date("2018-01-01"))

        expect:
        with(loan.getBalance()) {
            assert principalDue == 300.00
            assert interestDue == 105.00
            assert penaltyDue == 0.00
            assert totalDue == 405.00
        }

        when:
        loan.applyPenalty(date("2018-01-01"))

        then:
        assert loan.getBalance().penaltyApplied == 0.00
    }

    def "penalty not applied if loan in grace period"() {
        given:
        def loan = fintech.spain.alfa.product.testing.TestFactory.newClient().registerDirectly()
            .issueActiveLoan(300.00, 30, date("2018-01-01"))

        when:
        loan.applyPenalty(loan.getGracePeriodEndDate())

        then:
        assert loan.getBalance().penaltyApplied == 0.00
    }

    def "penalty applied after grace period"() {
        given:
        def loan = fintech.spain.alfa.product.testing.TestFactory.newClient().registerDirectly()
            .issueActiveLoan(300.00, 30, date("2018-01-01"))

        when:
        loan.applyPenalty(loan.getGracePeriodEndDate().plusDays(1))

        then:
        assert loan.getBalance().penaltyApplied == 4.05 * (AlfaConstants.GRACE_PERIOD_IN_DAYS + 1)

        when:
        loan.applyPenalty(loan.getGracePeriodEndDate().plusDays(2))

        then:
        assert loan.getBalance().penaltyApplied == 4.05 * (AlfaConstants.GRACE_PERIOD_IN_DAYS + 2)
    }

    def "penalty not applied twice"() {
        given:
        def loan = fintech.spain.alfa.product.testing.TestFactory.newClient().registerDirectly()
            .issueActiveLoan(300.00, 30, date("2018-01-01"))

        when:
        loan.applyPenalty(loan.getGracePeriodEndDate().plusDays(9))
        loan.applyPenalty(loan.getGracePeriodEndDate().plusDays(10))
        loan.applyPenalty(loan.getGracePeriodEndDate().plusDays(8))

        then:
        assert loan.getBalance().penaltyApplied == 4.05 * (AlfaConstants.GRACE_PERIOD_IN_DAYS + 10)
    }

    def "penalty repayment"() {
        given:
        def issueDate = TimeMachine.today().minusDays(30)
        def loan = fintech.spain.alfa.product.testing.TestFactory.newClient().registerDirectly()
            .issueActiveLoan(300.00, 30, issueDate)

        when:
        loan.applyPenalty(loan.getGracePeriodEndDate().plusDays(1))

        then:
        assert loan.getBalance().penaltyDue == 4.05 * (AlfaConstants.GRACE_PERIOD_IN_DAYS + 1)
        assert loan.getBalance().totalDue == 300.00 + 105.00 + 4.05 * (AlfaConstants.GRACE_PERIOD_IN_DAYS + 1)

        when:
        loan.repay(1.00, loan.getGracePeriodEndDate().plusDays(1))

        then:
        assert loan.getBalance().penaltyDue == 4.05 * (AlfaConstants.GRACE_PERIOD_IN_DAYS + 1) - 1.00
        assert loan.getBalance().totalDue == 300.00 + 105.00 + 4.05 * (AlfaConstants.GRACE_PERIOD_IN_DAYS + 1) - 1.00

        when:
        loan.repayAll(loan.getGracePeriodEndDate().plusDays(1))

        then:
        assert loan.getBalance().totalDue == 0.00
    }

    def "penalty repayment taken into account when applying new penalties"() {
        given:
        def issueDate = TimeMachine.today().minusDays(40)
        def loan = fintech.spain.alfa.product.testing.TestFactory.newClient().registerDirectly()
            .issueActiveLoan(300.00, 30, issueDate)
        loan.applyPenalty(loan.getGracePeriodEndDate().plusDays(10))

        when:
        loan.repay(loan.getBalance().penaltyDue, loan.getGracePeriodEndDate().plusDays(10))

        then:
        assert loan.getBalance().penaltyDue == 0.0

        when:
        loan.applyPenalty(loan.getGracePeriodEndDate().plusDays(11))

        then:
        assert loan.getBalance().penaltyDue == 4.05
    }

    def "penalty write-off taken into account when applying new penalties"() {
        given:
        def loan = fintech.spain.alfa.product.testing.TestFactory.newClient().registerDirectly()
            .issueActiveLoan(300.00, 30, date("2018-01-01"))
        loan.applyPenalty(loan.getGracePeriodEndDate().plusDays(10))

        when:
        loan.writeOffPenalty(loan.getGracePeriodEndDate().plusDays(11), loan.getBalance().penaltyDue)

        then:
        assert loan.getBalance().penaltyDue == 0.0

        when:
        loan.applyPenalty(loan.getGracePeriodEndDate().plusDays(11))

        then:
        assert loan.getBalance().penaltyDue == 4.05
    }

    def "max penalty limit is respected"() {
        given:
        def loan = fintech.spain.alfa.product.testing.TestFactory.newClient().registerDirectly()
            .issueActiveLoan(300.00, 30, date("2018-01-01"))

        when:
        loan.applyPenalty(loan.getGracePeriodEndDate().plusDays(100))

        then:
        assert loan.getBalance().penaltyDue == 345.00

        when:
        loan.applyPenalty(loan.getGracePeriodEndDate().plusDays(101))

        then:
        assert loan.getBalance().penaltyDue == 345.00
    }

    def "penalty after extension"() {
        given:
        def loan = fintech.spain.alfa.product.testing.TestFactory.newClient().registerDirectly()
            .issueActiveLoan(300.00, 30, date("2018-01-01"))
        def initialPenaltyDate = loan.getGracePeriodEndDate().plusDays(1);

        when:
        loan.applyPenalty(initialPenaltyDate)
        def initialPenaltyDue = loan.getBalance().penaltyDue;

        then:
        assert initialPenaltyDue == 4.05 * (AlfaConstants.GRACE_PERIOD_IN_DAYS + 1)

        when:
        loan.extend(expectedExtensionPrice(300.00, 30), loan.getLoan().getMaturityDate())
        loan.applyPenalty(initialPenaltyDate.plusDays(5))

        then: "penalties reversed after extensions"
        assert loan.getBalance().penaltyDue == 0.00
        assert loan.getBalance().penaltyOutstanding == 0.00

        when:
        def a = loan.getGracePeriodEndDate().plusDays(1)
        println loan.transactions()
        loan.applyPenalty(a)

        then: "new penalties generated"
        assert loan.getBalance().penaltyDue > 0
    }

    def "Penalties are calculated for ACTIVE loan after rescheduler broken"() {
        when:
        def loan = dcTestCases.rescheduleBroken()

        then:
        loan.statusDetail == LoanStatusDetail.ACTIVE
        loan.getBalance().penaltyDue != 0.00

        when:
        def oldPenalties = loan.getBalance().penaltyDue
        TimeMachine.useFixedClockAt(LocalDate.now().plusDays(2))
        loan.applyPenalty(TimeMachine.today())

        then:
        loan.statusDetail == LoanStatusDetail.ACTIVE
        loan.getBalance().penaltyDue > oldPenalties
    }
}
