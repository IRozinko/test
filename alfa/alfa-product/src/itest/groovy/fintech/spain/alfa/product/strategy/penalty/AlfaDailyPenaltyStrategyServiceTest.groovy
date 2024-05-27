package fintech.spain.alfa.product.strategy.penalty

import fintech.JsonUtils
import fintech.spain.alfa.product.AbstractAlfaTest
import fintech.spain.alfa.product.strategy.extension.AlfaExtensionStrategy
import fintech.strategy.CalculationStrategyService
import fintech.strategy.SaveCalculationStrategyCommand
import fintech.spain.alfa.strategy.StrategyType
import fintech.spain.alfa.strategy.penalty.DailyPenaltyStrategyProperties
import org.springframework.beans.factory.annotation.Autowired

class AlfaDailyPenaltyStrategyServiceTest extends AbstractAlfaTest {
    @Autowired
    private AlfaDailyPenaltyStrategyService service
    @Autowired
    private CalculationStrategyService calculationStrategyService

    def "save/retrieve/update strategy"() {
        when:
        def id = calculationStrategyService.saveCalculationStrategy(
            new SaveCalculationStrategyCommand()
                .setStrategyType(StrategyType.PENALTY.getType())
                .setCalculationType(AlfaDailyPenaltyStrategy.CALCULATION_TYPE.name())
                .setVersion("00X")
                .setDefault(true)
                .setEnabled(true)
                .setProperties(
                    new DailyPenaltyStrategyProperties(penaltyRate: 3.50)
                ))

        and:
        def jsonNodeProperties = service.getStrategyPropertiesAsJson(id)

        then:
        def savedProperties = JsonUtils.readValue(jsonNodeProperties, DailyPenaltyStrategyProperties.class)

        savedProperties.penaltyRate == 3.50

        when:
        savedProperties.penaltyRate = 9.50

        and:
        service.saveStrategy(id, JsonUtils.toJsonNode(savedProperties))

        then:
        def updatedJsonNodeProperties = service.getStrategyPropertiesAsJson(id)
        def updatedProperties = JsonUtils.readValue(updatedJsonNodeProperties, DailyPenaltyStrategyProperties.class)

        updatedProperties.penaltyRate == 9.50
    }

    def "supports"() {
        expect:
        service.supports(StrategyType.PENALTY.getType(), AlfaDailyPenaltyStrategy.CALCULATION_TYPE.name())
        !service.supports(StrategyType.EXTENSION.getType(), AlfaDailyPenaltyStrategy.CALCULATION_TYPE.name())
        !service.supports(StrategyType.PENALTY.getType(), AlfaExtensionStrategy.CALCULATION_TYPE.name())
    }

    def "validate"() {
        when:
        calculationStrategyService.saveCalculationStrategy(
            new SaveCalculationStrategyCommand()
                .setStrategyType(StrategyType.PENALTY.getType())
                .setCalculationType(AlfaDailyPenaltyStrategy.CALCULATION_TYPE.name())
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
            new DailyPenaltyStrategyProperties(penaltyRate: -33.50),
            new DailyPenaltyStrategyProperties(penaltyRate: null)
        ]
    }
}
