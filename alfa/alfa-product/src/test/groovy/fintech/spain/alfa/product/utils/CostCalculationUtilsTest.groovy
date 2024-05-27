package fintech.spain.alfa.product.utils

import fintech.transactions.Balance
import spock.lang.Specification

class CostCalculationUtilsTest extends Specification {

    def "available costs"() {
        given:
        def balance = new Balance()

        when:
        balance.setPrincipalDisbursed(principal)
        balance.setInterestApplied(interest)

        then:
        maxPenalties == CostCalculationUtils.availableCosts(balance)

        where:
        principal | interest | maxPenalties
        300.00    | 105.00   | 345.00
        500.00    | 0.00     | 750.00
        500.00    | 750.00   | 0.00
    }

}
