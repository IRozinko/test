package fintech.spain.alfa.product.acceptance

import fintech.TimeMachine
import fintech.spain.alfa.product.AbstractAlfaTest

import org.springframework.beans.factory.annotation.Autowired

import static fintech.BigDecimalUtils.amount
import static fintech.TimeMachine.today

class AcceptancePenaltiesTest extends AbstractAlfaTest {

    @Autowired
    PenaltyTestCases penaltyTestCases

    def "no penalties on dpd 1"() {
        when:
        def loan = penaltyTestCases.penaltiesOnDpd(1)

        then:
        with(loan.balance) {
            assert penaltyApplied == 0
            assert totalDue == 235.00 + penaltyApplied
        }
    }

    def "no penalties on dpd 2"() {
        when:
        def loan = penaltyTestCases.penaltiesOnDpd(2)

        then:
        with(loan.balance) {
            assert penaltyApplied == 0
            assert totalDue == 235.00 + penaltyApplied
        }
    }

    def "penalties on dpd 3"() {
        when:
        def loan = penaltyTestCases.penaltiesOnDpd(3)

        then:
        with(loan.balance) {
            assert penaltyApplied == 7.05
            assert totalDue == 235.00 + 7.05
        }
    }

    def "penalties on dpd 15"() {
        when:
        def loan = penaltyTestCases.penaltiesOnDpd(15)

        then:
        with(loan.balance) {
            assert penaltyApplied == 35.25
            assert totalDue == 235.00 + 35.25
        }
    }

    def "penalties calculated on outstanding amounts (short version)"() {
        when:
        def loan = penaltyTestCases.penaltiesCalculatedOnOutstandingAmounts()

        then:
        with(loan.balance) {
            assert penaltyDue == 5.97
            assert totalDue == 199.00 + 5.97
        }
    }

    def "penalties calculated on outstanding amounts (long version)"() {
        given:
        def today = today()
        def term = 15L
        def dpd1 = 15
        def dpd2 = 16
        def dpd3 = 18
        def dpdTotal = 18
        def issueDate = today.minusDays(term).minusDays(dpdTotal)
        def penaltyDate1 = issueDate.plusDays(term).plusDays(dpd1)
        def paymentDate1 = penaltyDate1

        def penaltyDate2 = issueDate.plusDays(term).plusDays(dpd2)
        def penaltyDate3 = issueDate.plusDays(term).plusDays(dpd3)

        when:
        TimeMachine.useFixedClockAt(paymentDate1)
        def loan = fintech.spain.alfa.product.testing.TestFactory.newClient()
            .registerDirectly()
            .issueActiveLoan(amount(200), 15L, issueDate)
            .applyPenalty(penaltyDate1)

        then:
        with(loan.balance) {
            assert penaltyDue == 35.25
            assert totalDue == 235.00 + 35.25
        }

        when:
        loan.repay(71.25, paymentDate1)

        then:
        TimeMachine.useDefaultClock()
        with(loan.balance) {
            assert totalPaid == 71.25
            assert penaltyDue == 0.0
            assert interestDue == 0.00
            assert principalDue == 199.00
            assert totalDue == 199.00
        }

        when:
        loan.applyPenalty(penaltyDate2)

        then:
        with(loan.balance) {
            assert penaltyDue == 1.99
            assert totalDue == 199.00 + 1.99
        }

        when:
        loan.applyPenalty(penaltyDate3)

        then:
        with(loan.balance) {
            assert penaltyDue == 5.97
            assert totalDue == 199.00 + 5.97
        }
    }

    def "penalty limit reached (short version)"() {
        when:
        def loan = penaltyTestCases.penaltyLimitReached()

        then:
        with(loan.balance) {
            assert feePaid == 52.00
            assert penaltyPaid == 35.25
            assert interestPaid == 35.00
            assert principalPaid == 35.00
            assert penaltyDue == 212.25
            assert totalDue == 165.00 + 212.25
        }
    }

    def "penalty limit reached (long version)"() {
        when:
        def term = 15L
        def dpd1 = 15
        def issueDate = today().minusDays(term).minusDays(dpd1)
        def penaltyDate = issueDate.plusDays(term).plusDays(dpd1)
        def paymentDate = penaltyDate

        def paymentDueDate = penaltyDate.plusDays(15)

        def loan = fintech.spain.alfa.product.testing.TestFactory.newClient()
            .registerDirectly()
            .issueActiveLoan(amount(200), term, issueDate)
            .applyPenalty(penaltyDate)

        then:
        with(loan.balance) {
            assert penaltyDue == 35.25
            assert totalDue == 235.00 + 35.25
        }

        when:
        loan.repay(105.25, paymentDate)

        then:
        with(loan.balance) {
            assert totalPaid == 105.25
            assert penaltyPaid == 35.25
            assert interestPaid == 35.00
            assert principalPaid == 35.00
            assert totalDue == 165.00
        }

        when:
        loan.extend(52.00, paymentDate)

        then:
        assert loan.balance.feePaid == 52.00
        assert loan.getLoan().paymentDueDate == paymentDueDate

        when:
        loan.applyPenalty(paymentDueDate.plusDays(21L))

        then:
        assert loan.balance.penaltyDue == 0.00

        when:
        loan.applyPenalty(paymentDueDate.plusDays(22L))

        then:
        assert loan.balance.penaltyDue == 1.05

        when:
        loan.applyPenalty(paymentDueDate.plusDays(100L))

        then:
        assert loan.balance.penaltyDue == 129.75

        when:
        loan.applyPenalty(paymentDueDate.plusDays(150L))

        then:
        assert loan.balance.penaltyDue == 212.25
    }

}
