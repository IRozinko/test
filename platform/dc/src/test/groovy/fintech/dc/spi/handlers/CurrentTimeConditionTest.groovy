package fintech.dc.spi.handlers

import fintech.TimeMachine
import fintech.dc.impl.ConditionContextImpl
import fintech.dc.model.DcSettings
import spock.lang.Specification

import static fintech.DateUtils.dateTime

class CurrentTimeConditionTest extends Specification {

    def "current time condition"() {
        given:
        def context = new ConditionContextImpl(null, new DcSettings.Condition(params: [hourFrom: 10, hourTo: 11]), null, null)
        def condition = new CurrentTimeCondition()

        when:
        TimeMachine.useFixedClockAt(dateTime("2017-01-01 09:59:00"))

        then:
        !condition.apply(context)

        when:
        TimeMachine.useFixedClockAt(dateTime("2017-01-01 10:00:00"))

        then:
        condition.apply(context)

        when:
        TimeMachine.useFixedClockAt(dateTime("2017-01-01 11:59:00"))

        then:
        condition.apply(context)

        when:
        TimeMachine.useFixedClockAt(dateTime("2017-01-01 12:00:00"))

        then:
        !condition.apply(context)
    }
}
