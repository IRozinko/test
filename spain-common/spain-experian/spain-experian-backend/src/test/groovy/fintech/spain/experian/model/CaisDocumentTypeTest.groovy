package fintech.spain.experian.model

import spock.lang.Specification

class CaisDocumentTypeTest extends Specification {

    def "Get document type"() {
        expect:
        CaisDocumentType.getTypeOfDocumentNumber("51444375E") == CaisDocumentType.NIF
        CaisDocumentType.getTypeOfDocumentNumber("77404869W") == CaisDocumentType.NIF
        CaisDocumentType.getTypeOfDocumentNumber("X5692341W") == CaisDocumentType.NIE
        CaisDocumentType.getTypeOfDocumentNumber("G44418754") == CaisDocumentType.CIF

        when:
        CaisDocumentType.getTypeOfDocumentNumber("1")

        then:
        thrown(IllegalArgumentException.class)
    }
}
