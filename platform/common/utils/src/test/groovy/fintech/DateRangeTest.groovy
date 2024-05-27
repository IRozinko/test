package fintech

import spock.lang.Specification

import java.time.LocalDate
import java.time.temporal.ChronoUnit

class DateRangeTest extends Specification {


    def "Stream"() {
        given:
        def range = new DateRange(LocalDate.of(2019, 6, 1), LocalDate.of(2019, 6, 5))

        expect:
        range.stream().count() == 5
        range.count() == 5
    }
}
