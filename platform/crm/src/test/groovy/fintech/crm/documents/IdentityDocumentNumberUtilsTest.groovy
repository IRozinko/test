package fintech.crm.documents

import fintech.crm.documents.IdentityDocumentNumberUtils
import spock.lang.Specification
import spock.lang.Unroll

class IdentityDocumentNumberUtilsTest extends Specification {

    @Unroll
    def 'Valid DNI or NIE: #identificationNumber'() {
        expect:
        result == IdentityDocumentNumberUtils.isValidDniOrNie(identificationNumber)

        where:
        identificationNumber | result
        "A1111111B"          | false    // Valid NIE by regex, invalid by NIE logic
        "12345678K"          | false    // Valid DNI by regex, invalid by DNI logic
        "27916248K"          | true
        "Y4036104D"          | true
        "Z5331356F"          | true
        "X6835653F"          | true
    }

    @Unroll
    def 'Valid DNI: #identificationNumber'() {
        expect:
        result == IdentityDocumentNumberUtils.isValidDni(identificationNumber)

        where:
        identificationNumber | result
        "12345678A"          | false    // Invalid digits for last letter
        "01234567Z"          | false    // Invalid digits for last letter
        "23456789Z"          | false    // Invalid digits for last letter
        "12345678I"          | false    // Invalid letter
        "12345678O"          | false    // Invalid letter
        "12345678U"          | false    // Invalid letter
        "12345678I"          | false    // Invalid letter
        "12345678O"          | false    // Invalid letter
        "123456789"          | false    // Invalid letter
        "1234567U"           | false    // Invalid size (1 digit less)
        "123456789U"         | false    // Invalid size (1 digit more)
        "12345678Z"          | true
        "X4897840J"          | false
    }


}
