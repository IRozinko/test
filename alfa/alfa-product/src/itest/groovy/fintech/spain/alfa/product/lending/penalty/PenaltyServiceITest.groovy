package fintech.spain.alfa.product.lending.penalty

import fintech.payments.PaymentService
import fintech.spain.alfa.product.AbstractAlfaTest
import fintech.spain.alfa.product.strategy.penalty.AlfaDpdPenaltyStrategy
import fintech.spain.alfa.strategy.StrategyType
import fintech.spain.alfa.strategy.penalty.DpdPenaltyStrategyProperties
import fintech.strategy.CalculationStrategyService
import fintech.strategy.SaveCalculationStrategyCommand
import fintech.transactions.TransactionQuery
import fintech.transactions.TransactionService
import org.springframework.beans.factory.annotation.Autowired

import java.time.LocalDate

import static fintech.BigDecimalUtils.amount
import static fintech.DateUtils.date
import static fintech.TimeMachine.today
import static fintech.spain.alfa.strategy.penalty.DpdPenaltyStrategyProperties.PenaltyStrategy

class PenaltyServiceITest extends AbstractAlfaTest {

    @Autowired
    CalculationStrategyService calculationStrategyService

    @Autowired
    PenaltyService penaltyService

    @Autowired
    PaymentService paymentService

    @Autowired
    TransactionService transactionService

    def "HandleLoanPaidEvent"() {
        given:
        def term = 15
        def issueDate = today().minusDays(term).minusDays(10)
        def dueDate = issueDate.plusDays(term)
        def client = fintech.spain.alfa.product.testing.TestFactory.newClient().registerDirectly()
        def loan = client.issueActiveLoan(300.00, term, issueDate)

        and:
        def dayOfPayment = dueDate.plusDays(5)

        when:
        loan.applyPenalty(dueDate, dueDate.plusDays(10))

        then:
        loan.loan.penaltyOutstanding == 3.53 * 10

        when:
        loan.repay(100.00, dayOfPayment)

        then:
        loan.loan.penaltyOutstanding == 13.53

    }

    def "DPD penalty strategy test"() {
        given:
        def dpd = 6

        calculationStrategyService.saveCalculationStrategy(
            new SaveCalculationStrategyCommand()
                .setStrategyType(StrategyType.PENALTY.getType())
                .setCalculationType(AlfaDpdPenaltyStrategy.CALCULATION_TYPE.name())
                .setVersion("002")
                .setDefault(true)
                .setEnabled(true)
                .setProperties(
                    new DpdPenaltyStrategyProperties().setStrategies([new PenaltyStrategy(from: 3, rate: 10g), new PenaltyStrategy(from: 4, rate: 20g), new PenaltyStrategy(from: 5, rate: 30g)])
                ))

        when:
        def loan = fintech.spain.alfa.product.testing.TestFactory.newClient()
            .randomEmailAndName("Penalties on DPD " + dpd)
            .registerDirectly()
            .issueActiveLoan(amount(200), 15L, date("2018-01-01"))

        then:
        loan.getLoan().paymentDueDate == date('2018-01-16')

        when:
        (0..dpd).each {
            loan.applyPenalty(date("2018-01-01").plusDays(15L).plusDays(it))
        }

        then:
        assert penaltyAppliedOnDate(loan.loanId, date("2018-01-15")) == 0.00
        assert penaltyAppliedOnDate(loan.loanId, date("2018-01-16")) == 0.00
        assert penaltyAppliedOnDate(loan.loanId, date("2018-01-17")) == 0.00
        assert penaltyAppliedOnDate(loan.loanId, date("2018-01-18")) == 0.00
        assert penaltyAppliedOnDate(loan.loanId, date("2018-01-19")) == 235.00 * 0.1
        assert penaltyAppliedOnDate(loan.loanId, date("2018-01-20")) == 235.00 * 0.2
        assert penaltyAppliedOnDate(loan.loanId, date("2018-01-21")) == 235.00 * 0.3
        assert penaltyAppliedOnDate(loan.loanId, date("2018-01-22")) == 235.00 * 0.3

        with(loan.balance) {
            assert penaltyApplied == 235.00 * 0.9
            assert totalDue == 235.00 + 235.00 * 0.9
        }
    }

    BigDecimal penaltyAppliedOnDate(long loanId, LocalDate date) {
        return transactionService.getBalance(new TransactionQuery(loanId: loanId, valueDateIs: date)).penaltyApplied
    }
}
