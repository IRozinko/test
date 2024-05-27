package fintech.iovation

import fintech.iovation.impl.MockIovationProvider
import fintech.iovation.model.CheckTransactionCommand
import fintech.iovation.model.IovationStatus
import fintech.iovation.model.SaveBlackboxCommand

class IovationTest extends BaseSpecification {

    def "Error"() {
        given:
        mockProvider.setThrowError(true)

        when:
        def id = service.checkTransaction(new CheckTransactionCommand(clientId: 1L, applicationId: 2L, ipAddress: "192.168.0.1", clientNumber: "1001"))

        then:
        with(service.getTransaction(id)) {
            status == IovationStatus.ERROR
        }
    }

    def "Success"() {
        given:
        service.saveBlackbox(new SaveBlackboxCommand(clientId: 1L, ipAddress: "127.0.0.1", blackBox: "ignore"))
        service.saveBlackbox(new SaveBlackboxCommand(clientId: 1L, ipAddress: "192.168.0.1", blackBox: "correct one"))
        service.saveBlackbox(new SaveBlackboxCommand(clientId: 2L, ipAddress: "192.168.0.1", blackBox: "ignore"))

        when:
        def id = service.checkTransaction(new CheckTransactionCommand(clientId: 1L, applicationId: 2L, ipAddress: "192.168.0.1", clientNumber: "1001"))

        then:
        with(service.getTransaction(id)) {
            status == IovationStatus.OK
            clientId == 1L
            applicationId == 2L
            blackBox == "correct one"
            result == "A"
            reason == "MOCK"
            trackingNumber == MockIovationProvider.IOVATION_TRACKING_NUMBER
            deviceId == MockIovationProvider.IOVATION_DEVICE_ID
            details["device.screen"] == "1440X3440"
        }
    }
}
