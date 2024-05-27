package fintech.lending.core.creditlimit

import fintech.TimeMachine
import fintech.lending.BaseSpecification
import fintech.lending.core.CreditLineLoanHelper
import fintech.lending.core.LoanHolder
import fintech.lending.core.loan.LoanService
import fintech.lending.core.loan.commands.UpdateCreditLimitCommand
import org.springframework.beans.factory.annotation.Autowired

class CreditLimitTest extends BaseSpecification {

    @Autowired
    CreditLineLoanHelper loanHelper

    @Autowired
    LoanService loanService

    def "Update credit limit"() {
        when:
        loanHelper.init()
        def loanHolder = loanHelper.applyAndDisburse(new LoanHolder())

        then:
        with(loanService.getLoan(loanHolder.loanId)) {
            creditLimit == 1000.00g
            creditLimitAvailable == 0.00g
            creditLimitAwarded == 2000.00g
        }

        when:
        loanService.updateCreditLimit(new UpdateCreditLimitCommand(loanId: loanHolder.loanId, amount: 1000.00g, valueDate: TimeMachine.today()))

        then:
        with(loanService.getLoan(loanHolder.loanId)) {
            creditLimit == 2000.00g
            creditLimitAvailable == 1000.00g
            creditLimitAwarded == 2000.00g
        }

        when:
        loanService.updateCreditLimit(new UpdateCreditLimitCommand(loanId: loanHolder.loanId, amount: -500.00g, valueDate: TimeMachine.today()))

        then:
        with(loanService.getLoan(loanHolder.loanId)) {
            creditLimit == 1500.00g
            creditLimitAvailable == 500.00g
            creditLimitAwarded == 2000.00g
        }
    }
}
