package fintech.spain.alfa.web

import fintech.risk.checklist.CheckListConstants
import fintech.risk.checklist.CheckListService
import fintech.risk.checklist.commands.AddCheckListEntryCommand
import fintech.spain.alfa.product.crosscheck.CrosscheckRequest
import fintech.spain.alfa.product.crosscheck.CrosscheckResult
import fintech.spain.alfa.product.testing.TestFactory
import org.springframework.beans.factory.annotation.Autowired

import static fintech.DateUtils.date

class CrosscheckApiTest extends AbstractAlfaApiTest {

    @Autowired
    fintech.spain.alfa.web.config.security.InternalAuthenticationProvider internalAuthenticationProvider

    @Autowired
    CheckListService checkListService

    def setup() {
        internalAuthenticationProvider.setInternalApiKey(null)
    }

    def cleanup() {
        internalAuthenticationProvider.setInternalApiKey(null)
    }

    def "not enabled by default"() {
        given:
        def crosscheckRequest = new CrosscheckRequest().setDni("12345").setEmail("test@mial.com").setPhone("3333333")

        when:
        def result = restTemplate.postForEntity("/api/internal/crosscheck/client", crosscheckRequest, Object.class)

        then:
        assert result.statusCodeValue == 403
    }

    def "client not found by dni"() {
        given:
        internalAuthenticationProvider.setInternalApiKey("pass123")
        def crosscheckRequest = new CrosscheckRequest().setDni("12345").setEmail("test@mial.com").setPhone("3333333")

        when:
        def result = restTemplate.postForEntity("/api/internal/crosscheck/client", ApiHelper.authorized("pass123", crosscheckRequest), CrosscheckResult.class)

        then:
        assert result.statusCodeValue == 200
        with(result.body) {
            assert !found
            assert maxDpd == 0
            assert !blacklisted
            assert openLoans == 0
        }
    }

    def "Client found and is blacklisted by dni"() {
        given:
        internalAuthenticationProvider.setInternalApiKey("pass123")

        and:
        def client = TestFactory.newClient()
            .registerDirectly()
            .issueActiveLoan(300.0, 30, date("2018-01-01"))
            .repayAll(date("2018-03-01"))
            .updateDerivedValues(date("2018-03-01"))
            .toClient()
            .issueActiveLoan(300.0, 30, date("2018-03-01"))
            .toClient()
        checkListService.addEntry(new AddCheckListEntryCommand(type: CheckListConstants.CHECKLIST_TYPE_DNI, value1: client.dni))

        def crosscheckRequest = new CrosscheckRequest().setDni(client.dni).setEmail(client.getEmail()).setPhone(client.getMobilePhone())

        when:
        def result = restTemplate.postForEntity("/api/internal/crosscheck/client", ApiHelper.authorized("pass123", crosscheckRequest), CrosscheckResult.class)

        then:
        assert result.statusCodeValue == 200
        with(result.body) {
            assert found
            assert blacklisted
            assert openLoans == 1
            assert maxDpd > 0
            assert !activeRequest
            assert repeatedClient
        }
    }

    def "Client found and is blacklisted by email"() {
        given:
        internalAuthenticationProvider.setInternalApiKey("pass123")

        and:
        def client = TestFactory.newClient()
            .registerDirectly()
            .issueActiveLoan(300.0, 30, date("2018-01-01"))
            .repayAll(date("2018-03-01"))
            .updateDerivedValues(date("2018-03-01"))
            .toClient()
            .issueActiveLoan(300.0, 30, date("2018-03-01"))
            .toClient()
        checkListService.addEntry(new AddCheckListEntryCommand(type: CheckListConstants.CHECKLIST_TYPE_EMAIL, value1: client.email))

        def crosscheckRequest = new CrosscheckRequest().setDni(client.dni).setEmail(client.getEmail()).setPhone(client.getMobilePhone())

        when:
        def result = restTemplate.postForEntity("/api/internal/crosscheck/client", ApiHelper.authorized("pass123", crosscheckRequest), CrosscheckResult.class)

        then:
        assert result.statusCodeValue == 200
        with(result.body) {
            assert found
            assert blacklisted
            assert openLoans == 1
            assert maxDpd > 0
            assert !activeRequest
            assert repeatedClient
        }
    }

    def "Client found and is blacklisted by phone"() {
        given:
        internalAuthenticationProvider.setInternalApiKey("pass123")

        and:
        def client = TestFactory.newClient()
            .registerDirectly()
            .issueActiveLoan(300.0, 30, date("2018-01-01"))
            .repayAll(date("2018-03-01"))
            .updateDerivedValues(date("2018-03-01"))
            .toClient()
            .issueActiveLoan(300.0, 30, date("2018-03-01"))
            .toClient()
        checkListService.addEntry(new AddCheckListEntryCommand(type: CheckListConstants.CHECKLIST_TYPE_PHONE, value1: client.mobilePhone))

        def crosscheckRequest = new CrosscheckRequest().setDni(client.dni).setEmail(client.getEmail()).setPhone(client.getMobilePhone())

        when:
        def result = restTemplate.postForEntity("/api/internal/crosscheck/client", ApiHelper.authorized("pass123", crosscheckRequest), CrosscheckResult.class)

        then:
        assert result.statusCodeValue == 200
        with(result.body) {
            assert found
            assert blacklisted
            assert openLoans == 1
            assert maxDpd > 0
            assert !activeRequest
            assert repeatedClient
        }
    }

    def "Client found and has open application"() {
        given:
        internalAuthenticationProvider.setInternalApiKey("pass123")

        and:
        def client = TestFactory.newClient()
            .registerDirectly()
            .issueActiveLoan(300.0, 30, date("2018-01-01"))
            .repayAll(date("2018-03-01"))
            .updateDerivedValues(date("2018-03-01"))
            .toClient()
            .issueActiveLoan(300.0, 30, date("2018-03-01"))
            .toClient()
            .submitApplicationAndStartFirstLoanWorkflow(300.0, 30, date("2018-03-01"))

        checkListService.addEntry(new AddCheckListEntryCommand(type: CheckListConstants.CHECKLIST_TYPE_EMAIL, value1: client.email))

        def crosscheckRequest = new CrosscheckRequest().setDni(client.dni).setEmail(client.getEmail()).setPhone(client.getMobilePhone())

        when:
        def result = restTemplate.postForEntity("/api/internal/crosscheck/client", ApiHelper.authorized("pass123", crosscheckRequest), CrosscheckResult.class)

        then:
        assert result.statusCodeValue == 200
        with(result.body) {
            assert found
            assert blacklisted
            assert openLoans == 1
            assert maxDpd > 0
            assert activeRequest
            assert activeRequestStatus == "CollectBasicInformation"
            assert repeatedClient
        }
    }
}
