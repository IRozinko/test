package fintech.spain.alfa.web


import fintech.spain.crm.client.ClientDeleteService
import fintech.spain.alfa.product.testing.TestFactory
import org.springframework.beans.factory.annotation.Autowired

class LoginApiTest extends AbstractAlfaApiTest {

    @Autowired
    ClientDeleteService clientDeleteService

    def "login success"() {
        given:
        def client = TestFactory.newClient().registerDirectly()

        when:
        def result = restTemplate.postForEntity("/api/public/web/login", new fintech.spain.alfa.web.models.LoginRequest(email: client.signUpForm.email.toUpperCase(), password: client.signUpForm.password), fintech.spain.alfa.web.models.LoginResponse.class)

        then:
        assert result.statusCodeValue == 200
    }

    def "login fails with wrong password"() {
        given:
        def client = TestFactory.newClient().registerDirectly()

        when:
        def result = restTemplate.postForEntity("/api/public/web/login", new fintech.spain.alfa.web.models.LoginRequest(email: client.signUpForm.email, password: client.signUpForm.password + "!"), fintech.spain.alfa.web.models.LoginResponse.class)

        then:
        assert result.statusCodeValue == 403
    }

    def "login fails with wrong email"() {
        given:
        def client = TestFactory.newClient().registerDirectly()

        when:
        def result = restTemplate.postForEntity("/api/public/web/login", new fintech.spain.alfa.web.models.LoginRequest(email: "invalid" + client.signUpForm.email, password: client.signUpForm.password), fintech.spain.alfa.web.models.LoginResponse.class)

        then:
        assert result.statusCodeValue == 403
    }

    def "login with success in case of migrated client"() {
        given:
        def password = "5EkNkt"
        def passwordHash = "95839b86f27db4c4d4f0c01dd3790725a583aadf" //real generated hash on old alfa
        def client = TestFactory.newClient().registerDirectly().updateClientHashPassword(passwordHash)

        when:
        def request = new fintech.spain.alfa.web.models.LoginRequest(email: client.signUpForm.email, password: password)
        def result = restTemplate.postForEntity("/api/public/web/login", request, fintech.spain.alfa.web.models.LoginResponse.class)

        then:
        assert result.statusCodeValue == 200
    }

    def "login fails for soft deleted client"() {
        given:
        def client = TestFactory.newClient().registerDirectly().softDelete()

        when:
        def result = restTemplate.postForEntity("/api/public/web/login", new fintech.spain.alfa.web.models.LoginRequest(email: client.signUpForm.email, password: client.signUpForm.password), fintech.spain.alfa.web.models.LoginResponse.class)

        then:
        assert result.statusCodeValue == 403
    }

    def "login fails for hard deleted client"() {
        given:
        def client = TestFactory.newClient().registerDirectly().hardDelete()

        when:
        def result = restTemplate.postForEntity("/api/public/web/login", new fintech.spain.alfa.web.models.LoginRequest(email: client.signUpForm.email, password: client.signUpForm.password), fintech.spain.alfa.web.models.LoginResponse.class)

        then:
        assert result.statusCodeValue == 403
    }
}
