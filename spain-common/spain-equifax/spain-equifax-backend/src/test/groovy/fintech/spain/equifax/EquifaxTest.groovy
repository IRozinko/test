package fintech.spain.equifax

import fintech.TimeMachine
import fintech.spain.equifax.mock.MockedEquifaxResponse
import fintech.spain.equifax.model.EquifaxQuery
import fintech.spain.equifax.model.EquifaxRequest
import fintech.spain.equifax.model.EquifaxStatus

class EquifaxTest extends BaseSpecification {

    def "Person not found"() {
        given:
        mockProvider.responseSupplier = MockedEquifaxResponse.NOT_FOUND

        when:
        def response = service.request(request())

        then:
        response.status == EquifaxStatus.NOT_FOUND
    }

    def "Person is found"() {
        given:
        mockProvider.responseSupplier = MockedEquifaxResponse.FOUND

        when:
        def response = service.request(request())

        then:
        response.status == EquifaxStatus.FOUND
        response.totalNumberOfOperations == 2
        response.numberOfConsumerCreditOperations == 2
        response.numberOfMortgageOperations == 3
        response.numberOfPersonalLoanOperations == 4
        response.numberOfCreditCardOperations == 5
        response.numberOfTelcoOperations == 6
        response.totalNumberOfOtherUnpaid == 7
        response.totalUnpaidBalance == 10.00
        response.unpaidBalanceOwnEntity == 102.0
        response.unpaidBalanceOfOther == 103.0
        response.unpaidBalanceOfConsumerCredit == 104.0
        response.unpaidBalanceOfMortgage == 105.0
        response.unpaidBalanceOfPersonalLoan == 106.0
        response.unpaidBalanceOfCreditCard == 107.0
        response.unpaidBalanceOfTelco == 108.0
        response.unpaidBalanceOfOtherProducts == 109.0
        response.worstUnpaidBalance == 110.0
        response.worstSituationCode == "01"
        response.numberOfDaysOfWorstSituation == 8
        response.numberOfCreditors == 9
        response.delincuencyDays == 10
    }

    def "Response failed - authentication"() {
        given:
        mockProvider.responseSupplier = MockedEquifaxResponse.ERROR_NOT_AUTHORIZED

        when:
        def response = service.request(request())

        then:
        response.status == EquifaxStatus.ERROR
        response.error == "10001: Authorization failed"
    }

    def "Response failed - data source not available"() {
        given:
        mockProvider.responseSupplier = MockedEquifaxResponse.ERROR_DS_NOT_AVAILABLE

        when:
        def response = service.request(request())

        then:
        response.status == EquifaxStatus.ERROR
        response.error == "10040: Datasource unavailable"
    }


    def "Runtime error"() {
        given:
        mockProvider.throwError = true

        when:
        def response = service.request(request())

        then:
        response.status == EquifaxStatus.ERROR
    }

    def "Find latest"() {
        expect:
        !service.findLatestResponse(new EquifaxQuery()).isPresent()

        when:
        service.request(new EquifaxRequest(clientId: 1L, applicationId: 2L, documentNumber: "46935017K"))
        def response2 = service.request(new EquifaxRequest(clientId: 1L, applicationId: 2L, documentNumber: "46935017K"))

        then:
        service.findLatestResponse(new EquifaxQuery()).isPresent()
        service.findLatestResponse(new EquifaxQuery(clientId: 1L, documentNumber: "46935017K", status: [EquifaxStatus.FOUND, EquifaxStatus.NOT_FOUND])).get().id == response2.id
        service.findLatestResponse(new EquifaxQuery(clientId: 1L, documentNumber: "46935017K", createdAfter: TimeMachine.now().minusMinutes(1))).isPresent()
        !service.findLatestResponse(new EquifaxQuery(clientId: 1L, documentNumber: "46935017K", createdAfter: TimeMachine.now().plusMinutes(1))).isPresent()
        !service.findLatestResponse(new EquifaxQuery(clientId: 2L)).isPresent()
        !service.findLatestResponse(new EquifaxQuery(documentNumber: "something")).isPresent()
        !service.findLatestResponse(new EquifaxQuery(status: [EquifaxStatus.ERROR])).isPresent()
    }

    EquifaxRequest request() {
        return new EquifaxRequest(clientId: 1L, applicationId: 2L, documentNumber: "46935017K")
    }
}
