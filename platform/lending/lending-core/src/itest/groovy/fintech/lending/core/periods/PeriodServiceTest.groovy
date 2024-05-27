package fintech.lending.core.periods

import fintech.lending.BaseSpecification
import fintech.lending.core.db.Entities
import fintech.lending.core.periods.commands.ClosePeriodCommand
import fintech.lending.core.periods.db.PeriodEntity
import fintech.lending.core.periods.db.PeriodRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.transaction.support.TransactionTemplate
import spock.lang.Subject

import javax.validation.ConstraintViolationException
import java.time.LocalDate

import static fintech.DateUtils.date

class PeriodServiceTest extends BaseSpecification {

    @Subject
    @Autowired
    PeriodService periodService

    @Autowired
    PeriodRepository periodRepository

    @Autowired
    TransactionTemplate txTemplate

    LocalDate firstPeriod = date("2017-06-01")

    LocalDate lastPeriod = date("2017-06-20")

    def setup() {
        txTemplate.execute {
            periodRepository.saveAndFlush(new PeriodEntity(periodDate: firstPeriod, status: PeriodStatus.OPEN))
        }
    }

    def "close period"() {
        given:
        def period = date("2017-06-01")

        when:
        println periodRepository.findAll()
        periodService.closePeriod(new ClosePeriodCommand(periodDate: period))

        then:
        assert periodService.isClosedOrClosing(period)
    }

    def "10 open periods are generated after closing"() {
        given:
        assert periodRepository.count(Entities.period.status.eq(PeriodStatus.OPEN)) == 1

        when:
        periodService.closePeriod(new ClosePeriodCommand(periodDate: date("2017-06-01")))

        then:
        assert periodRepository.count(Entities.period.status.eq(PeriodStatus.OPEN)) == 4
        assert periodRepository.count(Entities.period.status.eq(PeriodStatus.CLOSED)) == 1

        when:
        periodService.closePeriod(new ClosePeriodCommand(periodDate: date("2017-06-02")))

        then:
        assert periodRepository.count(Entities.period.status.eq(PeriodStatus.OPEN)) == 4
        assert periodRepository.count(Entities.period.status.eq(PeriodStatus.CLOSED)) == 2

        when:
        periodService.closePeriod(new ClosePeriodCommand(periodDate: date("2017-06-03")))

        then:
        assert periodRepository.count(Entities.period.status.eq(PeriodStatus.OPEN)) == 4
        assert periodRepository.count(Entities.period.status.eq(PeriodStatus.CLOSED)) == 3

        when:
        periodService.closePeriod(new ClosePeriodCommand(periodDate: date("2017-06-04")))
        periodService.closePeriod(new ClosePeriodCommand(periodDate: date("2017-06-05")))
        periodService.closePeriod(new ClosePeriodCommand(periodDate: date("2017-06-06")))
        periodService.closePeriod(new ClosePeriodCommand(periodDate: date("2017-06-07")))
        periodService.closePeriod(new ClosePeriodCommand(periodDate: date("2017-06-08")))
        periodService.closePeriod(new ClosePeriodCommand(periodDate: date("2017-06-09")))
        periodService.closePeriod(new ClosePeriodCommand(periodDate: date("2017-06-10")))
        periodService.closePeriod(new ClosePeriodCommand(periodDate: date("2017-06-11")))
        periodService.closePeriod(new ClosePeriodCommand(periodDate: date("2017-06-12")))

        then:
        assert periodRepository.count(Entities.period.status.eq(PeriodStatus.OPEN)) == 4
        assert periodRepository.count(Entities.period.status.eq(PeriodStatus.CLOSED)) == 12
    }

    def "input should be valid"() {
        when:
        periodService.closePeriod(new ClosePeriodCommand())

        then:
        def ex = thrown(ConstraintViolationException)
    }

    def "can close only after period date"() {
        given:
        def period = date("2017-06-01")

        when:
        periodService.closePeriod(new ClosePeriodCommand(periodDate: period, closeDate: period))

        then:
        def ex = thrown(IllegalArgumentException)
        assert !periodService.isClosedOrClosing(period)

        when:
        periodService.closePeriod(new ClosePeriodCommand(periodDate: period, closeDate: period.plusDays(1)))

        then:
        assert periodService.isClosedOrClosing(period)
    }

    def "can close any period"() {
        given:
        def period1 = date("2017-06-01")
        def period2 = period1.plusDays(1)
        def period3 = period2.plusDays(1)
        periodService.closePeriod(new ClosePeriodCommand(periodDate: period1))

        when:
        periodService.closePeriod(new ClosePeriodCommand(periodDate: period3))

        then:
        assert periodService.isClosedOrClosing(period3)

        when:
        periodService.closePeriod(new ClosePeriodCommand(periodDate: period2))

        then:
        assert periodService.isClosedOrClosing(period1)
        assert periodService.isClosedOrClosing(period2)
        assert periodService.isClosedOrClosing(period3)
    }

    def "is closed period"() {
        expect:
        assert !periodService.isClosedOrClosing(firstPeriod)
    }

}
