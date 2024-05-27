package fintech.spain.alfa.product

import fintech.crm.CrmConstants
import fintech.crm.attachments.ClientAttachmentService
import fintech.crm.contacts.DuplicatePrimaryEmailException
import fintech.crm.contacts.DuplicatePrimaryPhoneException
import fintech.crm.country.impl.CountryNotValidException
import fintech.crm.documents.IdentityDocumentService
import org.springframework.beans.factory.annotation.Autowired

class RegistrationTest extends AbstractAlfaTest {

    @Autowired
    IdentityDocumentService identityDocumentService

    def "sign up"() {
        when:
        def client = fintech.spain.alfa.product.testing.TestFactory.newClient().signUp()

        then:
        with(client.getClient()) {
            firstName == client.signUpForm.firstName
            lastName == client.signUpForm.lastName
            phone == client.signUpForm.mobilePhone
            email == client.signUpForm.email
            documentNumber == client.signUpForm.documentNumber
        }

        and:
        def document = identityDocumentService.findPrimaryDocument(client.clientId, CrmConstants.IDENTITY_DOCUMENT_DNI)

        document.isPresent()
        document.get().number == client.signUpForm.documentNumber
        document.get().nationality.code == client.signUpForm.countryCodeOfNationality
    }

    def "fail to sign up with duplicate mobile phone"() {
        given:
        def clientA = fintech.spain.alfa.product.testing.TestFactory.newClient().signUp()
        def clientB = fintech.spain.alfa.product.testing.TestFactory.newClient().setMobilePhone(clientA.getMobilePhone())

        when:
        clientB.signUp()

        then:
        thrown(DuplicatePrimaryPhoneException.class)
    }

    def "fail to sign up duplicate email"() {
        given:
        def clientA = fintech.spain.alfa.product.testing.TestFactory.newClient().signUp()
        def clientB = fintech.spain.alfa.product.testing.TestFactory.newClient().setEmail(clientA.getEmail())

        when:
        clientB.signUp()

        then:
        thrown(DuplicatePrimaryEmailException.class)
    }

    def "first loan workflow is started"() {
        expect:
        assert fintech.spain.alfa.product.testing.TestFactory.newClient().signUp().toLoanWorkflow().isActive()
    }

    def "Privacy policy pdf is stored"() {
        when:
        def client = fintech.spain.alfa.product.testing.TestFactory.newClient().signUp()

        then:
        clientAttachmentService.findAttachments(ClientAttachmentService.AttachmentQuery.byClient(client.clientId, AlfaConstants.ATTACHMENT_TYPE_PRIVACY_POLICY)).size() == 1
    }

    def "fail to sign up when document number is not valid"() {
        given:
        def client = fintech.spain.alfa.product.testing.TestFactory.newClient()

        when:
        client.dni = "XXXXXXXX"
        client.signUp()

        then:
        thrown(IllegalArgumentException.class)
    }

    def "fail to sign up when nationality is not valid"() {
        given:
        def client = fintech.spain.alfa.product.testing.TestFactory.newClient()

        when:
        client.countryCodeOfNationality = "XX"
        client.signUp()

        then:
        thrown(CountryNotValidException.class)
    }

    def "fail to sign up when nationality is spain but document number is nie"() {
        given:
        def client = fintech.spain.alfa.product.testing.TestFactory.newClient()

        when:
        client.countryCodeOfNationality = "ES"
        client.dni = "X0252673H"
        client.signUp()

        then:
        thrown(IllegalArgumentException.class)
    }

    def "fail to sign up when nationality is not spain but document number is dni"() {
        given:
        def client = fintech.spain.alfa.product.testing.TestFactory.newClient()

        when:
        client.countryCodeOfNationality = "IT"
        client.dni = "96603776G"
        client.signUp()

        then:
        thrown(IllegalArgumentException.class)
    }
}
