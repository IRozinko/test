package fintech.spain.alfa.product

import fintech.accounting.AccountingReports
import fintech.accounting.ReportQuery
import fintech.lending.core.loan.LoanStatusDetail
import org.springframework.beans.factory.annotation.Autowired
import spock.lang.Unroll

import static fintech.BigDecimalUtils.amount
import static fintech.DateUtils.date

class LoanServicingFacadeTest extends AbstractAlfaTest {

    @Autowired
    AccountingReports accountingReports

    @Unroll
    def "prepayment amount: #days"() {
        given:
        def loan = fintech.spain.alfa.product.testing.TestFactory.newClient()
            .registerDirectly()
            .issueActiveLoan(amount(200), 30L, date("2018-01-01"))

        expect:
        with(loan.balance) {
            assert interestDue == 70.00
            assert principalDue == 200.00
            assert totalDue == 270.00
        }

        and:
        with(loan.calculatePrepayment(date("2018-01-01").plusDays(days))) {
            assert prepaymentAvailable == available
            assert interestToPay == interest
            if (available) {
                assert prepaymentFeeToPay == 200.00 * 0.005
                assert principalToPay == 200.00
                assert totalToPay == interestToPay + prepaymentFeeToPay + principalToPay
            }
        }

        where:
        days | available | interest | interestToWriteOff
        0    | true      | 0.00     | 70.00
        1    | true      | 2.33     | 70.00 - 2.33
        3    | true      | 7.00     | 70.00 - 7.00
        15   | true      | 35.00    | 70.00 - 35.00
        16   | true      | 37.33    | 70.00 - 37.33
        29   | true      | 67.67    | 70.00 - 67.67
        30   | false     | 0.00     | 0.00
        31   | false     | 0.00     | 0.00
    }

    @Unroll
    def "renounce loan: #renounceDate"() {
        given:
        def loan = fintech.spain.alfa.product.testing.TestFactory.newClient()
            .registerDirectly()
            .issueActiveLoan(amount(200), 30L, date("2018-01-01"))

        expect:
        with(loan.balance) {
            assert interestDue == 70.00
            assert principalDue == 200.00
            assert totalDue == 270.00
        }

        when:
        loan.renounce(renounceDate)

        then:
        with(loan.balance) {
            assert interestWrittenOff == expectedInterestWrittenOff
            assert interestDue == expectedInterestDue
            assert principalDue == 200.00
            assert totalDue == principalDue + expectedInterestDue
        }

        and:
        loan.statusDetail == LoanStatusDetail.RENOUNCED

        and:
        def turnover = accountingReports.getTurnover(
            new ReportQuery(loanId: loan.getLoanId(), bookingDateFrom: loan.getLoan().issueDate, bookingDateTo: renounceDate))

        turnover[fintech.spain.alfa.product.accounting.Accounts.LOANS_CHARGED].debit == expectedInterestWrittenOff + expectedInterestDue
        turnover[fintech.spain.alfa.product.accounting.Accounts.LOANS_CHARGED].credit == expectedInterestWrittenOff

        turnover[fintech.spain.alfa.product.accounting.Accounts.WRITE_OFF_COMMISSIONS_EARLY_PAYMENT].debit == expectedInterestWrittenOff
        turnover[fintech.spain.alfa.product.accounting.Accounts.WRITE_OFF_COMMISSIONS_EARLY_PAYMENT].credit == 0.0

        where:
        renounceDate       | expectedInterestWrittenOff | expectedInterestDue
        date("2018-01-01") | 70.00                      | 0.00
        date("2018-01-02") | 67.67                      | 2.33
        date("2018-01-14") | 39.67                      | 30.33
    }

    @Unroll
    def "can not renounce on date: #renounceDate"() {
        given:
        def loan = fintech.spain.alfa.product.testing.TestFactory.newClient()
            .registerDirectly()
            .issueActiveLoan(amount(200), 30L, date("2018-01-01"))

        when:
        loan.renounce(renounceDate)

        then:
        thrown(IllegalArgumentException.class)

        where:
        renounceDate << [date("2017-12-31"), date("2017-01-15")]
    }
}
