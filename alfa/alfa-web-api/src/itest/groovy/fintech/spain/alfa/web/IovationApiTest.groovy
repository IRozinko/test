package fintech.spain.alfa.web

import fintech.iovation.db.IovationBlackBoxRepository
import fintech.spain.alfa.product.testing.TestFactory
import fintech.web.api.models.OkResponse
import org.springframework.beans.factory.annotation.Autowired

class IovationApiTest extends AbstractAlfaApiTest {

    @Autowired
    IovationBlackBoxRepository repository

    def "Save blackbox"() {
        given:
        def client = TestFactory.newClient().registerDirectly()

        when:
        def token = apiHelper.login(client)

        then:
        with(repository.findAll()[0]) {
            clientId == client.clientId
            blackBox == client.blackbox
        }

        when:
        def result = restTemplate.postForEntity("/api/web/iovation/save-blackbox", ApiHelper.authorized(token, new fintech.spain.alfa.web.models.SaveIovationBlackboxRequest(blackBox: "123456")), OkResponse.class)

        then:
        result.statusCodeValue == 200
        with(repository.findAll().find { it.blackBox == "123456" }) {
            it.clientId == client.clientId
        }
    }

    def "Save blackbox with application"() {
        given:
        def client = TestFactory.newClient().signUpWithApplication()

        when:
        def token = apiHelper.login(client)

        then:
        with(repository.findAll()[0]) {
            clientId == client.clientId
            blackBox == client.blackbox
        }

        when:
        def result = restTemplate.postForEntity("/api/web/iovation/save-blackbox", ApiHelper.authorized(token, new fintech.spain.alfa.web.models.SaveIovationBlackboxRequest(blackBox: "123456")), OkResponse.class)

        then:
        result.statusCodeValue == 200
        with(repository.findAll().find { it.blackBox == "123456" }) {
            it.clientId == client.clientId
            it.loanApplicationId == client.applicationId
        }
    }
}
