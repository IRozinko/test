package fintech

import spock.lang.Specification

import java.time.LocalDate
import java.time.format.DateTimeParseException

import static fintech.DateUtils.date
import static fintech.DateUtils.dateTime

class DateUtilsTest extends Specification {

    def "ToYyyyMmDd"() {
        expect:
        DateUtils.toYyyyMmDd(date("2016-01-31")) == "2016-01-31"
        DateUtils.toYyyyMmDd(null) == null
    }

    def "Lt"() {
        expect:
        DateUtils.lt(date("2016-01-10"), date("2016-01-11"))
        !DateUtils.lt(date("2016-01-11"), date("2016-01-11"))
    }

    def "Loe"() {
        expect:
        DateUtils.loe(date("2016-01-10"), date("2016-01-11"))
        DateUtils.loe(date("2016-01-11"), date("2016-01-11"))
        !DateUtils.loe(date("2016-01-12"), date("2016-01-11"))
    }

    def "Gt"() {
        expect:
        DateUtils.gt(date("2016-01-10"), date("2016-01-09"))
        !DateUtils.gt(date("2016-01-11"), date("2016-01-11"))
    }

    def "Goe"() {
        expect:
        DateUtils.goe(date("2016-01-11"), date("2016-01-10"))
        DateUtils.goe(date("2016-01-11"), date("2016-01-11"))
        !DateUtils.goe(date("2016-01-11"), date("2016-01-12"))
    }


    def "Min"() {
        expect:
        DateUtils.min(date("2016-01-11"), date("2016-01-10")) == date("2016-01-10")
    }

    def "Max"() {
        expect:
        DateUtils.max(date("2016-01-11"), date("2016-01-12")) == date("2016-01-12")
    }

    def "Between"() {
        expect:
        DateUtils.betweenInclusive(date("2016-01-11"), date("2016-01-11"), date("2016-01-11"))
        DateUtils.betweenInclusive(date("2016-01-11"), date("2016-01-10"), date("2016-01-11"))
        DateUtils.betweenInclusive(date("2016-01-11"), date("2016-01-11"), date("2016-01-12"))
        !DateUtils.betweenInclusive(date("2016-01-11"), date("2016-01-12"), date("2016-01-13"))
    }

    def "Days until end of month"() {
        expect:
        DateUtils.daysUntilEndOfMonth(date("2016-01-01")) == 30
        DateUtils.daysUntilEndOfMonth(date("2016-12-21")) == 10
        DateUtils.daysUntilEndOfMonth(date("2016-12-31")) == 0
        DateUtils.daysUntilEndOfMonth(date("2016-02-01")) == 28
        DateUtils.daysUntilEndOfMonth(date("2016-02-28")) == 1
        DateUtils.daysUntilEndOfMonth(date("2016-02-29")) == 0
    }

    def "Days after start of month"() {
        expect:
        DateUtils.daysAfterStartOfMonth(date("2016-01-01")) == 0
        DateUtils.daysAfterStartOfMonth(date("2016-12-21")) == 20
        DateUtils.daysAfterStartOfMonth(date("2016-12-31")) == 30
        DateUtils.daysAfterStartOfMonth(date("2016-02-01")) == 0
        DateUtils.daysAfterStartOfMonth(date("2016-02-28")) == 27
        DateUtils.daysAfterStartOfMonth(date("2016-02-29")) == 28
    }


    def "ToLocalDate"() {
        expect:
        DateUtils.toLocalDate(new Date()) == LocalDate.now()
    }


    def "Datetime string is valid LocalDateTime"() {
        expect:
        assert !DateUtils.isValidLocalDateTime("123")
        assert !DateUtils.isValidLocalDateTime("")
        assert !DateUtils.isValidLocalDateTime(null)
        assert DateUtils.isValidLocalDateTime("2017-01-05 13:03:44")
    }

    def "Datetime string is valid LocalDateTime with custom format"() {
        expect:
        assert !DateUtils.isValidLocalDateTime("123", "DD/MM/YY")
        assert !DateUtils.isValidLocalDateTime("", "DD/MM/YY")
        assert !DateUtils.isValidLocalDateTime(null, "DD/MM/YY")
        assert !DateUtils.isValidLocalDateTime(null, null)
        assert DateUtils.isValidLocalDateTime("05/01/2017_130344", "dd/MM/yyyy_HHmmss")
    }

    def "LocalDate date(String dateStr, String format)"() {
        expect:
        assert date("27/01/17", "dd/MM/yy").toString() == "2017-01-27"
        assert date("09/11/2017 0:15", "d/M/yyyy H:m[:s]").toString() == "2017-11-09"
        assert date("09/11/2017 0:15:11", "d/M/yyyy H:m[:s]").toString() == "2017-11-09"
        assert date("09/11/2017 0:15:11", "d/M/yyyy H:m:s").toString() == "2017-11-09"
    }

    def "LocalDate date(String dateStr, String format) parse error"() {
        when:
        def date = date("27.01.17", "dd/MM/yy")

        then:
        thrown DateTimeParseException
        date == null
    }

    def "LocalDate date(String dateStr)"() {
        when:
        def date = date("2017-01-27")

        then:
        date.toString() == "2017-01-27"
    }

    def "LocalDateTime max"() {
        expect:
        DateUtils.max(dateTime("2017-01-01 11:59:59"), dateTime("2017-01-01 12:00:00")) == dateTime("2017-01-01 12:00:00")
    }
}
