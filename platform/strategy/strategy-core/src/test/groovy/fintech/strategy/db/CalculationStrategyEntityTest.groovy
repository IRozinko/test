package fintech.strategy.db


import spock.lang.Specification

class CalculationStrategyEntityTest extends Specification {
    def "to value object"() {
        given:
        def entity = new CalculationStrategyEntity(
            id: 1L,
            strategyType: "F",
            calculationType: "B",
            version: "003",
            enabled: true,
            isDefault: false
        )

        expect:
        with(entity.toValueObject()) {
            id == 1L
            strategyType == "F"
            calculationType == "B"
            version == "003"
            enabled
            !isDefault
        }
    }
}
