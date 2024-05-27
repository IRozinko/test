package fintech.spain.crosscheck

import fintech.spain.crosscheck.impl.MockSpainCrosscheckProvider
import fintech.spain.crosscheck.impl.SpainCrosscheckResponse
import fintech.spain.crosscheck.model.SpainCrosscheckRequestCommand
import fintech.spain.crosscheck.model.SpainCrosscheckStatus

class CrosscheckTest extends BaseSpecification {

    def "found"() {
        given:
        mockProvider.setResponse(new SpainCrosscheckResponse()
            .setError(false)
            .setResponseBody("{mock: true}")
            .setResponseStatusCode(200)
            .setAttributes(new SpainCrosscheckResponse.Attributes()
            .setFound(true)
            .setOpenLoans(3)
            .setMaxDpd(99)
            .setBlacklisted(true)
            .setClientNumber("C12345")
            .setRepeatedClient(true)
            .setPaidInvoices(0)
            .setUnpaidInvoices(0)
            .setActiveRequest(true)
            .setActiveRequestStatus("ExportDisbursement"))
        )

        when:
        def result = service.requestCrossCheck(new SpainCrosscheckRequestCommand(dni: "123", clientId: 1L, applicationId: 2L))
        result = service.get(result.id)

        then:
        result.status == SpainCrosscheckStatus.FOUND
        result.maxDpd == 99L
        result.openLoans == 3L
        result.blacklisted
        result.repeatedClient
        result.activeRequest
        result.activeRequestStatus == "ExportDisbursement"
    }

    def "not found"() {
        given:
        mockProvider.setResponse(MockSpainCrosscheckProvider.notFoundResponse())

        when:
        def result = service.requestCrossCheck(new SpainCrosscheckRequestCommand(dni: "123", clientId: 1L, applicationId: 2L))

        then:
        result.status == SpainCrosscheckStatus.NOT_FOUND
    }

    def "request fails"() {
        when:
        mockProvider.throwError = true
        def result = service.requestCrossCheck(new SpainCrosscheckRequestCommand(dni: "123", clientId: 1L, applicationId: 2L))

        then:
        result.status == SpainCrosscheckStatus.ERROR
    }
}
