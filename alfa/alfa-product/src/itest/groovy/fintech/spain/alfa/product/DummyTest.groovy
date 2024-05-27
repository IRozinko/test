package fintech.spain.alfa.product

import fintech.lending.core.loan.LoanStatus
import fintech.lending.core.loan.LoanStatusDetail

import static fintech.DateUtils.date
import static fintech.spain.alfa.product.testing.TestLoan.expectedExtensionPrice

class DummyTest extends AbstractAlfaTest {

    def "issue loan"() {
        when:
        def loan = fintech.spain.alfa.product.testing.TestFactory.newClient().registerDirectly().issueLoan(300.00, 30, date("2018-01-01"))

        then:
        with(loan.getLoan()) {
            assert status == LoanStatus.OPEN
            assert statusDetail == LoanStatusDetail.DISBURSING
            assert issueDate == date("2018-01-01")
            assert maturityDate == date("2018-01-31")
            assert totalDue == 0.00
            assert totalOutstanding == 0.00
        }

        when:
        loan.exportDisbursements(date("2018-01-02"))
        loan.updateDerivedValues(date("2018-01-02"))

        then:
        with(loan.getLoan()) {
            assert status == LoanStatus.OPEN
            assert statusDetail == LoanStatusDetail.ACTIVE
            assert issueDate == date("2018-01-01")
            assert maturityDate == date("2018-02-01")
            assert paymentDueDate == date("2018-02-01")
            assert cashOut == 0.00
            assert principalDisbursed == 300.00
            assert principalDue == 300.00
            assert interestDue == 105.00
            assert totalDue == 405.00
            assert totalOutstanding == 405.00
//            assert overdueDays == -30
//            assert maxOverdueDays == 0
        }

        when:
        loan.settleDisbursements(date("2018-01-03"))

        then:
        with(loan.getLoan()) {
            assert status == LoanStatus.OPEN
            assert statusDetail == LoanStatusDetail.ACTIVE
            assert issueDate == date("2018-01-01")
            assert maturityDate == date("2018-02-01")
            assert paymentDueDate == date("2018-02-01")
            assert cashOut == 300.00
            assert principalDisbursed == 300.00
            assert totalDue == 405.00
            assert totalOutstanding == 405.00
        }

        when:
        loan.repayAll(date("2018-02-02"))

        then:
        with(loan.getLoan()) {
            assert cashIn == 405.00
            assert totalDue == 0.00
            assert totalOutstanding == 0.00
            assert overpaymentAvailable == 0.00
            assert status == LoanStatus.CLOSED
            assert statusDetail == LoanStatusDetail.PAID
            assert maturityDate == date("2018-02-01")
            assert paymentDueDate == date("2018-02-01")
            assert closeDate == date("2018-02-02")
        }
    }

    def "extend loan"() {
        given:
        def loan = fintech.spain.alfa.product.testing.TestFactory.newClient().registerDirectly()
            .issueActiveLoan(300.00, 30, date("2018-01-01"))

        expect:
        loan.getLoan().getPaymentDueDate() == date("2018-01-31")

        when:
        loan.extend(expectedExtensionPrice(300.00, 7), date("2018-01-30"))

        then:
        loan.getLoan().getPaymentDueDate() == date("2018-02-07")

        when:
        loan.extend(expectedExtensionPrice(300.00, 30), date("2018-02-06"))

        then:
        loan.getLoan().getPaymentDueDate() == date("2018-03-09")
        loan.getLoan().getMaturityDate() == date("2018-03-09")
    }
}
