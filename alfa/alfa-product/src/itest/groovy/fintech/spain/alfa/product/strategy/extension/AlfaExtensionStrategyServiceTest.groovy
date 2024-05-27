package fintech.spain.alfa.product.strategy.extension

import fintech.JsonUtils
import fintech.spain.alfa.product.AbstractAlfaTest
import fintech.spain.alfa.product.strategy.interest.AlfaInterestStrategy
import fintech.strategy.CalculationStrategyService
import fintech.strategy.SaveCalculationStrategyCommand
import fintech.spain.alfa.strategy.StrategyType
import fintech.spain.alfa.strategy.extension.ExtensionStrategyProperties
import org.springframework.beans.factory.annotation.Autowired

class AlfaExtensionStrategyServiceTest extends AbstractAlfaTest {

    @Autowired
    private AlfaExtensionStrategyService service
    @Autowired
    private CalculationStrategyService calculationStrategyService

    def "save and retrieve strategy"() {
        when:
        Long id = calculationStrategyService.saveCalculationStrategy(
            new SaveCalculationStrategyCommand()
                .setStrategyType(StrategyType.EXTENSION.getType())
                .setCalculationType(AlfaExtensionStrategy.CALCULATION_TYPE.name())
                .setVersion("00X")
                .setDefault(true)
                .setEnabled(true)
                .setProperties(
                    new ExtensionStrategyProperties(
                        extensions: [
                            new ExtensionStrategyProperties.ExtensionOption(term: 1L, rate: 3.00),
                            new ExtensionStrategyProperties.ExtensionOption(term: 20L, rate: 13.00),
                            new ExtensionStrategyProperties.ExtensionOption(term: 30L, rate: 23.00)
                        ]
                    )
                )
        )

        and:
        def jsonNodeProperties = service.getStrategyPropertiesAsJson(id)

        then:
        def savedProperties = JsonUtils.readValue(jsonNodeProperties, ExtensionStrategyProperties.class)

        savedProperties.extensions.size() == 3
        savedProperties.extensions.find { it.term == 1L }.rate == 3.00
        savedProperties.extensions.find { it.term == 20L }.rate == 13.00
        savedProperties.extensions.find { it.term == 30L }.rate == 23.00

        when:
        service.saveStrategy(id, JsonUtils.toJsonNode(new ExtensionStrategyProperties()))

        and:
        def updatedJsonNodeProperties = service.getStrategyPropertiesAsJson(id)

        then:
        def updatedProperties = JsonUtils.readValue(updatedJsonNodeProperties, ExtensionStrategyProperties.class)
        updatedProperties.extensions.size() == 0
    }

    def "supports"() {
        expect:
        service.supports(StrategyType.EXTENSION.getType(), AlfaExtensionStrategy.CALCULATION_TYPE.name())
        !service.supports(StrategyType.EXTENSION.getType(), AlfaInterestStrategy.CALCULATION_TYPE.name())
        !service.supports(StrategyType.PENALTY.getType(), AlfaExtensionStrategy.CALCULATION_TYPE.name())
    }

    def "validate"() {
        when:
        calculationStrategyService.saveCalculationStrategy(
            new SaveCalculationStrategyCommand()
                .setStrategyType(StrategyType.EXTENSION.getType())
                .setCalculationType(AlfaExtensionStrategy.CALCULATION_TYPE.name())
                .setVersion("00X")
                .setDefault(true)
                .setEnabled(true)
                .setProperties(
                    new ExtensionStrategyProperties(
                        extensions: [option]
                    )
                )
        )

        then:
        IllegalArgumentException e = thrown()

        where:
        option << [
            new ExtensionStrategyProperties.ExtensionOption(term: 0L, rate: 3.00),
            new ExtensionStrategyProperties.ExtensionOption(term: -10L, rate: 3.00),
            new ExtensionStrategyProperties.ExtensionOption(term: 10L, rate: -3.00),
            new ExtensionStrategyProperties.ExtensionOption(term: 10L, rate: null)
        ]
    }

    def "validate same options"() {
        when:
        calculationStrategyService.saveCalculationStrategy(
            new SaveCalculationStrategyCommand()
                .setStrategyType(StrategyType.EXTENSION.getType())
                .setCalculationType(AlfaExtensionStrategy.CALCULATION_TYPE.name())
                .setVersion("00X")
                .setDefault(true)
                .setEnabled(true)
                .setProperties(
                    new ExtensionStrategyProperties(
                        extensions: [option, option]
                    )
                )
        )

        then:
        IllegalArgumentException e = thrown()

        where:
        option << [
            new ExtensionStrategyProperties.ExtensionOption(term: 10L, rate: 3.00)
        ]
    }
}
