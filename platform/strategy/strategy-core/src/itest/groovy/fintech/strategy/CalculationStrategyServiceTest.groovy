package fintech.strategy

import com.fasterxml.jackson.databind.JsonNode
import fintech.strategy.db.CalculationStrategyRepository
import fintech.strategy.spi.StrategyPropertiesRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

class CalculationStrategyServiceTest extends BaseSpecification {

    @Autowired
    CalculationStrategyService calculationStrategyService

    @Autowired
    CalculationStrategyRepository strategyRepository

    def "saving strategy creates new entity"() {
        given:
        def command = new SaveCalculationStrategyCommand(
            strategyType: "E",
            calculationType: "X",
            version: "001",
            properties: [111],
            enabled: true,
            isDefault: true
        )

        when:
        def id = calculationStrategyService.saveCalculationStrategy(command)

        then:
        with(strategyRepository.findOne(id)) {
            enabled
            isDefault
            strategyType == "E"
            calculationType == "X"
            version == "001"
        }

        when:
        calculationStrategyService.saveCalculationStrategy(command)

        then:
        IllegalArgumentException e = thrown()
        e.message == "Strategy with such full name exists EX001"
    }

    def "cannot have two default strategies - new strategy resets default flag for old one"() {
        when:
        def oldDefaultId = calculationStrategyService.saveCalculationStrategy(new SaveCalculationStrategyCommand(
            strategyType: "E",
            calculationType: "P",
            version: "001",
            properties: [],
            enabled: true,
            isDefault: true
        ))

        def newDefaultId = calculationStrategyService.saveCalculationStrategy(new SaveCalculationStrategyCommand(
            strategyType: "E",
            calculationType: "P",
            version: "002",
            properties: [],
            enabled: true,
            isDefault: true
        ))

        then:
        strategyRepository.findOne(oldDefaultId).isDefault == null
        strategyRepository.findOne(newDefaultId).isDefault
    }

    def "update calculation strategy - resets default flag and updates properties"() {
        given:
        def firstStrategyId = calculationStrategyService.saveCalculationStrategy(new SaveCalculationStrategyCommand(
            strategyType: "E",
            calculationType: "P",
            version: "001",
            properties: [],
            enabled: true,
            isDefault: true
        ))

        def secondStrategyId = calculationStrategyService.saveCalculationStrategy(new SaveCalculationStrategyCommand(
            strategyType: "E",
            calculationType: "P",
            version: "002",
            properties: [],
            enabled: true,
            isDefault: false
        ))

        when:
        calculationStrategyService.updateCalculationStrategy(new UpdateCalculationStrategyCommand(
            strategyId: secondStrategyId,
            isDefault: true,
            version: "003",
            properties: [123],
            enabled: false
        ))

        then:
        strategyRepository.findOne(firstStrategyId).isDefault == null

        and:
        with(strategyRepository.findOne(secondStrategyId)) {
            isDefault
            version == "003"
            !enabled
        }

        when: "property update doesnt reset default flag"
        calculationStrategyService.updateCalculationStrategy(new UpdateCalculationStrategyCommand(
            strategyId: secondStrategyId,
            isDefault: true,
            version: "003",
            properties: [1234],
            enabled: false
        ))

        then:
        with(strategyRepository.findOne(secondStrategyId)) {
            isDefault
            version == "003"
            !enabled
        }
    }

    @Component
    public static class MockStrategyRepository implements StrategyPropertiesRepository {

        @Override
        void saveStrategy(Long calculationStrategyId, JsonNode props) {

        }

        @Override
        JsonNode getStrategyPropertiesAsJson(Long calculationStrategyId) {
            return null
        }

        @Override
        Object getStrategyProperties(Long calculationStrategyId) {
            return null
        }

        @Override
        boolean supports(String strategyType, String calculationType) {
            return true
        }
    }
}
