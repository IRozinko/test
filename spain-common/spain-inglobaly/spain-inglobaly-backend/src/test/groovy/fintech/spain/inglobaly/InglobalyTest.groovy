package fintech.spain.inglobaly

import fintech.spain.inglobaly.impl.MockInglobalyProvider
import fintech.spain.inglobaly.model.InglobalyQuery
import fintech.spain.inglobaly.model.InglobalyRequest
import fintech.spain.inglobaly.model.InglobalyStatus

import static fintech.DateUtils.date

class InglobalyTest extends BaseSpecification {

    def "Found"() {
        when:
        mockProvider.setResponseResource(MockInglobalyProvider.RESPONSE_FOUND)
        def response = service.request(new InglobalyRequest(clientId: 1L, applicationId: 2L, documentNumber: "21896388C"))

        then:
        response.status == InglobalyStatus.FOUND
        response.dateOfBirth == date("1979-06-03")
        response.firstName == "ALEJANDRO"
        response.lastName == "CALVO"
        response.secondLastName == "FERNANDEZ"

        and:
        service.get(response.id).status == InglobalyStatus.FOUND
    }

    def "Not found"() {
        given:
        mockProvider.returnNotFound()

        when:
        def response = service.request(new InglobalyRequest(clientId: 1L, applicationId: 2L, documentNumber: "21896388C"))

        then:
        response.status == InglobalyStatus.NOT_FOUND
    }

    def "Request fails"() {
        given:
        mockProvider.setThrowError(true)

        when:
        def response = service.request(new InglobalyRequest(clientId: 1L, applicationId: 2L, documentNumber: "21896388C"))

        then:
        response.status == InglobalyStatus.ERROR
        response.clientId == 1L
        response.applicationId == 2L
        response.requestedDocumentNumber == "21896388C"
    }

    def "Find latest"() {
        expect:
        !service.findLatest(new InglobalyQuery().setClientId(1L)).isPresent()

        when:
        mockProvider.setResponseResource(MockInglobalyProvider.RESPONSE_FOUND)
        def response1 = service.request(new InglobalyRequest(clientId: 1L, applicationId: 2L, documentNumber: "21896388C"))
        mockProvider.returnNotFound()
        def response2 = service.request(new InglobalyRequest(clientId: 1L, applicationId: 2L, documentNumber: "21896388C"))

        then:
        service.findLatest(new InglobalyQuery().setClientId(1L)).get().id == response2.id
        service.findLatest(new InglobalyQuery().setDocumentNumber("21896388C")).get().id == response2.id
        service.findLatest(new InglobalyQuery().setClientId(1L).setStatus([InglobalyStatus.FOUND])).get().id == response1.id
        !service.findLatest(new InglobalyQuery().setClientId(2L)).isPresent()
    }
}
