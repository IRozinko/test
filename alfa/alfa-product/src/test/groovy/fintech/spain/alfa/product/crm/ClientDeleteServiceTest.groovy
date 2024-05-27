package fintech.spain.alfa.product.crm

import fintech.TimeMachine
import fintech.crm.CrmConstants
import fintech.crm.bankaccount.ClientBankAccountService
import fintech.crm.client.Gender
import fintech.crm.client.db.ClientRepository
import fintech.crm.contacts.EmailContactService
import fintech.crm.contacts.PhoneContactService
import fintech.crm.contacts.PhoneType
import fintech.crm.documents.IdentityDocumentService
import fintech.crm.logins.EmailLoginService
import fintech.spain.crm.client.ClientDeleteService
import fintech.spain.alfa.product.AbstractAlfaTest
import fintech.workflow.WorkflowQuery
import fintech.workflow.WorkflowService
import fintech.workflow.WorkflowStatus
import org.springframework.beans.factory.annotation.Autowired
import spock.lang.Subject

import static fintech.BigDecimalUtils.amount

class ClientDeleteServiceTest extends AbstractAlfaTest {

    @Subject
    @Autowired
    ClientDeleteService clientDeleteService

    @Autowired
    ClientRepository clientRepository

    @Autowired
    EmailContactService emailContactService

    @Autowired
    PhoneContactService phoneContactService

    @Autowired
    EmailLoginService emailLoginService

    @Autowired
    IdentityDocumentService identityDocumentService

    @Autowired
    ClientBankAccountService clientBankAccountService

    @Autowired
    WorkflowService workflowService

    def "soft delete is keeping the data"() {
        given:
        fintech.spain.alfa.product.testing.TestClient client = fintech.spain.alfa.product.testing.TestFactory.newClient()
            .registerDirectly()
            .submitApplicationAndStartFirstLoanWorkflow(amount(300), 30L, TimeMachine.today())

        when:
        clientDeleteService.softDelete(client.clientId)

        then: "client data"
        clientRepository.count() == 1
        with(clientRepository.getRequired(client.clientId)) {
            firstName == client.signUpForm.firstName
            lastName == client.signUpForm.lastName
            secondLastName == client.signUpForm.secondLastName
            phone == client.signUpForm.mobilePhone
            gender == Gender.valueOf(client.applicationForm.gender)
            deleted
            !acceptMarketing
            blockCommunication
        }

        and: "email contacts"
        def emailContacts = emailContactService.findAllEmailContacts(client.clientId)
        emailContacts.size() == 1
        with(emailContacts[0]) {
            email == client.signUpForm.email
            primary
        }

        and: "phone contacts"
        def phoneContacts = phoneContactService.findClientPhoneContacts(client.clientId)
        phoneContacts.size() == 1
        with(phoneContacts.find({ p -> p.phoneType == PhoneType.MOBILE })) {
            localNumber == client.signUpForm.mobilePhone
            primary
        }

        and: "email for login"
        !emailLoginService.findByClientId(client.clientId).isPresent()

        and: "identity documents"
        def documents = identityDocumentService.findPrimaryDocuments(client.clientId)
        documents.size() == 1
        with(documents[0]) {
            type == CrmConstants.IDENTITY_DOCUMENT_DNI
            number == client.signUpForm.documentNumber
        }

        and: "bank account"
        def accounts = clientBankAccountService.findAllByClientId(client.clientId)
        accounts.size() == 1
        with(accounts[0]) {
            accountNumber == client.iban.toString()
            primary
        }

        and: "active workflows"
        def workflows = workflowService.findWorkflows(WorkflowQuery.byClientId(client.clientId))
        workflows.size() == 1
        with(workflows[0]) {
            status == WorkflowStatus.TERMINATED
            terminateReason == "Client soft delete"
        }
    }

    def "it is possible to register with the email of a soft deleted client"() {
        given:
        fintech.spain.alfa.product.testing.TestClient clientDeleted = fintech.spain.alfa.product.testing.TestFactory.newClient()
            .registerDirectly()
            .submitApplicationAndStartFirstLoanWorkflow(amount(300), 30L, TimeMachine.today())
        clientDeleteService.softDelete(clientDeleted.clientId)

        when:
        fintech.spain.alfa.product.testing.TestClient client = fintech.spain.alfa.product.testing.TestFactory.newClient()
            .setEmail(clientDeleted.signUpForm.email)
            .registerDirectly()
            .submitApplicationAndStartFirstLoanWorkflow(amount(300), 30L, TimeMachine.today())

        then:
        noExceptionThrown()
        clientRepository.count() == 2

        and:
        def emailContacts = emailContactService.findAllEmailContacts(client.clientId)
        emailContacts.size() == 1
        with(emailContacts[0]) {
            email == clientDeleted.signUpForm.email
            primary
        }

        and: "email for login"
        def email = emailLoginService.findByClientId(client.clientId)
        email.isPresent()
        email.get().email == clientDeleted.signUpForm.email.toLowerCase()
    }

    def "it is possible to register with the phone of a soft deleted client"() {
        given:
        fintech.spain.alfa.product.testing.TestClient clientDeleted = fintech.spain.alfa.product.testing.TestFactory.newClient()
            .registerDirectly()
            .submitApplicationAndStartFirstLoanWorkflow(amount(300), 30L, TimeMachine.today())
        clientDeleteService.softDelete(clientDeleted.clientId)

        when:
        fintech.spain.alfa.product.testing.TestClient client = fintech.spain.alfa.product.testing.TestFactory.newClient()
            .setMobilePhone(clientDeleted.signUpForm.mobilePhone)
            .registerDirectly()
            .submitApplicationAndStartFirstLoanWorkflow(amount(300), 30L, TimeMachine.today())

        then:
        noExceptionThrown()
        clientRepository.count() == 2

        and:
        def phoneContacts = phoneContactService.findClientPhoneContacts(client.clientId)
        phoneContacts.size() == 1
        with(phoneContacts.find({ p -> p.phoneType == PhoneType.MOBILE })) {
            localNumber == clientDeleted.signUpForm.mobilePhone
            primary
        }
    }

    def "it is possible to register with the identity document of a soft deleted client"() {
        given:
        fintech.spain.alfa.product.testing.TestClient clientDeleted = fintech.spain.alfa.product.testing.TestFactory.newClient()
            .registerDirectly()
            .submitApplicationAndStartFirstLoanWorkflow(amount(300), 30L, TimeMachine.today())
        clientDeleteService.softDelete(clientDeleted.clientId)

        when:
        fintech.spain.alfa.product.testing.TestClient client = fintech.spain.alfa.product.testing.TestFactory.newClient()
            .setDni(clientDeleted.dni)
            .registerDirectly()
            .submitApplicationAndStartFirstLoanWorkflow(amount(300), 30L, TimeMachine.today())

        then:
        noExceptionThrown()
        clientRepository.count() == 2

        and:
        def documents = identityDocumentService.findPrimaryDocuments(client.clientId)
        documents.size() == 1
        with(documents[0]) {
            type == CrmConstants.IDENTITY_DOCUMENT_DNI
            number == clientDeleted.dni
        }
    }

    def "it is possible to register with the bank account of a soft deleted client"() {
        given:
        fintech.spain.alfa.product.testing.TestClient clientDeleted = fintech.spain.alfa.product.testing.TestFactory.newClient()
            .registerDirectly()
            .submitApplicationAndStartFirstLoanWorkflow(amount(300), 30L, TimeMachine.today())
        clientDeleteService.softDelete(clientDeleted.clientId)

        when:
        fintech.spain.alfa.product.testing.TestClient client = fintech.spain.alfa.product.testing.TestFactory.newClient()
            .setIban(clientDeleted.iban)
            .registerDirectly()
            .submitApplicationAndStartFirstLoanWorkflow(amount(300), 30L, TimeMachine.today())

        then:
        noExceptionThrown()
        clientRepository.count() == 2

        and:
        def accounts = clientBankAccountService.findAllByClientId(client.clientId)
        accounts.size() == 1
        with(accounts[0]) {
            accountNumber == clientDeleted.iban.toString()
            primary
        }
    }

    def "it is not possible to soft delete client with open loan"() {
        given:
        fintech.spain.alfa.product.testing.TestClient client = fintech.spain.alfa.product.testing.TestFactory.newClient()
            .registerDirectly()
            .issueActiveLoan(100.0, 30, TimeMachine.today())
            .toClient()

        when:
        clientDeleteService.softDelete(client.clientId)

        then:
        def e = thrown(IllegalArgumentException)
        e.message == "Cannot soft delete client with open loans"
    }

    def "it is possible to soft delete client with paid loan"() {
        given:
        fintech.spain.alfa.product.testing.TestClient client = fintech.spain.alfa.product.testing.TestFactory.newClient()
            .registerDirectly()
            .issueActiveLoan(100.0, 30, TimeMachine.today())
            .repayAll(TimeMachine.today())
            .toClient()

        when:
        clientDeleteService.softDelete(client.clientId)

        then:
        noExceptionThrown()
    }

    def "hard delete is deleting data"() {
        given:
        fintech.spain.alfa.product.testing.TestClient client = fintech.spain.alfa.product.testing.TestFactory.newClient()
            .registerDirectly()
            .submitApplicationAndStartFirstLoanWorkflow(amount(300), 30L, TimeMachine.today())

        when:
        clientDeleteService.hardDelete(client.clientId)

        then: "client data"
        clientRepository.count() == 0

        and: "email contacts"
        def emailContacts = emailContactService.findAllEmailContacts(client.clientId)
        emailContacts.size() == 0

        and: "phone contacts"
        def phoneContacts = phoneContactService.findClientPhoneContacts(client.clientId)
        phoneContacts.size() == 0

        and: "email for login"
        !emailLoginService.findByClientId(client.clientId).isPresent()

        and: "identity documents"
        def documents = identityDocumentService.findPrimaryDocuments(client.clientId)
        documents.size() == 0

        and: "bank account"
        def accounts = clientBankAccountService.findAllByClientId(client.clientId)
        accounts.size() == 0

        and: "active workflows"
        def workflows = workflowService.findWorkflows(WorkflowQuery.byClientId(client.clientId))
        workflows.size() == 0
    }

    def "it is possible to register with the email of a hard deleted client"() {
        given:
        fintech.spain.alfa.product.testing.TestClient clientDeleted = fintech.spain.alfa.product.testing.TestFactory.newClient()
            .registerDirectly()
            .submitApplicationAndStartFirstLoanWorkflow(amount(300), 30L, TimeMachine.today())
        clientDeleteService.hardDelete(clientDeleted.clientId)

        when:
        fintech.spain.alfa.product.testing.TestClient client = fintech.spain.alfa.product.testing.TestFactory.newClient()
            .setEmail(clientDeleted.signUpForm.email)
            .registerDirectly()
            .submitApplicationAndStartFirstLoanWorkflow(amount(300), 30L, TimeMachine.today())

        then:
        noExceptionThrown()
        clientRepository.count() == 1

        and:
        def emailContacts = emailContactService.findAllEmailContacts(client.clientId)
        emailContacts.size() == 1
        with(emailContacts[0]) {
            email == clientDeleted.signUpForm.email
            primary
        }

        and: "email for login"
        def email = emailLoginService.findByClientId(client.clientId)
        email.isPresent()
        email.get().email == clientDeleted.signUpForm.email.toLowerCase()
    }

    def "it is possible to register with the phone of a hard deleted client"() {
        given:
        fintech.spain.alfa.product.testing.TestClient clientDeleted = fintech.spain.alfa.product.testing.TestFactory.newClient()
            .registerDirectly()
            .submitApplicationAndStartFirstLoanWorkflow(amount(300), 30L, TimeMachine.today())
        clientDeleteService.hardDelete(clientDeleted.clientId)

        when:
        fintech.spain.alfa.product.testing.TestClient client = fintech.spain.alfa.product.testing.TestFactory.newClient()
            .setMobilePhone(clientDeleted.signUpForm.mobilePhone)
            .registerDirectly()
            .submitApplicationAndStartFirstLoanWorkflow(amount(300), 30L, TimeMachine.today())

        then:
        noExceptionThrown()
        clientRepository.count() == 1

        and:
        def phoneContacts = phoneContactService.findClientPhoneContacts(client.clientId)
        phoneContacts.size() == 1
        with(phoneContacts.find({ p -> p.phoneType == PhoneType.MOBILE })) {
            localNumber == clientDeleted.signUpForm.mobilePhone
            primary
        }
    }

    def "it is possible to register with the identity document of a hard deleted client"() {
        given:
        fintech.spain.alfa.product.testing.TestClient clientDeleted = fintech.spain.alfa.product.testing.TestFactory.newClient()
            .registerDirectly()
            .submitApplicationAndStartFirstLoanWorkflow(amount(300), 30L, TimeMachine.today())
        clientDeleteService.hardDelete(clientDeleted.clientId)

        when:
        fintech.spain.alfa.product.testing.TestClient client = fintech.spain.alfa.product.testing.TestFactory.newClient()
            .setDni(clientDeleted.dni)
            .registerDirectly()
            .submitApplicationAndStartFirstLoanWorkflow(amount(300), 30L, TimeMachine.today())

        then:
        noExceptionThrown()
        clientRepository.count() == 1

        and:
        def documents = identityDocumentService.findPrimaryDocuments(client.clientId)
        documents.size() == 1
        with(documents[0]) {
            type == CrmConstants.IDENTITY_DOCUMENT_DNI
            number == clientDeleted.dni
        }
    }

    def "it is possible to register with the bank account of a hard deleted client"() {
        given:
        fintech.spain.alfa.product.testing.TestClient clientDeleted = fintech.spain.alfa.product.testing.TestFactory.newClient()
            .registerDirectly()
            .submitApplicationAndStartFirstLoanWorkflow(amount(300), 30L, TimeMachine.today())
        clientDeleteService.hardDelete(clientDeleted.clientId)

        when:
        fintech.spain.alfa.product.testing.TestClient client = fintech.spain.alfa.product.testing.TestFactory.newClient()
            .setIban(clientDeleted.iban)
            .registerDirectly()
            .submitApplicationAndStartFirstLoanWorkflow(amount(300), 30L, TimeMachine.today())

        then:
        noExceptionThrown()
        clientRepository.count() == 1

        and:
        def accounts = clientBankAccountService.findAllByClientId(client.clientId)
        accounts.size() == 1
        with(accounts[0]) {
            accountNumber == clientDeleted.iban.toString()
            primary
        }
    }
}
