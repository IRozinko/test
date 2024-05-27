package fintech.calendar

import com.google.common.collect.ImmutableList
import fintech.TimeMachine
import fintech.calendar.impl.query.WorkingHoursQuery
import fintech.calendar.spi.BusinessCalendarService
import fintech.testing.integration.AbstractBaseSpecification
import org.springframework.beans.factory.annotation.Autowired
import spock.lang.Unroll

import java.time.LocalDate
import java.time.temporal.ChronoUnit

import static fintech.DateUtils.date
import static fintech.DateUtils.dateTime
import static java.time.DayOfWeek.FRIDAY
import static java.time.DayOfWeek.MONDAY
import static java.time.DayOfWeek.SATURDAY
import static java.time.DayOfWeek.SUNDAY
import static java.time.DayOfWeek.THURSDAY
import static java.time.DayOfWeek.TUESDAY
import static java.time.DayOfWeek.WEDNESDAY

class BusinessCalendarServiceTest extends AbstractBaseSpecification {

    @Autowired
    private BusinessCalendarService calendar

    def "MONDAY plus 5 days -> NEXT MONDAY"() {
        when:
        TimeMachine.useFixedClockAt(dateTime("2018-08-06 11:00:00"))

        then:
        def time = calendar.resolveBusinessTime(5, ChronoUnit.DAYS)
        with(time) {
            dayOfWeek == MONDAY
            dayOfMonth == 13
            hour == 11
            minute == 0
            second == 0
        }
    }

    def "THURSDAY -> THURSDAY"() {
        when:
        TimeMachine.useFixedClockAt(dateTime("2018-08-02 00:00:00"))

        then:
        def time = calendar.resolveBusinessTime(5, ChronoUnit.DAYS)
        with(time) {
            dayOfWeek == THURSDAY
            dayOfMonth == 9
            hour == 0
            minute == 0
            second == 0
        }
    }

    def "SATURDAY -> MONDAY"() {
        when:
        TimeMachine.useFixedClockAt(dateTime("2018-10-06 00:00:00"))

        then:
        def time = calendar.resolveBusinessTime(1, ChronoUnit.DAYS)
        with(time) {
            dayOfWeek == MONDAY
            dayOfMonth == 8
            hour == 0
            minute == 0
            second == 0
        }
    }

    def "WEDNESDAY -> TWO WEEKS -> WEDNESDAY: Same Time"() {
        when:
        TimeMachine.useFixedClockAt(dateTime("2018-08-01 14:30:45"))

        then:
        def time = calendar.resolveBusinessTime(10, ChronoUnit.DAYS)
        with(time) {
            dayOfWeek == WEDNESDAY
            dayOfMonth == 15
            hour == 14
            minute == 30
            second == 45
        }
    }

    def "FRIDAY at 16 plus 1 hours -> MONDAY at 7"() {
        when:
        TimeMachine.useFixedClockAt(dateTime("2018-10-05 16:50:21"))

        then:
        def time = calendar.resolveWorkingHours(1, ChronoUnit.HOURS)
        with(time) {
            dayOfWeek == MONDAY
            dayOfMonth == 8
            hour == 7
            minute == 0
            second == 0
        }
    }

    def "MONDAY plus 5 days -> NEXT MONDAY working hours"() {
        when:
        TimeMachine.useFixedClockAt(dateTime("2018-08-06 11:00:00"))

        then:
        def time = calendar.resolveWorkingHours(5, ChronoUnit.DAYS)
        with(time) {
            dayOfWeek == MONDAY
            dayOfMonth == 13
            hour == 11
            minute == 0
            second == 0
        }
    }

    def "MONDAY plus 5 days -> NEXT MONDAY non working hours"() {
        when:
        TimeMachine.useFixedClockAt(dateTime("2018-08-06 19:00:00"))

        then:
        def time = calendar.resolveWorkingHours(5, ChronoUnit.DAYS)
        with(time) {
            dayOfWeek == TUESDAY
            dayOfMonth == 14
            hour == 7
            minute == 0
            second == 0
        }
    }

    def "SATURDAY -> MONDAY working hours"() {
        when:
        TimeMachine.useFixedClockAt(dateTime("2018-10-06 00:00:00"))

        then:
        def time = calendar.resolveWorkingHours(1, ChronoUnit.DAYS)
        with(time) {
            dayOfWeek == MONDAY
            dayOfMonth == 8
            hour == 7
            minute == 0
            second == 0
        }
    }

    @Unroll
    def "Is It Business Day? #dayOfWeek"() {
        expect:
        assert calendar.isHoliday(dateTime(day).toLocalDate()) == isHoliday

        where:
        day                   | dayOfWeek | isHoliday
        "2018-08-06 12:00:00" | MONDAY    | false
        "2018-08-07 12:00:00" | TUESDAY   | false
        "2018-08-08 12:00:00" | WEDNESDAY | false
        "2018-08-09 12:00:00" | THURSDAY  | false
        "2018-08-10 12:00:00" | FRIDAY    | false
        "2018-08-11 12:00:00" | SATURDAY  | true
        "2018-08-12 12:00:00" | SUNDAY    | true
    }

    @Unroll
    def "Is It Working hour?"() {
        expect:
        dateTime(workinghour) == dateTime(date).query(new WorkingHoursQuery())

        where:
        date                  | workinghour
        "2018-08-10 05:00:00" | "2018-08-10 07:00:00"
        "2018-08-10 12:00:00" | "2018-08-10 12:00:00"
        "2018-08-10 19:00:00" | "2018-08-11 07:00:00"
        "2018-08-11 12:00:00" | "2018-08-11 12:00:00"
        "2018-08-12 12:00:00" | "2018-08-12 12:00:00"
    }

    def "Saved And Then Update Calendar"() {
        when:
        List<LocalDate> holidays = ImmutableList.of(date("2018-08-08"))
        calendar.putHolidays(holidays)

        then:
        calendar.resolveBusinessTime(dateTime("2018-08-06 12:00:00"), 4, ChronoUnit.DAYS).toLocalDate() == date("2018-08-13")

        when:
        List<LocalDate> businessDays = ImmutableList.of(date("2018-08-08"))
        calendar.removeHolidays(businessDays)

        holidays = ImmutableList.of(date("2018-08-09"), date("2018-08-10"))
        calendar.putHolidays(holidays)

        then:
        calendar.resolveBusinessTime(dateTime("2018-08-06 12:00:00"), 4, ChronoUnit.DAYS).toLocalDate() == date("2018-08-14")

    }

}
