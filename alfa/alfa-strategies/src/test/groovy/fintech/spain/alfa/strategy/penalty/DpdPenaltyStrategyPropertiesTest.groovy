package fintech.spain.alfa.strategy.penalty


import spock.lang.Specification
import spock.lang.Unroll

class DpdPenaltyStrategyPropertiesTest extends Specification {

    @Unroll
    def "Get penalty rate for #daysOverdue days overdue with strategies: #strategies"() {
        given:
        def properties = new DpdPenaltyStrategyProperties()
        properties.setStrategies(strategies.collect {
            i -> new DpdPenaltyStrategyProperties.PenaltyStrategy().setFrom(i[0]).setRate(i[1])
        })

        expect:
        properties.getRateFor(daysOverdue) == penaltyRate

        where:
        strategies                | daysOverdue || penaltyRate
        [[0, 10.00], [10, 40.00]] | 0           || 10.00
        [[0, 10.00], [10, 40.00]] | 3           || 10.00
        [[0, 10.00], [10, 40.00]] | 5           || 10.00
        [[0, 10.00], [10, 40.00]] | 10          || 40.00
        [[0, 10.00], [10, 40.00]] | 15          || 40.00
        [[100, 50.00]]            | 10          || 0.00
        [[100, 50.00]]            | 101         || 50.00
    }

    def "get rate when no strategies are defined"() {
        given:
        def properties = new DpdPenaltyStrategyProperties().setStrategies([])

        when:
        properties.getRateFor(10)

        then:
        def ex = thrown(RuntimeException)
        ex.message == 'Penalty strategy is`nt configured properly'
    }

}
