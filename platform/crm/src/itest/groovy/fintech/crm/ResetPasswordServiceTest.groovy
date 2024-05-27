package fintech.crm

import fintech.crm.logins.*
import org.springframework.beans.factory.annotation.Autowired

class ResetPasswordServiceTest extends BaseSpecification {

    @Autowired
    private ResetPasswordService passwordService

    @Autowired
    private EmailLoginService emailLoginService

    Long clientId

    def setup() {
        clientId = createClient()
    }

    def "Generate token and reset password"() {
        given:
        emailLoginService.add(new AddEmailLoginCommand(clientId, "test@mail.com", "password1", false))
        def token = passwordService.generateToken(new GenerateTokenCommand(clientId, 24))

        when:
        passwordService.resetPassword(new ResetPasswordCommand(token, "newPassword1"))

        then:
        def login = emailLoginService.findByEmail("test@mail.com").get()
        PasswordHash.verifyPassword("newPassword1", login.password)
    }

    def "Failed to reset password with expired token"() {
        given:
        emailLoginService.add(new AddEmailLoginCommand(clientId, "test@mail.com", "password1", false))
        def token = passwordService.generateToken(new GenerateTokenCommand(clientId, 0))

        when:
        passwordService.resetPassword(new ResetPasswordCommand(token, "newPassword1"))

        then:
        ResetPasswordException ex = thrown()
        ex.message == "Valid reset password token not found"
    }

    def "Failed to reset password with invalid token"() {
        when:
        passwordService.resetPassword(new ResetPasswordCommand("invalidToken", "newPassword1"))

        then:
        ResetPasswordException ex = thrown()
        ex.message == "Valid reset password token not found"

    }
}
