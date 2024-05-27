package fintech.spain.alfa.web

import fintech.TimeMachine
import fintech.spain.alfa.product.testing.TestFactory
import fintech.spain.alfa.product.web.WebAuthorities
import fintech.spain.alfa.product.web.model.PopupType
import fintech.spain.alfa.product.workflow.undewrtiting.UnderwritingWorkflows
import org.springframework.http.HttpMethod
import spock.lang.Ignore

import java.time.LocalDate

class ClientInfoApiTest extends AbstractAlfaApiTest {

    def "Client info - cannot be get for unauthorized user "() {
        given:
        def token = "invalid"

        when:
        def result = restTemplate.exchange("/api/public/web/client", HttpMethod.GET, ApiHelper.authorized(token, ""), fintech.spain.alfa.web.models.ClientInfoResponse.class)

        then:
        result.statusCodeValue == 403
    }

    def "Client info - Get expected info for registered client "() {
        given:
        def client = TestFactory.newClient().registerDirectly()
        def token = apiHelper.login(client)

        when:
        def result = restTemplate.exchange("/api/public/web/client", HttpMethod.GET, ApiHelper.authorized(token, ""), fintech.spain.alfa.web.models.ClientInfoResponse.class)

        then:
        result.statusCodeValue == 200
        with(result.body) {
            assert authenticated
            id == client.clientId
            documentNumber == client.client.documentNumber
            number == client.client.number
            state == fintech.spain.alfa.web.services.navigation.UiState.PROFILE
            roles == [WebAuthorities.WEB_FULL]
            assert !temporaryPassword
            assert !application
            assert qualifiedForNewLoan
        }
    }

    def "Client info - check expected popups"() {
        given:
        TimeMachine.useFixedClockAt(LocalDate.now().minusDays(4))

        def client = TestFactory.newClient().randomEmailAndName("test").signUp()
        client.toLoanWorkflow().runAll()
        def token = apiHelper.login(client)

        when:
        def result = restTemplate.exchange("/api/public/web/client", HttpMethod.GET, ApiHelper.authorized(token, ""), fintech.spain.alfa.web.models.ClientInfoResponse.class)

        then:
        result.statusCodeValue == 200
        with(result.body.popups) {
            size() == 1
            it[0].type == PopupType.LOAN_RESOLUTION_APPROVED
        }

        when:
        TimeMachine.useFixedClockAt(LocalDate.now())
        result = restTemplate.exchange("/api/public/web/client", HttpMethod.GET, ApiHelper.authorized(token, ""), fintech.spain.alfa.web.models.ClientInfoResponse.class)

        then: "pupup should be auto exhausted"
        result.statusCodeValue == 200
        with(result.body.popups) {
            size() == 0
        }
    }

    def "Client info - Get expected state in case of issued loan"() {
        given:
        def client = TestFactory.newClient().registerDirectly()
        client.issueActiveLoan(BigDecimal.valueOf(1000), 3, LocalDate.now())
        def token = apiHelper.login(client)

        when:
        def result = restTemplate.exchange("/api/public/web/client", HttpMethod.GET, ApiHelper.authorized(token, ""), fintech.spain.alfa.web.models.ClientInfoResponse.class)

        then:
        result.statusCodeValue == 200
        with(result.body) {
            assert authenticated
            id == client.clientId
            documentNumber == client.client.documentNumber
            number == client.client.number
            state == fintech.spain.alfa.web.services.navigation.UiState.PROFILE
            assert !temporaryPassword
            application.statusDetail == "APPROVED"
            application.status == "CLOSED"
            application.type == "NEW_LOAN"
            application.requestedPrincipal == BigDecimal.valueOf(1000)
            application.offeredPrincipal == BigDecimal.valueOf(1000)
            assert !qualifiedForNewLoan
        }
    }


    def "Client info - Get expected state in case of Instantor step"() {
        given:
        def client = TestFactory.newClient()
        client.signUp()
            .toLoanWorkflow()
            .runBeforeActivity(UnderwritingWorkflows.Activities.DOCUMENT_FORM)
        def token = apiHelper.login(client)

        when:
        def result = restTemplate.exchange("/api/public/web/client", HttpMethod.GET, ApiHelper.authorized(token, ""), fintech.spain.alfa.web.models.ClientInfoResponse.class)

        then:
        result.statusCodeValue == 200
        result.body.state == fintech.spain.alfa.web.services.navigation.UiState.REGISTRATION_INSTANTOR
    }

    def "Client info - Wait 30 seconds after Instantor Manual Check for the Approve Loan Offer state before sending Underwriting in progress state"() {
        given:
        def client = TestFactory.newClient()
        client.signUp()
            .toLoanWorkflow()
            .runBeforeActivity(UnderwritingWorkflows.Activities.INSTANTOR_RULES)
        def token = apiHelper.login(client)

        when:
        def result = restTemplate.exchange("/api/public/web/client", HttpMethod.GET, ApiHelper.authorized(token, ""), fintech.spain.alfa.web.models.ClientInfoResponse.class)

        then:
        result.statusCodeValue == 200
        result.body.state == fintech.spain.alfa.web.services.navigation.UiState.REGISTRATION_PROCESSING

        when:
        client.toLoanWorkflow()
            .runBeforeActivity(UnderwritingWorkflows.Activities.CREDIT_LIMIT)
        def startedWaitingAt = client.toLoanWorkflow().getActivity(UnderwritingWorkflows.Activities.INSTANTOR_MANUAL_CHECK).getCompletedAt()
        TimeMachine.useFixedClockAt(startedWaitingAt)
        result = restTemplate.exchange("/api/public/web/client", HttpMethod.GET, ApiHelper.authorized(token, ""), fintech.spain.alfa.web.models.ClientInfoResponse.class)

        then:
        result.statusCodeValue == 200
        result.body.state == fintech.spain.alfa.web.services.navigation.UiState.REGISTRATION_PROCESSING

        when:
        TimeMachine.useFixedClockAt(startedWaitingAt.plusSeconds(30))
        result = restTemplate.exchange("/api/public/web/client", HttpMethod.GET, ApiHelper.authorized(token, ""), fintech.spain.alfa.web.models.ClientInfoResponse.class)

        then:
        result.statusCodeValue == 200
        result.body.state == fintech.spain.alfa.web.services.navigation.UiState.REGISTRATION_PROCESSING

        when:
        TimeMachine.useFixedClockAt(startedWaitingAt.plusSeconds(31))
        result = restTemplate.exchange("/api/public/web/client", HttpMethod.GET, ApiHelper.authorized(token, ""), fintech.spain.alfa.web.models.ClientInfoResponse.class)

        then:
        result.statusCodeValue == 200
        result.body.state == fintech.spain.alfa.web.services.navigation.UiState.UNDERWRITING_IN_PROGRESS
    }

    @Ignore //TWINX-2186
    def "Client info - Do not wait 30 seconds if Instantor Manual Check is active"() {
        given:
        def client = TestFactory.newClient()
        client.signUp()
            .toLoanWorkflow()
            .runBeforeActivity(UnderwritingWorkflows.Activities.INSTANTOR_RULES)
        def token = apiHelper.login(client)

        when:
        def result = restTemplate.exchange("/api/public/web/client", HttpMethod.GET, ApiHelper.authorized(token, ""), fintech.spain.alfa.web.models.ClientInfoResponse.class)

        then:
        result.statusCodeValue == 200
        result.body.state == fintech.spain.alfa.web.services.navigation.UiState.REGISTRATION_PROCESSING

        when:
        client.toLoanWorkflow()
            .runBeforeActivity(UnderwritingWorkflows.Activities.INSTANTOR_MANUAL_CHECK)
        result = restTemplate.exchange("/api/public/web/client", HttpMethod.GET, ApiHelper.authorized(token, ""), fintech.spain.alfa.web.models.ClientInfoResponse.class)

        then:
        result.statusCodeValue == 200
        result.body.state == fintech.spain.alfa.web.services.navigation.UiState.UNDERWRITING_IN_PROGRESS
    }
}
