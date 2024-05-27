package fintech

import spock.lang.Specification

class ValidateTest extends Specification {

    def "IsEqual"() {
        expect:
        Validate.isEqual(1.0g, 1.0g, "")

        when:
        Validate.isEqual(1.0g, 1.1g, "")

        then:
        thrown IllegalArgumentException
    }

    def "IsNegative"() {
        expect:
        Validate.isNegative(-1.0g, "")

        when:
        Validate.isNegative(0.0g, "")

        then:
        thrown IllegalArgumentException
    }

    def "IsZeroOrPositive"() {
        expect:
        Validate.isZeroOrPositive(0.0g, "")
        Validate.isZeroOrPositive(1.0g, "")

        when:
        Validate.isZeroOrPositive(-1.0g, "")

        then:
        thrown IllegalArgumentException
    }

    def "IsZeroOrNegative"() {
        expect:
        Validate.isZeroOrNegative(0.0g, "")
        Validate.isZeroOrNegative(-1.0g, "")

        when:
        Validate.isZeroOrNegative(1.0g, "")

        then:
        thrown IllegalArgumentException
    }

    def "IsZero"() {
        expect:
        Validate.isZero(0.0g, "")

        when:
        Validate.isZero(0.01g, "")

        then:
        thrown IllegalArgumentException
    }

    def "IsPositive"() {
        expect:
        Validate.isPositive(1.0g, "")

        when:
        Validate.isPositive(0.0g, "")

        then:
        thrown IllegalArgumentException
    }
}
