package fintech.risk.checklist

import fintech.crm.client.ClientService
import fintech.crm.client.CreateClientCommand
import fintech.crm.client.UpdateClientCommand
import fintech.crm.client.db.ClientRepository
import fintech.crm.client.util.ClientNumberGenerator
import fintech.crm.documents.AddIdentityDocumentCommand
import fintech.crm.documents.IdentityDocumentService
import fintech.risk.checklist.commands.AddCheckListEntryCommand
import fintech.risk.checklist.commands.AddCheckListTypeCommand
import fintech.risk.checklist.db.CheckListEntryRepository
import fintech.risk.checklist.db.CheckListTypeRepository
import fintech.risk.checklist.model.CheckListAction
import fintech.risk.checklist.model.CheckListQuery
import org.springframework.beans.factory.annotation.Autowired

class CheckListTest extends BaseSpecification {

    @Autowired
    CheckListService service

    @Autowired
    ClientService clientService

    @Autowired
    ClientRepository clientRepository

    @Autowired
    ClientNumberGenerator clientNumberGenerator

    @Autowired
    private IdentityDocumentService identityDocumentService

    @Autowired
    CheckListTypeRepository typeRepository

    @Autowired
    CheckListEntryRepository entryRepository

    def "Whitelist"() {
        given:
        service.addType(new AddCheckListTypeCommand(type: "IP_ADDRESS", action: CheckListAction.WHITELIST))

        expect: "No entries in whitelist, allow all"
        service.isAllowed(CheckListQuery.builder().type("IP_ADDRESS").value1("127.0.0.1").build())

        when:
        service.addEntry(new AddCheckListEntryCommand(type: "IP_ADDRESS", value1: "127.0.0.1", comment: "test"))

        then:
        service.isAllowed(CheckListQuery.builder().type("IP_ADDRESS").value1("127.0.0.1").build())
        !service.isAllowed(CheckListQuery.builder().type("IP_ADDRESS").value1("127.0.0.2").build())
    }

    def "Blacklist"() {
        given:
        service.addType(new AddCheckListTypeCommand(type: "IP_ADDRESS", action: CheckListAction.BLACKLIST))

        expect:
        service.isAllowed(CheckListQuery.builder().type("IP_ADDRESS").value1("127.0.0.1").build())

        when:
        service.addEntry(new AddCheckListEntryCommand(type: "IP_ADDRESS", value1: "127.0.0.1"))

        then:
        !service.isAllowed(CheckListQuery.builder().type("IP_ADDRESS").value1("127.0.0.1").build())
        service.isAllowed(CheckListQuery.builder().type("IP_ADDRESS").value1("127.0.0.2").build())
    }

    def "Adding same type two times"() {
        expect:
        typeRepository.count() == 0

        when:
        service.addType(new AddCheckListTypeCommand(type: "IP_ADDRESS", action: CheckListAction.WHITELIST))
        service.addType(new AddCheckListTypeCommand(type: "IP_ADDRESS", action: CheckListAction.WHITELIST))

        then:
        typeRepository.count() == 1
    }

    def "Adding same entry two times"() {
        given:
        service.addType(new AddCheckListTypeCommand(type: "IP_ADDRESS", action: CheckListAction.WHITELIST))

        expect:
        entryRepository.count() == 0

        when:
        service.addEntry(new AddCheckListEntryCommand(type: "IP_ADDRESS", value1: "127.0.0.1"))
        service.addEntry(new AddCheckListEntryCommand(type: "IP_ADDRESS", value1: "127.0.0.1"))

        then:
        entryRepository.count() == 1
    }


    def "Adding entry to Blacklist and check client is blocked for communications"() {

        given:
        def dni = "ABC12345"
        service.addType(new AddCheckListTypeCommand(type: "DNI", action: CheckListAction.BLACKLIST))

        when:
        def clientId = clientService.create(new CreateClientCommand(clientNumberGenerator.newNumber("C", 7)))
        def id1 = identityDocumentService.addDocument(new AddIdentityDocumentCommand(clientId: clientId, type: "ID_CARD", number: dni))
        identityDocumentService.makeDocumentPrimary(id1)

        def command = UpdateClientCommand.fromClient(clientService.get(clientId))
        command.setBlockCommunication(false)
        clientService.update(command)
        def updatedClient = clientService.get(clientId)

        then:
        updatedClient.getDocumentNumber() == dni
        !updatedClient.blockCommunication

        when:
        service.addEntry(new AddCheckListEntryCommand(type: "DNI", value1: dni))

        then:
        clientService.get(clientId).blockCommunication
    }

}
