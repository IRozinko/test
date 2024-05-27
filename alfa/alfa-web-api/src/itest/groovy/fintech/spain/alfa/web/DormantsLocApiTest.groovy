package fintech.spain.alfa.web

import fintech.TimeMachine
import fintech.crm.client.model.DormantsCompleteWFRequest
import fintech.spain.alfa.product.testing.TestFactory
import fintech.spain.alfa.product.testing.settings.LocTestSettings
import fintech.spain.alfa.product.workflow.dormants.event.LocClientRedirectionCompleted
import fintech.testing.integration.TestingEventConsumer
import fintech.web.api.models.OkResponse
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpMethod
import org.springframework.http.HttpStatus

import java.time.LocalDateTime

import static fintech.BigDecimalUtils.amount
import static fintech.spain.alfa.product.workflow.dormants.LocInstantorWorkflows.Activities.LocLoanOffer
import static fintech.spain.alfa.product.workflow.dormants.LocInstantorWorkflows.Activities.LocSendToPrestoRedirect

class DormantsLocApiTest extends AbstractAlfaApiTest {

    @Autowired
    fintech.spain.alfa.web.config.security.InternalAuthenticationProvider internalAuthenticationProvider

    @Autowired
    TestingEventConsumer eventConsumer

    @Autowired
    LocTestSettings locTestSettings

    @Override
    def setup() {
        locTestSettings.setUp()
    }

    def "Getting expected offer parameters for client with LOC workflow"() {
        given:
        def client = TestFactory.newClient()
            .setDateOfBirth(TimeMachine.today().minusYears(36))
            .setAmount(1000.00)
            .signUp()
            .toLoanWorkflow().runAll()
            .exportDisbursement()
            .toLoan()
            .repayAll(TimeMachine.today())
            .toClient()


        when: "past 60+ days"
        TimeMachine.useFixedClockAt(LocalDateTime.now().plusDays(61))

        and: "client chosen for Dormants WF"
        client.submitLineOfCreditAndStartWorkflow(2000.00, TimeMachine.now()).toDormantsQualifyLocWorkflow().runAll()
        def workflow = client.toDormantsInstantorLocWorkflow()
        workflow.runAfterActivity(LocLoanOffer)

        def token = apiHelper.login(client)
        and:
        def result = restTemplate.exchange("/api/web/wf/dormants-loc/offer", HttpMethod.GET, ApiHelper.authorized(token, ""), fintech.spain.alfa.web.models.DormantsLocOffer.class)

        then:
        result.statusCode == HttpStatus.OK
        result.body.applicationId == client.toApplication().application.id
        result.body.loanAgreementAttachment
        result.body.standardInformationAttachment
        result.body.creditLimit == client.toDormantsInstantorLocWorkflow().toApplication().application.creditLimit
        !result.body.payments.isEmpty()
    }

    def "Complete LoC WF on request"() {
        given:
        internalAuthenticationProvider.setInternalApiKey("pass123")
        def client = TestFactory.newClient()
            .registerDirectly()
            .submitLineOfCreditAndStartWorkflow(amount(1000.00), TimeMachine.now())
            .toDormantsQualifyLocWorkflow()
            .runBeforeActivity(LocSendToPrestoRedirect)
            .toClient()

        when:
        def result = restTemplate.postForEntity("/api/internal/wf/dormants-loc/complete",
            ApiHelper.authorized("pass123", new DormantsCompleteWFRequest(client.clientId)),
            OkResponse.class)

        then:
        result.statusCodeValue == 200
        eventConsumer.countOf(LocClientRedirectionCompleted.class) > 0
        client.client.transferredToLoc

        when:
        def token = apiHelper.login(client)
        def clientInfo = restTemplate.exchange("/api/public/web/client", HttpMethod.GET, ApiHelper.authorized(token, ""), fintech.spain.alfa.web.models.ClientInfoResponse.class).body

        then:
        clientInfo.transferredToLoc
    }
}
