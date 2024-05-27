package fintech.spain.alfa.product.acceptance

import fintech.lending.core.creditlimit.AddCreditLimitCommand
import fintech.lending.core.creditlimit.CreditLimitService
import fintech.spain.alfa.product.AbstractAlfaTest

import org.springframework.beans.factory.annotation.Autowired

import static fintech.DateUtils.date

class AcceptanceCalculatorLimitsTest extends AbstractAlfaTest {

    @Autowired
    CalculatorLimitTestCases calculatorLimitTestCases
    @Autowired
    private CreditLimitService creditLimitService

    def "client does not have loan history and no credit_limit -> default is used"() {
        when:
        def client = calculatorLimitTestCases.clientDoesNotHaveLoanHistory()

        then:
        assert client.offerSettings(date("2018-01-01")).maxAmount == 300.00
    }

    def "In case of credit_limit is present then that value is used"() {
        when:
        def client = calculatorLimitTestCases.maxPlus50()
        def creditLimit = new AddCreditLimitCommand()
        creditLimit.clientId = client.clientId
        creditLimit.limit = 320.00
        creditLimit.activeFrom = date("2018-01-01")
        creditLimit.reason = "Test"
        creditLimitService.addLimit(creditLimit)

        then:
        assert client.offerSettings(date("2018-01-08").plusDays(3).plusDays(7)).maxAmount == 320.00
    }
}
