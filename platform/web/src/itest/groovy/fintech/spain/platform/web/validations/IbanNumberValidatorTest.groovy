package fintech.spain.platform.web.validations

import spock.lang.Specification
import spock.lang.Unroll

class IbanNumberValidatorTest extends Specification {

    @Unroll
    def 'Validate iban: #iban'() {
        expect:
        result == IbanNumberValidator.isValid(iban)

        where:
        iban                            | result
        "ES9121000418450200051332"      | true
        "es9121000418450200051332"      | true
        "ES9121000418450200051330"      | false
        "ES91 2100 0418 4502 0005 1332" | true
        ""                              | false
        "ES912100041845020005133"       | false
    }
}
