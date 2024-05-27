package fintech.lending.core.discount

import fintech.TimeMachine
import fintech.lending.BaseSpecification
import fintech.lending.core.CreditLineLoanHelper
import fintech.lending.core.LoanHolder
import org.springframework.beans.factory.annotation.Autowired

class DiscountServiceTest extends BaseSpecification {

    @Autowired
    private CreditLineLoanHelper loanHelper

    @Autowired
    private DiscountService discountService

    def setup() {
        loanHelper.init()
    }

    def "Find discount"() {
        when:
        def discount1 = discountService.applyDiscount(new ApplyDiscountCommand(clientId: 1, rateInPercent: 8.00, effectiveFrom: TimeMachine.today().minusDays(3), effectiveTo: TimeMachine.today().minusDays(1)))
        def discount2 = discountService.applyDiscount(new ApplyDiscountCommand(clientId: 1, rateInPercent: 8.00, effectiveFrom: TimeMachine.today(), effectiveTo: TimeMachine.today()))
        def discount3 = discountService.applyDiscount(new ApplyDiscountCommand(clientId: 1, rateInPercent: 8.00, effectiveFrom: TimeMachine.today().plusDays(1), effectiveTo: TimeMachine.today().plusDays(3)))
        def discount4 = discountService.applyDiscount(new ApplyDiscountCommand(clientId: 1, rateInPercent: 7.00, effectiveFrom: TimeMachine.today(), effectiveTo: TimeMachine.today()))
        def discount5 = discountService.applyDiscount(new ApplyDiscountCommand(clientId: 1, rateInPercent: 9.00, effectiveFrom: TimeMachine.today(), effectiveTo: TimeMachine.today()))

        and:
        loanHelper.applyAndDisburse(new LoanHolder(clientId: 1, discountId: discount2.id))
        loanHelper.applyAndDisburse(new LoanHolder(clientId: 1, discountId: discount5.id))

        then:
        discountService.findDiscount(1, TimeMachine.today()).get().id == discount4.id
        discountService.findDiscount(1, TimeMachine.today().minusDays(1)).get().id == discount1.id
        discountService.findDiscount(1, TimeMachine.today().plusDays(1)).get().id == discount3.id
    }
}
