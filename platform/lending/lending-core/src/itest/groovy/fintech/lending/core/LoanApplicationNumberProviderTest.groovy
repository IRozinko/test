package fintech.lending.core

import fintech.lending.BaseSpecification
import fintech.lending.core.application.impl.LoanApplicationNumberProvider
import org.springframework.beans.factory.annotation.Autowired

class LoanApplicationNumberProviderTest extends BaseSpecification {

    @Autowired
    LoanApplicationNumberProvider loanApplicationNumberProvider

    def "New Number with dash"() {
        when:
        def number = loanApplicationNumberProvider.newNumber("1234567", "-", 3)

        then:
        number == "1234567-001"
    }

    def "New Number without dash"() {
        when:
        def number = loanApplicationNumberProvider.newNumber("123456", "", 2)

        then:
        number == "12345601"
    }
}
