package fintech.nordigen

import fintech.nordigen.model.NordigenQuery
import fintech.nordigen.model.NordigenRequestBody
import fintech.nordigen.model.NordigenRequestCommand
import fintech.nordigen.model.NordigenStatus

class NordigenTest extends BaseSpecification {

    def "Success"() {
        when:
        def result = service.request(new NordigenRequestCommand(clientId: 1L, applicationId: 2L, requestBody: new NordigenRequestBody()))

        then:
        result.status == NordigenStatus.OK
        result.json.accountList[0].factors.groupCash == 0.24799310209662
    }

    def "Failure"() {
        when:
        mockProvider.throwError = true
        def result = service.request(new NordigenRequestCommand(clientId: 1L, applicationId: 2L, requestBody: new NordigenRequestBody()))

        then:
        result.status == NordigenStatus.ERROR
        result.json == null
    }

    def "Find latest"() {
        expect:
        !service.findLatest(new NordigenQuery()).isPresent()

        when:
        def result1 = service.request(new NordigenRequestCommand(clientId: 1L, instantorResponseId: 2L, requestBody: new NordigenRequestBody()))

        then:
        service.findLatest(new NordigenQuery()).isPresent()
        service.findLatest(NordigenQuery.byInstantorResponseIdOk(2L)).get().id == result1.id

        when:
        def result2 = service.request(new NordigenRequestCommand(clientId: 1L, instantorResponseId: 2L, requestBody: new NordigenRequestBody()))

        then:
        service.findLatest(NordigenQuery.byInstantorResponseIdOk(2L)).get().id == result2.id

        and:
        !service.findLatest(NordigenQuery.byInstantorResponseIdOk(3L)).isPresent()
        service.findLatest(new NordigenQuery(clientId: 1L)).isPresent()
        !service.findLatest(new NordigenQuery(clientId: 2L)).isPresent()
        !service.findLatest(new NordigenQuery(clientId: 1L, statuses: [NordigenStatus.ERROR])).isPresent()
    }
}
