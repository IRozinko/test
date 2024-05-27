package fintech.spain.alfa.product.acceptance


import fintech.spain.alfa.product.AbstractAlfaTest
import fintech.spain.alfa.product.strategy.extension.AlfaExtensionStrategy
import fintech.spain.alfa.product.strategy.interest.AlfaInterestStrategy
import fintech.spain.alfa.product.strategy.penalty.AlfaDailyPenaltyStrategy

import fintech.strategy.CalculationStrategyService
import fintech.strategy.SaveCalculationStrategyCommand
import fintech.spain.alfa.strategy.StrategyType
import fintech.spain.alfa.strategy.extension.ExtensionStrategyProperties
import fintech.spain.alfa.strategy.interest.MonthlyInterestStrategyProperties
import fintech.spain.alfa.strategy.penalty.DailyPenaltyStrategyProperties
import org.springframework.beans.factory.annotation.Autowired

import static fintech.BigDecimalUtils.amount
import static fintech.DateUtils.date

class AcceptanceStrategiesTest extends AbstractAlfaTest {

    @Autowired
    CalculationStrategyService calculationStrategyService

    @Autowired
    DcTestCases dcTestCases

    def "default extension strategy is used for new loans"() {
        given:
        calculationStrategyService.saveCalculationStrategy(
            new SaveCalculationStrategyCommand()
                .setStrategyType(StrategyType.EXTENSION.getType())
                .setCalculationType(AlfaExtensionStrategy.CALCULATION_TYPE.name())
                .setVersion("002")
                .setDefault(true)
                .setEnabled(true)
                .setProperties(
                new ExtensionStrategyProperties()
                    .setExtensions(Arrays.asList(new ExtensionStrategyProperties.ExtensionOption().setTerm(99L).setRate(amount(100.00))
                ))
            ))

        def loan = fintech.spain.alfa.product.testing.TestFactory.newClient()
            .randomEmailAndName("7 days prolong payment")
            .registerDirectly()
            .issueActiveLoan(amount(200), 15L, date("2018-01-01"))


        when:
        def extensions = loan.listExtensionOffers(date("2018-01-01"))

        then:
        extensions.size() == 1
        extensions.get(0).periodCount == 99
        extensions.get(0).price == amount(200)
    }

    def "default penalty strategy is used for new loans"() {
        given:
        def dpd = 5

        calculationStrategyService.saveCalculationStrategy(
            new SaveCalculationStrategyCommand()
                .setStrategyType(StrategyType.PENALTY.getType())
                .setCalculationType(AlfaDailyPenaltyStrategy.CALCULATION_TYPE.name())
                .setVersion("002")
                .setDefault(true)
                .setEnabled(true)
                .setProperties(
                new DailyPenaltyStrategyProperties()
                    .setPenaltyRate(amount(2.00))
            ))

        def loan = fintech.spain.alfa.product.testing.TestFactory.newClient()
            .randomEmailAndName("Penalties on DPD " + dpd)
            .registerDirectly()
            .issueActiveLoan(amount(200), 15L, date("2018-01-01"))

        when:
        loan.applyPenalty(date("2018-01-01").plusDays(15L).plusDays(dpd))


        then:
        with(loan.balance) {
            assert penaltyApplied == 23.5
            assert totalDue == 235.00 + 23.5
        }
    }

    def "default interest strategy is used for new loans"() {
        given:
        calculationStrategyService.saveCalculationStrategy(
            new SaveCalculationStrategyCommand()
                .setStrategyType(StrategyType.INTEREST.getType())
                .setCalculationType(AlfaInterestStrategy.CALCULATION_TYPE.name())
                .setVersion("002")
                .setDefault(true)
                .setEnabled(true)
                .setProperties(
                new MonthlyInterestStrategyProperties()
                    .setMonthlyInterestRate(amount(15.00))
            ))

        when:
        def loan = fintech.spain.alfa.product.testing.TestFactory.newClient()
            .randomEmailAndName("Interest 15%")
            .registerDirectly()
            .issueActiveLoan(amount(100), 30L, date("2018-01-01"))

        then:
        with(loan.balance) {
            assert interestApplied == 15.00
            assert totalDue == 100.00 + 15.00
        }

    }

    def "no extensions when loan was rescheduled"() {
        given:
        calculationStrategyService.saveCalculationStrategy(
            new SaveCalculationStrategyCommand()
                .setStrategyType(StrategyType.EXTENSION.getType())
                .setCalculationType(AlfaExtensionStrategy.CALCULATION_TYPE.name())
                .setVersion("002")
                .setDefault(true)
                .setEnabled(true)
                .setProperties(
                    new ExtensionStrategyProperties()
                        .setExtensions(Arrays.asList(new ExtensionStrategyProperties.ExtensionOption().setTerm(99L).setRate(amount(100.00))
                        ))
                ))

        def loan = dcTestCases.rescheduleOffered(date("2018-01-01"))

        when:
        def extensions = loan.listExtensionOffers(date("2018-01-01"))

        then:
        extensions.size() == 0
    }
}
