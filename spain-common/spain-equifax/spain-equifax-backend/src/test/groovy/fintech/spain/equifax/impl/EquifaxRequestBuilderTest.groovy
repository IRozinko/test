package fintech.spain.equifax.impl

import fintech.spain.equifax.xml.EquifaxRequestBuilder
import fintech.spain.equifax.xml.RequestInput
import spock.lang.Specification

class EquifaxRequestBuilderTest extends Specification {

    def "Build"() {
        when:
        def request = new EquifaxRequestBuilder(new RequestInput(
            documentNumber: "26128063V",
        )).build()

        then:
        def subject = request.consumerSubjects.consumerSubject[0]
        subject.identification[0].spainDocument == "26128063V"
        subject.identification[0].spainDocumentType == "NIF"
    }
}
