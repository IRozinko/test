package fintech.crm

import fintech.TimeMachine
import fintech.crm.client.ClientService
import fintech.crm.client.CreateClientCommand
import fintech.crm.client.UpdateClientCommand
import fintech.crm.client.db.ClientRepository
import fintech.crm.client.model.ChangeAcceptMarketingCommand
import fintech.crm.client.util.ClientNumberGenerator
import fintech.crm.marketing.impl.MarketingConsentLogRepository
import org.apache.commons.lang3.RandomStringUtils
import org.springframework.beans.factory.annotation.Autowired

class ClientServiceTest extends BaseSpecification {

    @Autowired
    ClientService clientService

    @Autowired
    ClientRepository clientRepository

    @Autowired
    ClientNumberGenerator clientNumberGenerator

    @Autowired
    MarketingConsentLogRepository marketingConsentLogRepository

    def "Client gets registered"() {
        when:
        def clientId = clientService.create(new CreateClientCommand(clientNumberGenerator.newNumber("C", 7)))

        then:
        clientId != null

        when:
        def client = clientRepository.getOptional(clientId)

        then:
        client.isPresent()
        client.get().number != null
    }

    def "Update client"() {
        when:
        def clientId = clientService.create(new CreateClientCommand(clientNumberGenerator.newNumber("C", 7)))
        def command = UpdateClientCommand.fromClient(clientService.get(clientId))
        command.setFirstName("John")
        command.setLastName("Smith")
        command.setAcceptMarketing(true)
        command.setAcceptTerms(true)
        command.setAcceptVerification(true)
        clientService.update(command)
        def updatedClient = clientService.get(clientId)

        then:
        updatedClient.firstName == "John"
        updatedClient.lastName == "Smith"
        updatedClient.acceptMarketing
        updatedClient.acceptTerms
        updatedClient.acceptVerification


        when:
        clientService.update(UpdateClientCommand.fromClient(clientService.get(clientId)))
        updatedClient = clientService.get(clientId)

        then:
        updatedClient.firstName == "John"
        updatedClient.lastName == "Smith"
        updatedClient.acceptMarketing
        updatedClient.acceptTerms
        updatedClient.acceptVerification
    }

    def "update of marketing consent should be logged"() {
        given:
        def clientId = clientService.create(new CreateClientCommand(RandomStringUtils.randomAlphanumeric(10)))

        expect:
        with(clientService.get(clientId)) {
            !acceptMarketing
        }

        when:
        clientService.update(UpdateClientCommand.fromClient(clientService.get(clientId)).setAcceptMarketing(true))

        then:
        with(clientService.get(clientId)) {
            acceptMarketing
        }
        with(marketingConsentLogRepository.findByClientId(clientId)) {
            size() == 1
            with(get(0)) {
                clientId == clientId
                timestamp != null
                value

            }
        }

        when:
        clientService.update(UpdateClientCommand.fromClient(clientService.get(clientId)).setAcceptMarketing(true))

        then:
        marketingConsentLogRepository.findByClientId(clientId).size() == 1
    }

    def "Client segments"() {
        given:
        def clientId = clientService.create(new CreateClientCommand(clientNumberGenerator.newNumber("C", 7)))

        expect:
        clientService.get(clientId).segments.empty

        when:
        clientService.addToSegment(clientId, TimeMachine.now(), "new", "registered")
        def segments = clientService.get(clientId).segments

        then:
        segments.size() == 2
        clientService.get(clientId).isInSegment("new")
        clientService.get(clientId).isInSegment("registered")
        !clientService.get(clientId).isInSegment("identified")

        when:
        clientService.removeFromSegment(clientId, "new", "registered")
        clientService.addToSegment(clientId, TimeMachine.now(), "identified")
        segments = clientService.get(clientId).segments

        then:
        segments.size() == 1
        !clientService.get(clientId).isInSegment("new")
        !clientService.get(clientId).isInSegment("registered")
        clientService.get(clientId).isInSegment("identified")

        when:
        clientService.removeFromSegment(clientId, "identified")

        then:
        clientService.get(clientId).segments.empty
    }

    def "Save null or blank attribute values"() {
        given:
        def clientId = clientService.create(new CreateClientCommand(clientNumberGenerator.newNumber("C", 7)))
        def client = clientService.get(clientId)

        when:
        def command = UpdateClientCommand.fromClient(client)
        command.attributes.put("test", null)
        command.attributes.put("test2", "")
        clientService.update(command)

        then:
        with(clientService.get(clientId)) {
            assert attributes.containsKey("test")
            assert attributes["test"] == ""
            assert attributes.containsKey("test2")
            assert attributes["test2"] == ""
        }
    }

    def "Update accept marketing flag"() {
        given:
        def clientId = clientService.create(new CreateClientCommand(clientNumberGenerator.newNumber("C", 7)))

        when:
        clientService.updateAcceptMarketing(new ChangeAcceptMarketingCommand(clientId, true))

        then:
        clientService.get(clientId).acceptMarketing

        when: "re-updating same flag"
        clientService.updateAcceptMarketing(new ChangeAcceptMarketingCommand(clientId, true))

        then:
        clientService.get(clientId).acceptMarketing

        when: "set to false"
        clientService.updateAcceptMarketing(new ChangeAcceptMarketingCommand(clientId, false))

        then:
        !clientService.get(clientId).acceptMarketing
    }
}
