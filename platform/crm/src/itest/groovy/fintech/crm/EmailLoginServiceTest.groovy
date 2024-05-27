package fintech.crm


import fintech.crm.logins.AddEmailLoginCommand
import fintech.crm.logins.ChangeEmailCommand
import fintech.crm.logins.ChangePasswordCommand
import fintech.crm.logins.CurrentPasswordMatchException
import fintech.crm.logins.DuplicateEmailLoginException
import fintech.crm.logins.EmailLoginService
import fintech.crm.logins.PasswordHash
import org.springframework.beans.factory.annotation.Autowired

class EmailLoginServiceTest extends BaseSpecification {

    @Autowired
    private EmailLoginService emailLoginService

    Long clientId

    def setup() {
        clientId = createClient()
    }

    def "Can add email login and find client by email"() {
        when:
        emailLoginService.add(new AddEmailLoginCommand(clientId, "test@mail.com", "password1", false))

        then:
        emailLoginService.findByEmail("test@mail.com").get().clientId == clientId

        and: "can find client ignoring case of email"
        emailLoginService.findByEmail(" TEST@mail.com ").get().clientId == clientId

        and: "client not find with another email"
        !emailLoginService.findByEmail("test2@mail.com").isPresent()
    }

    def "Password is hashed"() {
        given:
        emailLoginService.add(new AddEmailLoginCommand(clientId, "test@mail.com", "password1", false))

        when:
        def login = emailLoginService.findByEmail("test@mail.com").get()

        then:
        login.password != "password1";
        login.password.length() == PasswordHash.createHash("password1").length()
    }

    def "Can't add email login with empty email or empty password"() {
        when:
        emailLoginService.add(new AddEmailLoginCommand(clientId, email, password, false))

        then:
        IllegalArgumentException ex = thrown()
        ex.message == message

        where:
        email           | password    || message
        null            | 'password1' || 'Empty email'
        ''              | 'password1' || 'Empty email'
        'test@mail.com' | null        || 'Empty password'
        'test@mail.com' | ''          || 'Empty password'
    }

    def "Can't add email login with email for which email login already exists"() {
        given:
        emailLoginService.add(new AddEmailLoginCommand(clientId, "test@mail.com", "password1", false))

        when:
        emailLoginService.add(new AddEmailLoginCommand(clientId, "test@mail.com", "password2", false))

        then:
        DuplicateEmailLoginException ex = thrown()
        ex.message == 'Email login already in use: test@mail.com'

        when: "another user tries to add email login with existing email"
        def anotherClientId = createClient()
        emailLoginService.add(new AddEmailLoginCommand(anotherClientId, "test@mail.com", "password2", false))

        then:
        ex = thrown()
        ex.message == 'Email login already in use: test@mail.com'
    }

    def "Email gets changed for client"() {
        given:
        def currentEmail = "test@mail.com"
        def newEmail = "test_1@mail.com"

        when:
        emailLoginService.add(new AddEmailLoginCommand(clientId, currentEmail, "password1", false))

        then:
        emailLoginService.findByEmail(currentEmail).isPresent()

        when: "client changes email"
        emailLoginService.changeEmail(new ChangeEmailCommand(clientId: clientId, newEmail: newEmail, currentEmail: currentEmail))

        then:
        emailLoginService.findByEmail(newEmail).isPresent()
        !emailLoginService.findByEmail(currentEmail).isPresent()
        emailLoginService.findByClientId(clientId).get().email == "test_1@mail.com"
    }

    def "Can't change email which already exists"() {
        given:
        def anotherClientId = createClient()
        emailLoginService.add(new AddEmailLoginCommand(clientId, "test_1@mail.com", "password1", false))
        emailLoginService.add(new AddEmailLoginCommand(anotherClientId, "test_2@mail.com", "password2", false))

        when:
        emailLoginService.changeEmail(new ChangeEmailCommand(clientId: clientId, currentEmail: "test_1@mail.com", newEmail: "test_2@mail.com"))

        then:
        DuplicateEmailLoginException ex = thrown()
        ex.message == "Email login already in use: test_2@mail.com"
    }

    def "Password gets changed for user"() {
        given:
        def newPassword = "password1234"
        emailLoginService.add(new AddEmailLoginCommand(clientId, "test@mail.com", "password1", false))

        when:
        emailLoginService.changePassword(new ChangePasswordCommand(clientId: clientId, currentPassword: 'password1', newPassword: newPassword, email: "test@mail.com"))

        then:
        def passwordHash = emailLoginService.findByEmail("test@mail.com").get().password
        PasswordHash.verifyPassword(newPassword, passwordHash)
    }

    def "Exception gets thrown if current password doesn't match"() {
        given:
        emailLoginService.add(new AddEmailLoginCommand(clientId, "test@mail.com", "password1", false))

        when:
        def wrongCurrentPassword = "password111"
        emailLoginService.changePassword(new ChangePasswordCommand(clientId: clientId, currentPassword: wrongCurrentPassword, newPassword: "password1234", email: "test@mail.com"))

        then:
        CurrentPasswordMatchException ex = thrown()
        ex.message == "Current password doesn't match for user: test@mail.com"
    }

    def "Temporary password"() {
        when:
        emailLoginService.add(new AddEmailLoginCommand(clientId, "test@mail.com", "whatever", true))

        then:
        emailLoginService.findByEmail("test@mail.com").get().temporaryPassword

        when:
        emailLoginService.changePassword(new ChangePasswordCommand(clientId: clientId, currentPassword: 'not important', newPassword: 'new password', email: "test@mail.com"))

        then:
        !emailLoginService.findByEmail("test@mail.com").get().temporaryPassword

        and:
        def passwordHash = emailLoginService.findByEmail("test@mail.com").get().password
        PasswordHash.verifyPassword('new password', passwordHash)
    }

    def "Don't throw error changing email that doesn't exist"() {
        when:
        emailLoginService.changeEmail(new ChangeEmailCommand(clientId: clientId, currentEmail: "test_1@mail.com", newEmail: "test_2@mail.com"))

        then:
        noExceptionThrown()
    }
}
