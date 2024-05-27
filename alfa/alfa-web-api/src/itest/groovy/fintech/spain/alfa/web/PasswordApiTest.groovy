package fintech.spain.alfa.web

import fintech.crm.logins.ResetPasswordService
import fintech.notification.NotificationHelper
import fintech.spain.alfa.product.cms.CmsSetup
import fintech.spain.alfa.product.testing.TestClient
import fintech.spain.alfa.product.testing.TestFactory
import fintech.spain.web.common.ApiError
import fintech.web.api.models.OkResponse
import org.springframework.beans.factory.annotation.Autowired

class PasswordApiTest extends AbstractAlfaApiTest {

    @Autowired
    ResetPasswordService resetPasswordService

    @Autowired
    NotificationHelper notificationHelper

    def "Reset password"() {
        given:
        def client = TestFactory.newClient().registerDirectly()

        when:
        def result = restTemplate.postForEntity("/api/public/web/forgot-password", new fintech.spain.alfa.web.models.ForgotPasswordRequest(email: client.email), OkResponse.class)

        then:
        result.statusCodeValue == 200

        and: "Email is sent"
        isEmailSent(client, CmsSetup.RESET_PASSWORD_NOTIFICATION)

        when:
        def token = resetPasswordService.findTokensByClient(client.clientId)[0]
        def newPassword = client.signUpForm.password + "!"
        result = restTemplate.postForEntity("/api/public/web/reset-password", new fintech.spain.alfa.web.models.ResetPasswordRequest(token: token, password: newPassword, repeatedPassword: newPassword), OkResponse.class)

        then:
        result.statusCodeValue == 200

        when: "Login works with new password"
        result = restTemplate.postForEntity("/api/public/web/login", new fintech.spain.alfa.web.models.LoginRequest(email: client.signUpForm.email, password: newPassword), fintech.spain.alfa.web.models.LoginResponse.class)

        then:
        result.statusCodeValue == 200

        when: "Login fails with old password"
        result = restTemplate.postForEntity("/api/public/web/login", new fintech.spain.alfa.web.models.LoginRequest(email: client.signUpForm.email, password: client.signUpForm.password), fintech.spain.alfa.web.models.LoginResponse.class)

        then:
        result.statusCodeValue == 403
    }

    def "Reset password doesn't fail when client is soft deleted"() {
        given:
        def client = TestFactory.newClient().registerDirectly().softDelete()

        when:
        def result = restTemplate.postForEntity("/api/public/web/forgot-password", new fintech.spain.alfa.web.models.ForgotPasswordRequest(email: client.email), OkResponse.class)

        then:
        result.statusCodeValue == 200
    }

    def "Reset password doesn't fail when client is hard deleted"() {
        given:
        def client = TestFactory.newClient().registerDirectly().hardDelete()

        when:
        def result = restTemplate.postForEntity("/api/public/web/forgot-password", new fintech.spain.alfa.web.models.ForgotPasswordRequest(email: client.email), OkResponse.class)

        then:
        result.statusCodeValue == 200
    }

    def "Reset password fails when client has been soft deleted"() {
        given:
        def client = TestFactory.newClient().registerDirectly()

        when:
        def result = restTemplate.postForEntity("/api/public/web/forgot-password", new fintech.spain.alfa.web.models.ForgotPasswordRequest(email: client.email), OkResponse.class)

        then:
        result.statusCodeValue == 200

        and: "Email is sent"
        isEmailSent(client, CmsSetup.RESET_PASSWORD_NOTIFICATION)

        when:
        def token = resetPasswordService.findTokensByClient(client.clientId)[0]
        def newPassword = client.signUpForm.password + "!"
        client.softDelete()
        result = restTemplate.postForEntity("/api/public/web/reset-password", new fintech.spain.alfa.web.models.ResetPasswordRequest(token: token, password: newPassword, repeatedPassword: newPassword), OkResponse.class)

        then:
        result.statusCodeValue == 400
    }

    def "Reset password fails when client has been hard deleted"() {
        given:
        def client = TestFactory.newClient().registerDirectly()

        when:
        def result = restTemplate.postForEntity("/api/public/web/forgot-password", new fintech.spain.alfa.web.models.ForgotPasswordRequest(email: client.email), OkResponse.class)

        then:
        result.statusCodeValue == 200

        and: "Email is sent"
        isEmailSent(client, CmsSetup.RESET_PASSWORD_NOTIFICATION)

        when:
        def token = resetPasswordService.findTokensByClient(client.clientId)[0]
        def newPassword = client.signUpForm.password + "!"
        client.hardDelete()
        result = restTemplate.postForEntity("/api/public/web/reset-password", new fintech.spain.alfa.web.models.ResetPasswordRequest(token: token, password: newPassword, repeatedPassword: newPassword), OkResponse.class)

        then:
        result.statusCodeValue == 400
    }

    def "Change password - Client is able to login with new password"() {
        given:
        def client = TestFactory.newClient().registerDirectly()
        def token = apiHelper.login(client)
        def newPassword = "12qwaszx"
        def oldPassword = client.signUpForm.password

        when:
        def result = restTemplate.postForEntity("/api/web/profile/change-password", ApiHelper.authorized(token, new fintech.spain.alfa.web.models.ChangePasswordRequest(currentPassword: oldPassword, newPassword: newPassword)), OkResponse.class)

        then:
        result.statusCodeValue == 200

        when: "Can login with new password"
        client.signUpForm.password = newPassword

        then:
        apiHelper.login(client)
    }

    def "Change password - Client is no able to login with old password"() {
        given:
        def client = TestFactory.newClient().registerDirectly()
        def token = apiHelper.login(client)
        def newPassword = "12qwaszx"
        def oldPassword = client.signUpForm.password

        when:
        def result = restTemplate.postForEntity("/api/web/profile/change-password", ApiHelper.authorized(token, new fintech.spain.alfa.web.models.ChangePasswordRequest(currentPassword: oldPassword, newPassword: newPassword)), OkResponse.class)

        then:
        result.statusCodeValue == 200

        when: "Can not login with old password"
        token = apiHelper.login(client, 403)

        then:
        token == null
    }

    def "Change password - Client provided invalid current password"() {
        given:
        def client = TestFactory.newClient().registerDirectly()
        def token = apiHelper.login(client)
        def newPassword = "12qwaszx"
        def oldPassword = "invalid"

        when:
        def result = restTemplate.postForEntity("/api/web/profile/change-password", ApiHelper.authorized(token, new fintech.spain.alfa.web.models.ChangePasswordRequest(currentPassword: oldPassword, newPassword: newPassword)), ApiError.class)

        then:
        result.statusCodeValue == 400
        result.body.fieldErrors.containsKey("currentPassword")
    }

    def "Change password - Client with temporary password passed validation"() {
        given:
        def client = TestFactory.newClient().registerDirectly()
        def token = apiHelper.login(client)
        def newPassword = "12qwaszx"
        def oldPassword = "Does not matter"
        client.setPasswordTemporary()

        when:
        def result = restTemplate.postForEntity("/api/web/profile/change-password", ApiHelper.authorized(token, new fintech.spain.alfa.web.models.ChangePasswordRequest(currentPassword: oldPassword, newPassword: newPassword)), OkResponse.class)

        then:
        result.statusCodeValue == 200

        when: "Can login with new password"
        client.signUpForm.password = newPassword

        then:
        apiHelper.login(client)
    }

    def "Change password fails when client has been soft deleted"() {
        given:
        def client = TestFactory.newClient().registerDirectly()
        def token = apiHelper.login(client)
        client.softDelete()
        def newPassword = "12qwaszx"
        def oldPassword = client.signUpForm.password

        when:
        def result = restTemplate.postForEntity("/api/web/profile/change-password", ApiHelper.authorized(token, new fintech.spain.alfa.web.models.ChangePasswordRequest(currentPassword: oldPassword, newPassword: newPassword)), OkResponse.class)

        then:
        result.statusCodeValue == 403
    }

    def "Change password fails when client has been hard deleted"() {
        given:
        def client = TestFactory.newClient().registerDirectly()
        def token = apiHelper.login(client)
        client.hardDelete()
        def newPassword = "12qwaszx"
        def oldPassword = client.signUpForm.password

        when:
        def result = restTemplate.postForEntity("/api/web/profile/change-password", ApiHelper.authorized(token, new fintech.spain.alfa.web.models.ChangePasswordRequest(currentPassword: oldPassword, newPassword: newPassword)), OkResponse.class)

        then:
        result.statusCodeValue == 403
    }

    private boolean isEmailSent(TestClient client, String cmsKey) {
        return notificationHelper.countEmails(client.clientId, cmsKey) > 0
    }
}
