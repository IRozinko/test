package fintech.spain.alfa.product.acceptance

import fintech.TimeMachine
import fintech.lending.core.loan.LoanStatus
import fintech.lending.core.loan.LoanStatusDetail
import fintech.spain.alfa.product.AbstractAlfaTest
import spock.util.concurrent.PollingConditions

import java.time.LocalDate

import static fintech.BigDecimalUtils.amount

class AcceptanceBatchActionsTest extends AbstractAlfaTest {

    static final int DPD = 10

    fintech.spain.alfa.product.testing.TestLoan loan
    LocalDate issueDate

    def setup() {
        issueDate = TimeMachine.today().minusDays(30).minusDays(DPD)
        loan = fintech.spain.alfa.product.testing.TestFactory.newClient()
            .registerDirectly()
            .issueActiveLoan(amount(200), 30, issueDate)
            .applyPenalty(issueDate.plusDays(30).plusDays(DPD))
            .postToDc()
    }

    def 'externalizes debt'() {
        when:
        loan.applyPenalty(issueDate.plusDays(30).plusDays(DPD))

        then:
        loan.loan.penaltyApplied == 27g

        when:
        loan.externalizeDebt()
            .applyPenalty(issueDate.plusDays(30).plusDays(DPD))

        then: "portfolio changed accordingly"
        loan.debt.agent == null
        loan.debt.portfolio == 'External Collections'

        and: 'no new penalties are applied'
        loan.loan.penaltySuspended
        loan.loan.penaltyApplied == 27g

        and: "loan status detail EXTERNALIZED"
        loan.loan.statusDetail == LoanStatusDetail.EXTERNALIZED
    }

    def 'recover externalized'() {
        given:
        def conditions = new PollingConditions(timeout: 20)
        loan.externalizeDebt()

        when:
        loan.recoverDebt()

        then: 'penalties are enabled'
        !loan.loan.penaltySuspended
        loan.debt.portfolio == 'Collections'

        and: "loan status detail ACTIVE"
        conditions.eventually {
            loan.loan.statusDetail == LoanStatusDetail.ACTIVE
        }
    }


    def 'sells debt'() {
        when:
        loan.sellDebt()

        then: 'penalties are enabled'
        loan.loan.penaltySuspended
        loan.debt.portfolio == 'Sold'
        loan.status == LoanStatus.CLOSED
        loan.statusDetail == LoanStatusDetail.SOLD
    }

    def 'repurchases debt'() {
        given:
        loan.sellDebt()

        when:
        loan.repurchaseDebt("Collections")

        then:
        !loan.loan.penaltySuspended
        loan.debt.portfolio == 'Collections'
        loan.status == LoanStatus.OPEN
        loan.statusDetail == LoanStatusDetail.REPURCHASED
    }

    def 'Paying repurchased loan'() {
        given:
        loan.sellDebt()

        when:
        loan.repurchaseDebt("Collections")

        then:
        loan.status == LoanStatus.OPEN
        loan.statusDetail == LoanStatusDetail.REPURCHASED

        when:
        loan.repayAll(TimeMachine.today())

        then:
        loan.status == LoanStatus.CLOSED
        loan.statusDetail == LoanStatusDetail.PAID
    }

    def "Selling repurchased loan"() {
        given:
        loan.sellDebt()
        loan.repurchaseDebt("Collections")

        when:
        loan.sellDebt()

        then:
        loan.loan.penaltySuspended
        loan.debt.portfolio == 'Sold'
        loan.status == LoanStatus.CLOSED
        loan.statusDetail == LoanStatusDetail.SOLD
    }
}
