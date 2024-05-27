package fintech.lending.core.creditlimit

import fintech.TimeMachine
import fintech.lending.BaseSpecification
import fintech.lending.core.creditlimit.AddCreditLimitCommand
import fintech.lending.core.creditlimit.CreditLimitService
import org.springframework.beans.factory.annotation.Autowired

import static fintech.DateUtils.date

class CreditLimitServiceTest extends BaseSpecification {

    @Autowired
    CreditLimitService service

    def 'Credit limit doesnt present'() {
        expect:
        !service.getClientLimit(1L, TimeMachine.today()).isPresent()
    }

    def "Add limit"() {
        when:
        service.addLimit(new AddCreditLimitCommand(clientId: 1, limit: 100.00g, reason: "test", activeFrom: date("2001-01-02")))

        then:
        !service.getClientLimit(1L, date("2001-01-01")).isPresent()
        service.getClientLimit(1L, date("2001-01-02")).get().limit == 100.00g
        service.getClientLimit(1L, date("2001-01-03")).get().limit == 100.00g
        !service.getClientLimit(2L, date("2001-01-03")).isPresent()

        when:
        service.addLimit(new AddCreditLimitCommand(clientId: 1, limit: 101.00g, reason: "test", activeFrom: date("2001-01-02")))

        then:
        service.getClientLimit(1L, date("2001-01-02")).get().limit == 101.00g

        when:
        service.addLimit(new AddCreditLimitCommand(clientId: 1, limit: 99.00g, reason: "test", activeFrom: date("2001-01-02")))

        then:
        service.getClientLimit(1L, date("2001-01-02")).get().limit == 99.00g

        when:
        service.addLimit(new AddCreditLimitCommand(clientId: 1, limit: 102.00g, reason: "test", activeFrom: date("2001-01-03")))

        then:
        service.getClientLimit(1L, date("2001-01-02")).get().limit == 99.00g
        service.getClientLimit(1L, date("2001-01-03")).get().limit == 102.00g
        service.getClientLimit(1L, date("2001-01-04")).get().limit == 102.00g
    }
}
