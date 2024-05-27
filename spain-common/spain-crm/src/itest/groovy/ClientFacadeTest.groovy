import fintech.crm.CrmConstants
import fintech.crm.client.ClientService
import fintech.crm.client.CreateClientCommand
import fintech.crm.client.db.ClientRepository
import fintech.crm.contacts.AddEmailContactCommand
import fintech.crm.contacts.AddPhoneCommand
import fintech.crm.contacts.EmailContactService
import fintech.crm.contacts.PhoneContactService
import fintech.crm.contacts.PhoneType
import fintech.crm.documents.AddIdentityDocumentCommand
import fintech.crm.documents.IdentityDocumentService
import fintech.crm.logins.AddEmailLoginCommand
import fintech.crm.logins.EmailLoginService
import fintech.risk.checklist.CheckListService
import fintech.risk.checklist.commands.AddCheckListTypeCommand
import fintech.spain.crm.client.ClientFacade
import fintech.testing.integration.AbstractBaseSpecification
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.orm.jpa.JpaObjectRetrievalFailureException
import org.springframework.transaction.support.TransactionTemplate
import spock.lang.Subject

import static fintech.risk.checklist.CheckListConstants.CHECKLIST_TYPE_DNI
import static fintech.risk.checklist.CheckListConstants.CHECKLIST_TYPE_EMAIL
import static fintech.risk.checklist.CheckListConstants.CHECKLIST_TYPE_PHONE
import static fintech.risk.checklist.model.CheckListAction.BLACKLIST

class ClientFacadeTest extends AbstractBaseSpecification {

    @Subject
    @Autowired
    ClientFacade clientFacade

    @Autowired
    IdentityDocumentService identityDocumentService

    @Autowired
    ClientService clientService

    @Autowired
    CheckListService checkListService

    @Autowired
    EmailContactService emailContactService

    @Autowired
    EmailLoginService emailLoginService

    @Autowired
    PhoneContactService phoneContactService

    @Autowired
    TransactionTemplate txTemplate

    @Autowired
    ClientRepository clientRepository

    def setup() {
        testDatabase.cleanDb()

        checkListService.addType(AddCheckListTypeCommand.builder().type(CHECKLIST_TYPE_DNI).action(BLACKLIST).build())
        checkListService.addType(AddCheckListTypeCommand.builder().type(CHECKLIST_TYPE_EMAIL).action(BLACKLIST).build())
        checkListService.addType(AddCheckListTypeCommand.builder().type(CHECKLIST_TYPE_PHONE).action(BLACKLIST).build())
    }

    def "blacklist document not in database"() {
        when:
        clientFacade.blacklistDocument(1001L, CrmConstants.IDENTITY_DOCUMENT_DNI, "test")

        then:
        noExceptionThrown()

        and:
        !clientFacade.isDocumentBlacklisted(1001L, CrmConstants.IDENTITY_DOCUMENT_DNI)
    }

    def "do not blacklist document not marked as primary"() {
        given:
        def clientId = clientService.create(new CreateClientCommand("123"))
        identityDocumentService.addDocument(new AddIdentityDocumentCommand(clientId: clientId, type: CrmConstants.IDENTITY_DOCUMENT_DNI, number: "12345678"))

        when:
        clientFacade.blacklistDocument(clientId, CrmConstants.IDENTITY_DOCUMENT_DNI, "test")

        then:
        !clientFacade.isDocumentBlacklisted(clientId, CrmConstants.IDENTITY_DOCUMENT_DNI)
    }

    def "do not blacklist document with wrong type"() {
        given:
        def clientId = clientService.create(new CreateClientCommand("123"))
        def documentId = identityDocumentService.addDocument(new AddIdentityDocumentCommand(clientId: clientId, type: "anotherType", number: "12345678"))
        identityDocumentService.makeDocumentPrimary(documentId)

        when:
        clientFacade.blacklistDocument(clientId, CrmConstants.IDENTITY_DOCUMENT_DNI, "test")

        then:
        !clientFacade.isDocumentBlacklisted(clientId, CrmConstants.IDENTITY_DOCUMENT_DNI)
    }

    def "blacklist document"() {
        given:
        def clientId = clientService.create(new CreateClientCommand("123"))
        def documentId = identityDocumentService.addDocument(new AddIdentityDocumentCommand(clientId: clientId, type: CrmConstants.IDENTITY_DOCUMENT_DNI, number: "12345678"))
        identityDocumentService.makeDocumentPrimary(documentId)

        when:
        clientFacade.blacklistDocument(clientId, CrmConstants.IDENTITY_DOCUMENT_DNI, "test")

        then:
        clientFacade.isDocumentBlacklisted(clientId, CrmConstants.IDENTITY_DOCUMENT_DNI)
    }

    def "blacklist email not in database"() {
        when:
        clientFacade.blacklistEmail(1001L, "test")

        then:
        noExceptionThrown()

        and:
        !clientFacade.isEmailBlacklisted(1001L)
    }

    def "do not blacklist email not used for login"() {
        given:
        def clientId = clientService.create(new CreateClientCommand("123"))
        emailContactService.addEmailContact(new AddEmailContactCommand(clientId: clientId, email: "test@test.com"))

        when:
        clientFacade.blacklistEmail(clientId, "test")

        then:
        !clientFacade.isEmailBlacklisted(clientId)
    }

    def "do not blacklist primary email not used for login"() {
        given:
        def clientId = clientService.create(new CreateClientCommand("123"))
        def emailId = emailContactService.addEmailContact(new AddEmailContactCommand(clientId: clientId, email: "test@test.com"))
        emailContactService.makeEmailPrimary(emailId)

        when:
        clientFacade.blacklistEmail(clientId, "test")

        then:
        !clientFacade.isEmailBlacklisted(clientId)
    }

    def "blacklist email used for login"() {
        given:
        def clientId = clientService.create(new CreateClientCommand("123"))
        emailLoginService.add(new AddEmailLoginCommand(clientId, "test@test.com", "password", false))

        when:
        clientFacade.blacklistEmail(clientId, "test")

        then:
        clientFacade.isEmailBlacklisted(clientId)
    }

    def "blacklist phone not in database"() {
        when:
        clientFacade.blacklistPhone(1001L, "test")

        then:
        thrown(JpaObjectRetrievalFailureException.class)
    }

    def "blacklist phone contact"() {
        given:
        def clientId = clientService.create(new CreateClientCommand("123"))
        phoneContactService.addPhoneContact(new AddPhoneCommand(clientId: clientId, countryCode: "34", localNumber: "123456", type: PhoneType.MOBILE))
        txTemplate.execute {
            clientRepository.getRequired(clientId).phone = "000000"
        }

        when:
        clientFacade.blacklistPhone(clientId, "test")

        then:
        clientFacade.isPhoneBlacklisted(clientId)
    }

    def "blacklist primary phone contact"() {
        given:
        def clientId = clientService.create(new CreateClientCommand("123"))
        def phoneId = phoneContactService.addPhoneContact(new AddPhoneCommand(clientId: clientId, countryCode: "34", localNumber: "123456", type: PhoneType.MOBILE))
        phoneContactService.makePhonePrimary(phoneId)

        when:
        clientFacade.blacklistPhone(clientId, "test")

        then:
        clientFacade.isPhoneBlacklisted(clientId)
    }

    def "no errors blacklisting null phone number"() {
        given:
        def clientId = clientService.create(new CreateClientCommand("123"))

        when:
        clientFacade.blacklistPhone(clientId, "test")

        then:
        noExceptionThrown()

        and:
        !clientFacade.isPhoneBlacklisted(clientId)
    }
}
