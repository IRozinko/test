package fintech

import spock.lang.Specification

import static fintech.BigDecimalUtils.amount

class BigDecimalUtilsTest extends Specification {

    def "IsEqual"() {
        expect:
        BigDecimalUtils.eq(1.01g, 1.01g)
        !BigDecimalUtils.eq(1.01g, 1.02g)
    }

    def "IsLe"() {
        expect:
        BigDecimalUtils.loe(1.01g, 1.01g)
        BigDecimalUtils.loe(1.01g, 1.02g)
        !BigDecimalUtils.loe(1.01g, 1.00g)
    }

    def "IsGe"() {
        expect:
        BigDecimalUtils.goe(1.02g, 1.01g)
        BigDecimalUtils.goe(1.02g, 1.02g)
        !BigDecimalUtils.goe(1.02g, 1.03g)
    }

    def "IsG"() {
        expect:
        BigDecimalUtils.gt(1.02g, 1.01g)
        !BigDecimalUtils.gt(1.02g, 1.02g)
    }

    def "IsL"() {
        expect:
        BigDecimalUtils.lt(1.01g, 1.02g)
        !BigDecimalUtils.lt(1.01g, 1.01g)
    }

    def "IsZero"() {
        expect:
        BigDecimalUtils.isZero(0.0g)
        !BigDecimalUtils.isZero(0.1g)
    }

    def "Amount"() {
        expect:
        amount(0.125g) == 0.12g
        amount("0.125") == 0.12g
        amount(123L) == 123.0g
    }

    def "Sum"() {
        expect:
        BigDecimalUtils.sum().apply(1.0g, 2.0g) == 3.0g
    }

    def "Max"() {
        expect:
        BigDecimalUtils.max(1.0g, 2.0g) == 2.0g
    }

    def "Min"() {
        expect:
        BigDecimalUtils.min(1.0g, 2.0g) == 1.0g
    }

    def "normalize"() {
        expect:
        BigDecimalUtils.normalize(0.0, 0.0) == 0.0
        BigDecimalUtils.normalize(49.99, 50.0) == 0.0
        BigDecimalUtils.normalize(50.0, 50.0) == 50.0
        BigDecimalUtils.normalize(50.99, 50.0) == 50.0
        BigDecimalUtils.normalize(51.00, 50.0) == 50.0
        BigDecimalUtils.normalize(100.00, 50.0) == 100.0
        BigDecimalUtils.normalize(199.99, 50.0) == 150.0
    }

    def "divide by payments"() {
        given:
        def amount = 900.0

        when:
        def result = BigDecimalUtils.divideByPayments(amount, 3)

        then:
        result.size() == 3
        result[0] == 300.0
        result[1] == 300.0
        result[2] == 300.0
    }

    def "divide by payments with multiple decimals"() {
        given:
        def amount = 1000.0001

        when:
        def result = BigDecimalUtils.divideByPayments(amount, 3)

        then:
        result.size() == 3
        result[0] == 333.33
        result[1] == 333.33
        result[2] == 333.3401
    }

    def "multiplyByHundred"() {
        expect:
        BigDecimalUtils.multiplyByHundred(amount(100)) == 10000
    }

    def "divideByHundred"() {
        expect:
        BigDecimalUtils.divideByHundred(1000) == 10
    }
}
