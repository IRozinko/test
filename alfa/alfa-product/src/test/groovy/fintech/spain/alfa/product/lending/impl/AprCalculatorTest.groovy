package fintech.spain.alfa.product.lending.impl

import spock.lang.Specification

class AprCalculatorTest extends Specification {

    def "calculate"() {
        expect:
        AprCalculator.calculate(300.00, 105.00, 30) == 3752.37
        AprCalculator.calculate(50.00, 18.00, 30) == 4114.28
        AprCalculator.calculate(300.00, 25.00, 7) == 6395.25
        AprCalculator.calculate(300.00, 25.00, 1) == 999999999.9
    }
}
