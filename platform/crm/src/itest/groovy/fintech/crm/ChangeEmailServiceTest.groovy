package fintech.crm

import fintech.crm.contacts.AddEmailContactCommand
import fintech.crm.contacts.EmailContactService
import fintech.crm.logins.*
import fintech.crm.logins.db.VerifyEmailTokenRepository
import org.springframework.beans.factory.annotation.Autowired

class ChangeEmailServiceTest extends BaseSpecification {

    @Autowired
    private EmailLoginService emailLoginService

    @Autowired
    private EmailContactService emailContactService

    @Autowired
    private ChangeEmailService changeEmailService

    @Autowired
    VerifyEmailTokenRepository verifyEmailTokenRepository

    Long clientId1
    Long clientId2

    def setup() {
        client1()
        client2()
    }

    def "Generate and insert token"() {
        when:
        changeEmailService.generateToken(new GenerateTokenCommand(clientId1, 12))

        then:
        def entities = verifyEmailTokenRepository.findAll()
        def token = entities.get(0)
        assert entities.size() == 1
        assert token.client.id == clientId1
        assert token.token != null
        assert !token.used
    }

    def "Check if email is available"() {
        expect:
        changeEmailService.isEmailAvailable(clientId1, email) == isAvailable

        where:
        email                           || isAvailable
        "jhon_primary@mailinator.com"   || false
        "jhon_secondary@mailinator.com" || true
        "bill@mailinator.com"           || false
        "jhon.smith@mailinator.com"     || true
    }

    def "Verify token and change email"() {
        given:
        def token = changeEmailService.generateToken(new GenerateTokenCommand(clientId1, 12))
        def newEmail = "jhon_smith@mailinator.com"

        when:
        ChangeEmailCommand command = new ChangeEmailCommand()
        command.setToken(token)
        command.setNewEmail(newEmail)
        changeEmailService.verifyAndChange(command)

        then:
        def tokenEntity = verifyEmailTokenRepository.findAll().get(0)
        assert tokenEntity.used
        assert tokenEntity.client.id == clientId1
        assert emailLoginService.findByEmail(newEmail).isPresent()
        assert emailContactService.findPrimaryEmail(clientId1).isPresent()
    }

    def client1() {
        clientId1 = createClient()
        emailLoginService.add(new AddEmailLoginCommand(clientId1, "jhon_primary@mailinator.com", "Test1234", false))
        Long emailContactId = emailContactService.addEmailContact(new AddEmailContactCommand(clientId: clientId1, email: "jhon_primary@mailinator.com"))
        emailContactService.makeEmailPrimary(emailContactId)
        emailContactService.addEmailContact(new AddEmailContactCommand(clientId: clientId1, email: "jhon_secondary@mailinator.com"))
    }

    def client2() {
        clientId2 = createClient()
        emailLoginService.add(new AddEmailLoginCommand(clientId2, "bill@mailinator.com", "Test1234", false))
        Long emailContactId = emailContactService.addEmailContact(new AddEmailContactCommand(clientId: clientId2, email: "bill@mailinator.com"))
        emailContactService.makeEmailPrimary(emailContactId)
    }
}
