package fintech.spain.alfa.product.strategy.interest

import fintech.JsonUtils
import fintech.spain.alfa.product.AbstractAlfaTest
import fintech.spain.alfa.product.strategy.extension.AlfaExtensionStrategy
import fintech.strategy.CalculationStrategyService
import fintech.strategy.SaveCalculationStrategyCommand
import fintech.spain.alfa.strategy.StrategyType
import fintech.spain.alfa.strategy.interest.MonthlyInterestStrategyProperties
import org.springframework.beans.factory.annotation.Autowired

class AlfaInterestStrategyServiceTest extends AbstractAlfaTest {

    @Autowired
    private AlfaInterestStrategyService service
    @Autowired
    private CalculationStrategyService calculationStrategyService

    def "save/retrieve/update strategy"() {
        when:
        def scenario = 'test'
        def anotherScenario = 'score_test'
        def id = calculationStrategyService.saveCalculationStrategy(
            new SaveCalculationStrategyCommand()
                .setStrategyType(StrategyType.INTEREST.getType())
                .setCalculationType(AlfaInterestStrategy.CALCULATION_TYPE.name())
                .setVersion("00X")
                .setDefault(true)
                .setEnabled(true)
                .setProperties(
                    new MonthlyInterestStrategyProperties(monthlyInterestRate: 33.50, usingDecisionEngine: false, scenario: scenario)
                ))

        and:
        def jsonNodeProperties = service.getStrategyPropertiesAsJson(id)

        then:
        def savedProperties = JsonUtils.readValue(jsonNodeProperties, MonthlyInterestStrategyProperties.class)

        savedProperties.monthlyInterestRate == 33.50
        !savedProperties.usingDecisionEngine
        savedProperties.scenario == scenario

        when:
        savedProperties.monthlyInterestRate = 99.50
        savedProperties.usingDecisionEngine = true
        savedProperties.scenario = anotherScenario

        and:
        service.saveStrategy(id, JsonUtils.toJsonNode(savedProperties))

        then:
        def updatedJsonNodeProperties = service.getStrategyPropertiesAsJson(id)
        def updatedProperties = JsonUtils.readValue(updatedJsonNodeProperties, MonthlyInterestStrategyProperties.class)

        updatedProperties.monthlyInterestRate == 99.50
        updatedProperties.usingDecisionEngine
        updatedProperties.scenario == anotherScenario

    }

    def "supports"() {
        expect:
        service.supports(StrategyType.INTEREST.getType(), AlfaInterestStrategy.CALCULATION_TYPE.name())
        !service.supports(StrategyType.EXTENSION.getType(), AlfaInterestStrategy.CALCULATION_TYPE.name())
        !service.supports(StrategyType.INTEREST.getType(), AlfaExtensionStrategy.CALCULATION_TYPE.name())
    }

    def "validate"() {
        when:
        calculationStrategyService.saveCalculationStrategy(
            new SaveCalculationStrategyCommand()
                .setStrategyType(StrategyType.INTEREST.getType())
                .setCalculationType(AlfaInterestStrategy.CALCULATION_TYPE.name())
                .setVersion("00X")
                .setDefault(true)
                .setEnabled(true)
                .setProperties(
                    props
                ))

        then:
        IllegalArgumentException e = thrown()

        where:
        props << [
            new MonthlyInterestStrategyProperties(monthlyInterestRate: -33.50),
            new MonthlyInterestStrategyProperties(monthlyInterestRate: null)
        ]
    }
}
