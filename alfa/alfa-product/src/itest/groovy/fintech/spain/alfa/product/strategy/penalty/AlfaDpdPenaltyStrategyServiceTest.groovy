package fintech.spain.alfa.product.strategy.penalty

import fintech.JsonUtils
import fintech.spain.alfa.product.AbstractAlfaTest
import fintech.spain.alfa.product.strategy.extension.AlfaExtensionStrategy
import fintech.strategy.CalculationStrategyService
import fintech.strategy.SaveCalculationStrategyCommand
import fintech.spain.alfa.strategy.StrategyType
import fintech.spain.alfa.strategy.penalty.DpdPenaltyStrategyProperties
import org.springframework.beans.factory.annotation.Autowired

class AlfaDpdPenaltyStrategyServiceTest extends AbstractAlfaTest {

    @Autowired
    private AlfaDpdPenaltyStrategyService service
    @Autowired
    private CalculationStrategyService calculationStrategyService

    def "save/retrieve/update strategy"() {
        when:
        def id = calculationStrategyService.saveCalculationStrategy(
            new SaveCalculationStrategyCommand()
                .setStrategyType(StrategyType.PENALTY.getType())
                .setCalculationType(AlfaDpdPenaltyStrategy.CALCULATION_TYPE.name())
                .setVersion("00X")
                .setDefault(true)
                .setEnabled(true)
                .setProperties(
                    new DpdPenaltyStrategyProperties()
                        .setStrategies([
                            new DpdPenaltyStrategyProperties.PenaltyStrategy().setFrom(1).setRate(12.30)
                        ])
                ))

        and:
        def jsonNodeProperties = service.getStrategyPropertiesAsJson(id)

        then:
        def savedProperties = JsonUtils.readValue(jsonNodeProperties, DpdPenaltyStrategyProperties.class)

        with(savedProperties.strategies[0]) {
            from == 1
            rate == 12.30
        }

        when:
        savedProperties.strategies[0].rate = 9.50

        and:
        service.saveStrategy(id, JsonUtils.toJsonNode(savedProperties))

        then:
        def updatedJsonNodeProperties = service.getStrategyPropertiesAsJson(id)
        def updatedProperties = JsonUtils.readValue(updatedJsonNodeProperties, DpdPenaltyStrategyProperties.class)

        updatedProperties.strategies[0].rate == 9.50
    }

    def "supports"() {
        expect:
        service.supports(StrategyType.PENALTY.getType(), AlfaDpdPenaltyStrategy.CALCULATION_TYPE.name())
        !service.supports(StrategyType.EXTENSION.getType(), AlfaDailyPenaltyStrategy.CALCULATION_TYPE.name())
        !service.supports(StrategyType.PENALTY.getType(), AlfaExtensionStrategy.CALCULATION_TYPE.name())
    }

    def "validate"() {
        when:
        calculationStrategyService.saveCalculationStrategy(
            new SaveCalculationStrategyCommand()
                .setStrategyType(StrategyType.PENALTY.getType())
                .setCalculationType(AlfaDpdPenaltyStrategy.CALCULATION_TYPE.name())
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
            new DpdPenaltyStrategyProperties()
                .setStrategies([
                    new DpdPenaltyStrategyProperties.PenaltyStrategy().setFrom(-1).setRate(12.30)
                ]),
            new DpdPenaltyStrategyProperties()
                .setStrategies([
                    new DpdPenaltyStrategyProperties.PenaltyStrategy().setFrom(1)
                ])
        ]
    }
}
