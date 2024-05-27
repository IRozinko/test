package fintech.crm

import fintech.crm.address.ClientAddressService
import fintech.crm.address.SaveClientAddressCommand
import fintech.crm.address.db.ClientAddressRepository
import org.springframework.beans.factory.annotation.Autowired

class ClientAddressTest extends BaseSpecification {

    @Autowired
    ClientAddressService addressService

    @Autowired
    ClientAddressRepository clientAddressRepository

    Long clientId

    def setup() {
        clientId = createClient()
    }

    def "Insert and updating addresses"() {
        SaveClientAddressCommand command = new SaveClientAddressCommand()
        command.clientId = clientId
        command.city = "Barcelona"
        command.type = "ACTUAL"
        command.houseLetter = "123"
        command.houseFloor = "5"

        when:
        addressService.addAddress(command)
        def savedAddress = addressService.getClientPrimaryAddress(clientId, "ACTUAL")

        then:
        assert savedAddress.isPresent()
        def clientAddress = savedAddress.get()
        with(clientAddress) {
            city == "Barcelona"
            houseLetter == "123"
            houseFloor == "5"
            type == "ACTUAL"
        }

        and:
        assert clientAddressRepository.count() == 1

        when:
        command.city = "Madrid"
        addressService.addAddress(command)

        then:
        addressService.getClientPrimaryAddress(clientId, "ACTUAL")
        addressService.getClientPrimaryAddress(clientId, "ACTUAL").get().city == "Madrid"

        and:
        assert clientAddressRepository.count() == 2
    }

}
