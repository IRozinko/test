package fintech.crm

import fintech.crm.documents.AddIdentityDocumentCommand
import fintech.crm.documents.DuplicateDocumentNumberException
import fintech.crm.documents.IdentityDocumentService
import fintech.crm.documents.db.IdentityDocumentRepository
import org.springframework.beans.factory.annotation.Autowired

class IdentityDocumentServiceTest extends BaseSpecification {

    Long clientId
    Long anotherClientId

    @Autowired
    private IdentityDocumentService identityDocumentService

    @Autowired
    private IdentityDocumentRepository repository

    def setup() {
        clientId = createClient()
        anotherClientId = createClient()
    }

    def "Add and get identity document"() {
        when:
        def idCard = identityDocumentService.findPrimaryDocument(clientId, "ID_CARD")

        then:
        idCard == Optional.empty()

        when:
        def id = identityDocumentService.addDocument(new AddIdentityDocumentCommand(clientId: clientId, type: "ID_CARD", number: "ABC123"))
        identityDocumentService.makeDocumentPrimary(id)
        idCard = identityDocumentService.findPrimaryDocument(clientId, "ID_CARD")

        then:
        idCard.isPresent()

        with(idCard.get()) {
            type == "ID_CARD"
            number == "ABC123"
        }
    }

    def "Can't add already registered identity document"() {
        when:
        def id1 = identityDocumentService.addDocument(new AddIdentityDocumentCommand(clientId: clientId, type: "ID_CARD", number: "ABC123"))
        identityDocumentService.makeDocumentPrimary(id1)

        and:
        def id2 = identityDocumentService.addDocument(new AddIdentityDocumentCommand(clientId: anotherClientId, type: "ID_CARD", number: "ABC123"))
        identityDocumentService.makeDocumentPrimary(id2)

        then:
        thrown(DuplicateDocumentNumberException.class)
    }

    def "Don't update existing identity document for user"() {
        when:
        def identityDocumentId = identityDocumentService.addDocument(new AddIdentityDocumentCommand(clientId: clientId, type: "ID_CARD", number: "ABC123"))

        and:
        identityDocumentService.addDocument(new AddIdentityDocumentCommand(clientId: clientId, type: "ID_CARD", number: "ABC123"))

        then:
        def identityDocumentEntity = repository.findOne(identityDocumentId)

        assert identityDocumentEntity.type == "ID_CARD"
        assert identityDocumentEntity.number == "ABC123"
        assert identityDocumentEntity.client.id == clientId
        assert identityDocumentEntity.id == identityDocumentId
    }

    def "Should not have two primary documents"() {
        when:
        def id1 = identityDocumentService.addDocument(new AddIdentityDocumentCommand(clientId: clientId, type: "ID_CARD", number: "ABC123"))
        identityDocumentService.makeDocumentPrimary(id1)
        def id2 = identityDocumentService.addDocument(new AddIdentityDocumentCommand(clientId: clientId, type: "ID_CARD", number: "ABC1234"))
        identityDocumentService.makeDocumentPrimary(id2)

        then:
        identityDocumentService.findPrimaryDocument(clientId, "ID_CARD").get().id == id2
    }

    def "Find by number"() {
        when:
        def id1 = identityDocumentService.addDocument(new AddIdentityDocumentCommand(clientId: clientId, type: "ID_CARD", number: "ABC123"))
        identityDocumentService.makeDocumentPrimary(id1)
        identityDocumentService.addDocument(new AddIdentityDocumentCommand(clientId: clientId, type: "DOCUMENT", number: "ABC123"))
        identityDocumentService.addDocument(new AddIdentityDocumentCommand(clientId: clientId, type: "ID_CARD", number: "AA111"))
        identityDocumentService.addDocument(new AddIdentityDocumentCommand(clientId: anotherClientId, type: "ID_CARD", number: "ABC123"))

        then:
        identityDocumentService.findByNumber("ABC123", "ID_CARD", true).get().id == id1
        !identityDocumentService.findByNumber("ABC1234", "ID_CARD", true).isPresent()
    }
}
