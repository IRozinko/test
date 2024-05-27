package fintech.spain.alfa.product.acceptance

import fintech.BigDecimalUtils
import fintech.TimeMachine
import fintech.lending.core.loan.LoanStatusDetail
import fintech.spain.alfa.product.AbstractAlfaTest
import fintech.spain.alfa.product.AlfaConstants
import fintech.spain.alfa.product.lending.spi.AlfaLoanDerivedValuesResolver

import fintech.transactions.TransactionQuery
import fintech.transactions.TransactionType
import org.springframework.beans.factory.annotation.Autowired
import spock.lang.Unroll

import static fintech.BigDecimalUtils.amount
import static fintech.DateUtils.date

class AcceptancePaymentsTest extends AbstractAlfaTest {

    @Autowired
    PaymentTestCases paymentTestCases

    @Autowired
    AlfaLoanDerivedValuesResolver resolver

    def "on time payment"() {
        when:
        def loan = paymentTestCases.onTimePayment()

        then:
        with(loan.balance) {
            assert principalPaid == 200.00
            assert interestPaid == 35.00
            assert totalDue == 0.00
        }

        and:
        assert loan.statusDetail == LoanStatusDetail.PAID
    }

    def "partial payment basic"() {
        when:
        def loan = paymentTestCases.partialPaymentBasic()

        then:
        with(loan.balance) {
            assert principalPaid == 165.00
            assert principalDue == 35.00
            assert interestPaid == 35.00
            assert interestDue == 0.00
            assert totalDue == 35.00
        }

        and:
        assert loan.statusDetail == LoanStatusDetail.ACTIVE
    }

    def "total debt payment"() {
        when:
        def loan = paymentTestCases.totalDebtPayment()

        then:
        with(loan.balance) {
            assert penaltyApplied == 35.25
            assert penaltyPaid == 35.25
            assert interestPaid == 35.00
            assert principalPaid == 200.00
            assert totalDue == 0.00
        }

        and:
        assert loan.statusDetail == LoanStatusDetail.PAID
    }

    def "only penalty full payment"() {
        when:
        def loan = paymentTestCases.onlyPenaltyFullPayment()

        then:
        with(loan.balance) {
            assert penaltyApplied == 35.25
            assert penaltyPaid == 35.25
            assert penaltyDue == 0.00
            assert totalDue == 235.00
        }

        and:
        assert loan.statusDetail == LoanStatusDetail.ACTIVE

        when:
        loan.applyPenalty(loan.getLoan().getPaymentDueDate().plusDays(60))

        then:
        with(loan.balance) {
            assert penaltyDue > 0.00
        }
    }

    def "penalty, interest and principal payment"() {
        when:
        def loan = paymentTestCases.penaltyInterestAndPrincipalPayment()

        then:
        with(loan.balance) {
            assert penaltyPaid == 35.25
            assert interestPaid == 35.00
            assert principalPaid == 1.01
            assert totalDue == 198.99
        }
    }

    def "only penalty partial payment"() {
        when:
        def loan = paymentTestCases.onlyPenaltyPartialPayment()

        then:
        with(loan.balance) {
            assert penaltyPaid == 30.10
            assert penaltyDue == 5.15
            assert totalDue == 240.15
        }
    }

    @Unroll
    def "Payment calculation (#testName) with dpd=#dpdDays and gracePeriod=#gracePeriod"() {
        when:
        def loan = paymentTestCases.withPenaltiesForDpdDays(dpdDays, repaidDays)
        def numberOfCalculatedPenaltiesDays = (repaidDays > gracePeriod ? repaidDays : 0)
        then:
        with(loan.balance) {
            assert overpaymentReceived == (2.35 * repaidDays) - (2.35 * numberOfCalculatedPenaltiesDays)
            assert penaltyApplied == 2.35 * numberOfCalculatedPenaltiesDays
            assert penaltyPaid == 2.35 * (repaidDays > gracePeriod ? repaidDays : 0)
            assert interestPaid == 35.00
            assert principalPaid == 200.00
            assert penaltyDue == BigDecimalUtils.max(BigDecimal.ZERO, 2.35 * numberOfCalculatedPenaltiesDays - 2.35 * repaidDays)
            assert totalDue == BigDecimalUtils.max(BigDecimal.ZERO, 2.35 * numberOfCalculatedPenaltiesDays - 2.35 * repaidDays)
        }

        and:
        assert loan.statusDetail == loanStatus
        where:
        testName                                     | dpdDays | repaidDays | gracePeriod                           | loanStatus
        "Dpd is in grace period"                     | 1       | 1          | AlfaConstants.GRACE_PERIOD_IN_DAYS | LoanStatusDetail.PAID
        "Dpd is in grace period"                     | 2       | 2          | AlfaConstants.GRACE_PERIOD_IN_DAYS | LoanStatusDetail.PAID
        "Dpd is not in grace period"                 | 3       | 3          | AlfaConstants.GRACE_PERIOD_IN_DAYS | LoanStatusDetail.PAID
        "Dpd is not in grace period"                 | 4       | 4          | AlfaConstants.GRACE_PERIOD_IN_DAYS | LoanStatusDetail.PAID
        "Dpd is not in grace period with re-payment" | 4       | 2          | AlfaConstants.GRACE_PERIOD_IN_DAYS | LoanStatusDetail.PAID

    }

    def "full payment in advance"() {
        when:
        def loan = paymentTestCases.fullPaymentInAdvance()

        then:
        with(loan.balance) {
            assert principalPaid == 200.00
            assert interestPaid == 30.00
            assert totalDue == 0.00
        }

        and:
        assert loan.statusDetail == LoanStatusDetail.PAID
    }

    def "late prepayment"() {
        when:
        def loan = paymentTestCases.latePrepayment()

        then:
        with(loan.balance) {
            assert principalPaid == 166.00
            assert interestPaid == 70.00
            assert totalDue == 34.00
        }

        and:
        assert loan.statusDetail == LoanStatusDetail.ACTIVE
    }

    def "pre payment with 15% discount"() {
        when:
        def loan = paymentTestCases.prePayment15Discount()

        then:
        with(loan.balance) {
            assert interestApplied == 31.00
            assert principalPaid == 210.00
            assert interestPaid == 10.33
            assert interestWrittenOff == 31.00 - 10.33
            assert interestDue == 0.00
            assert feePaid == 210 * 0.005
            assert totalDue == 0.00
        }

        and:
        assert loan.statusDetail == LoanStatusDetail.PAID
    }

    def "pre payment on loan issue date"() {
        when:
        def loan = paymentTestCases.prePaymentOnLoanIssueDate()

        then:
        def balance = loan.getBalance()
        with(loan.balance) {
            assert principalPaid == 270.00
            assert interestPaid == 0.00
            assert interestWrittenOff == 95.00
            assert interestDue == 0.00
            assert feePaid == 1.35
            assert totalDue == 0.00
        }

        and:
        assert loan.statusDetail == LoanStatusDetail.PAID
    }

    def "pre payment 1 day later"() {
        when:
        def loan = paymentTestCases.prePayment1DayLater()

        then:
        with(loan.balance) {
            assert interestApplied == 31.00
            assert principalPaid == 210.00
            assert interestPaid == 10.33
            assert interestWrittenOff == 31.00 - 10.33
            assert interestDue == 0.00
            assert feePaid == 210 * 0.005
            assert totalDue == 0.00
        }

        and:
        assert loan.statusDetail == LoanStatusDetail.PAID
    }

    def "pre payment 3 days later"() {
        when:
        def loan = paymentTestCases.prePayment3DaysLater()

        then:
        with(loan.balance) {
            assert interestApplied == 31.00
            assert principalPaid == 210.00
            assert interestPaid == 10.33
            assert interestWrittenOff == 31.00 - 10.33
            assert interestDue == 0.00
            assert feePaid == 210 * 0.005
            assert totalDue == 0.00
        }

        and:
        assert loan.statusDetail == LoanStatusDetail.PAID
    }

    def "renounce loan and repay"() {
        when:
        def loan = paymentTestCases.renounceLoanAndRepay()

        then:
        with(loan.balance) {
            assert principalPaid == 210.00
            assert interestPaid == 12.33
            assert interestWrittenOff == 24.67
            assert interestDue == 0.00
            assert totalDue == 0.00
        }

        and:
        assert loan.statusDetail == LoanStatusDetail.RENOUNCED_PAID
    }

    def "renounce loan with 20 discount and repay"() {
        when:
        def loan = paymentTestCases.renounceLoan20DiscountAndRepay()

        then:
        with(loan.balance) {
            assert interestApplied == 29.00
            assert principalPaid == 210.00
            assert interestPaid == 9.67
            assert interestWrittenOff == 19.33
            assert interestDue == 0.00
            assert totalDue == 0.00
        }

        and:
        assert loan.statusDetail == LoanStatusDetail.RENOUNCED_PAID
    }

    def "renounce loan and overpay"() {
        when:
        def loan = paymentTestCases.renounceLoanAndOverpay()

        then:
        with(loan.balance) {
            assert principalPaid == 210.00
            assert interestPaid == 12.33
            assert interestWrittenOff == 24.67
            assert interestDue == 0.00
            assert totalDue == 0.00
            assert overpaymentReceived == 1.00
            assert overpaymentAvailable == 1.00
        }

        and:
        assert loan.statusDetail == LoanStatusDetail.RENOUNCED_PAID
    }

    def "renounce loan and underpay"() {
        when:
        def loan = paymentTestCases.renounceLoanAndUnderpay()

        then:
        with(loan.balance) {
            assert principalPaid == 209.00
            assert interestPaid == 12.33
            assert interestWrittenOff == 24.67
            assert interestDue == 0.00
            assert totalDue == 1.00
            assert principalDue == 1.00
        }

        and:
        assert loan.statusDetail == LoanStatusDetail.RENOUNCED
    }

    def "recalculate dpd after payment is processed, not affected by last transaction date if voided"() {
        given:
        def loan = fintech.spain.alfa.product.testing.TestFactory.newClient()
            .registerDirectly()
            .issueActiveLoan(amount(300), 30, date("2019-01-01"))

        when:
        loan.applyPenalty(date("2019-02-04"))

        then:
        loan.loan.overdueDays == 4

        when:
        TimeMachine.useFixedClockAt(date("2019-02-04"))
        loan.voidTransactions(TransactionQuery.byLoan(loan.loanId, TransactionType.APPLY_PENALTY))
        loan.repay(amount(500), date("2019-01-31"))
        resolver.resolveDerivedValues(loan.loanId)

        then:
        loan.loan.overdueDays == 0
    }

    def "recalculate dpd after payment is processed, not affected by last transaction date"() {
        given:
        def loan = fintech.spain.alfa.product.testing.TestFactory.newClient()
            .registerDirectly()
            .issueActiveLoan(amount(300), 30, date("2019-01-01"))

        when:
        loan.applyPenalty(date("2019-02-04"))

        then:
        loan.loan.overdueDays == 4

        when:
        TimeMachine.useFixedClockAt(date("2019-02-04"))
        loan.repay(amount(500), date("2019-01-31"))
        resolver.resolveDerivedValues(loan.loanId)

        then:
        loan.loan.overdueDays == 0
    }
}
