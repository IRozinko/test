package fintech.spain.alfa.web.navigation

import fintech.TimeMachine
import fintech.instantor.InstantorSimulation
import fintech.spain.platform.web.SpecialLinkType
import fintech.spain.platform.web.spi.SpecialLinkService
import fintech.spain.alfa.product.presto.api.MockLineOfCreditCrossApiClient

import fintech.spain.alfa.product.testing.TestFactory
import fintech.spain.alfa.product.testing.settings.LocTestSettings
import fintech.spain.alfa.product.workflow.common.Attributes

import fintech.spain.alfa.web.AbstractAlfaApiTest
import fintech.spain.alfa.web.ClientApiHelper
import fintech.workflow.impl.WorkflowBackgroundJobs
import org.springframework.beans.factory.annotation.Autowired
import spock.lang.Unroll

import java.time.LocalDateTime

import static fintech.BigDecimalUtils.amount
import static fintech.spain.platform.web.model.command.SpecialLinkQuery.byClientId

class DormantsWFNavigationTest extends AbstractAlfaApiTest {

    @Autowired
    ClientApiHelper clientApiHelper

    @Autowired
    SpecialLinkService specialLinkService

    @Autowired
    WorkflowBackgroundJobs workflowBackgroundJobs

    @Autowired
    LocTestSettings locTestSettings

    @Override
    def setup() {
        locTestSettings.setUp()
    }

    @Unroll
    def "DormantsLocInstantor #activity -> #state"() {
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
        def workflow = client.toWorkflow(LocInstantorWorkflows.DORMANTS_LOC_INSTANTOR, TestDormantsLocInstantorWorkflow.class)

        and: "client login UA"
        def webToken = clientApiHelper.getClietToken(client.clientId)

        then: "DormantsLocInstantor WF is active"
        workflow.isActive()

        when: "workflow pass till #activity"
        if (activity == LocInstantorManualCheck) {
            workflow.setInstantorResponseSupplier({
                InstantorSimulation.simulateOkResponseWithSingleAccount(client.clientId, client.dni, client.fullName() + "O", client.iban.toString(), amount(1000), amount(3500))
            })
        }
        if (activity == LocPhoneValidationCall) {
            workflow.runBeforeActivity(LocPreOfferCall)

            def link = specialLinkService.findRequiredLink(byClientId(client.getClientId(), SpecialLinkType.LOC_SPECIAL_OFFER))
            specialLinkService.activateLink(link.token)
        }
        workflow.runBeforeActivity(activity)

        then: "Client can see only #state state"
        workflow.isActivityActive(activity)
        clientApiHelper.getClientInfo(webToken).getState() == state
        clientApiHelper.getClientInfo(webToken).getData() == stateData

        where:
        activity                   | state                                  | stateData
        LocPreOfferDataPreparation | fintech.spain.alfa.web.services.navigation.UiState.PROFILE                        | [:]
        LocPreOffer                | fintech.spain.alfa.web.services.navigation.UiState.PROFILE                        | [:]
        LocPreOfferEmail           | fintech.spain.alfa.web.services.navigation.UiState.PROFILE                        | [:]
        LocPreOfferSMS             | fintech.spain.alfa.web.services.navigation.UiState.PROFILE                        | [:]
        LocPreOfferCall            | fintech.spain.alfa.web.services.navigation.UiState.PROFILE                        | [:]
        LocInstantorForm           | fintech.spain.alfa.web.services.navigation.UiState.DORMANTS_WF_INSTANTOR_FORM     | [:]
        LocInstantorCallback       | fintech.spain.alfa.web.services.navigation.UiState.DORMANTS_WF_IN_PROGRESS        | [:]
        LocInstantorReview         | fintech.spain.alfa.web.services.navigation.UiState.DORMANTS_WF_INSTANTOR_REVIEW   | [:]
        LocInstantorRules          | fintech.spain.alfa.web.services.navigation.UiState.DORMANTS_WF_IN_PROGRESS        | [:]
        LocInstantorManualCheck    | fintech.spain.alfa.web.services.navigation.UiState.DORMANTS_WF_MANUAL_CHECK       | [:]
        LocCreditLimit             | fintech.spain.alfa.web.services.navigation.UiState.DORMANTS_WF_IN_PROGRESS        | [:]
        LocCreditLimitRules        | fintech.spain.alfa.web.services.navigation.UiState.DORMANTS_WF_IN_PROGRESS        | [:]
        LocPhoneValidationCall     | fintech.spain.alfa.web.services.navigation.UiState.DORMANTS_WF_PHONE_VALIDATION   | [:]
        LocLoanOffer               | fintech.spain.alfa.web.services.navigation.UiState.DORMANTS_WF_IN_PROGRESS        | [:]
        LocLoanOfferEmail          | fintech.spain.alfa.web.services.navigation.UiState.DORMANTS_WF_IN_PROGRESS        | [:]
        LocLoanOfferSMS            | fintech.spain.alfa.web.services.navigation.UiState.DORMANTS_WF_IN_PROGRESS        | [:]
        LocApprovalCall            | fintech.spain.alfa.web.services.navigation.UiState.DORMANTS_WF_LOAN_OFFER         | [:]
        LocApproveLoanOffer        | fintech.spain.alfa.web.services.navigation.UiState.DORMANTS_WF_LOAN_OFFER         | [:]
        LocSendToPresto            | fintech.spain.alfa.web.services.navigation.UiState.DORMANTS_WF_IN_PROGRESS        | [:]
        LocSendToPrestoRedirect    | fintech.spain.alfa.web.services.navigation.UiState.DORMANTS_WF_REDIRECT_TO_PRESTO | [(Attributes.LOC_REDIRECT_LINK): MockLineOfCreditCrossApiClient.FAKE_LINK]
    }
}
