package fintech.spain.alfa.product

import fintech.TimeMachine
import fintech.instantor.InstantorService
import fintech.instantor.InstantorSimulation
import fintech.instantor.SimulateInstantorReq
import fintech.instantor.model.InstantorResponseQuery
import fintech.instantor.model.InstantorResponseStatus
import fintech.lending.core.application.LoanApplicationStatusDetail
import fintech.settings.SettingsService
import fintech.spain.alfa.product.settings.AlfaSettings
import fintech.spain.alfa.product.workflow.undewrtiting.UnderwritingTasks
import fintech.workflow.ActivityStatus
import fintech.workflow.impl.WorkflowBackgroundJobs
import org.apache.commons.lang3.StringUtils
import org.iban4j.Iban
import org.springframework.beans.factory.annotation.Autowired
import spock.lang.Unroll

import static fintech.BigDecimalUtils.amount
import static fintech.spain.alfa.product.settings.AlfaSettings.INTEGRATION_SETTINGS

class InstantorTest extends AbstractAlfaTest {

    @Autowired
    InstantorService instantorService

    @Autowired
    fintech.spain.alfa.product.instantor.InstantorFacade instantorFacade

    @Autowired
    SettingsService settingsService

    @Autowired
    WorkflowBackgroundJobs workflowBackgroundJobs

    def "instantor wait response time"() {
        given:
        int expireTime = settingsService.getJson(INTEGRATION_SETTINGS, AlfaSettings.IntegrationSettings.class).getInstantor().expiresInSeconds

        when:
        def workflow = fintech.spain.alfa.product.testing.TestFactory.newClient()
            .signUp()
            .toLoanWorkflow()
            .runBeforeActivity(fintech.spain.alfa.product.workflow.undewrtiting.UnderwritingWorkflows.Activities.INSTANTOR_CALLBACK)
        def now = TimeMachine.now()
        workflowBackgroundJobs.consumeNow()

        then:
        workflow.getActivity(fintech.spain.alfa.product.workflow.undewrtiting.UnderwritingWorkflows.Activities.INSTANTOR_CALLBACK).nextAttemptAt >= now.plusSeconds(expireTime)

        when:
        def client = workflow.toClient().getClient()
        String settingsJson = settingsService.getString(AlfaSettings.INSTANTOR_SETTINGS)
        def responseId = instantorService.saveResponse(InstantorSimulation.simulateOkResponse(
            SimulateInstantorReq.builder()
                .clientId(client.getId())
                .dni(client.getDocumentNumber())
                .name(workflow.toClient().fullName())
                .account1(client.getAccountNumber())
                .iban(fintech.spain.alfa.product.testing.RandomData.randomIban().toString())
                .averageAmountOfIncomingTransactionsMonth(amount(1000))
                .averageAmountOfOutgoingTransactionsMonth(amount(3500))
                .build(),
            settingsJson))
        instantorService.processResponse(responseId)

        then:
        workflow.getActivity(fintech.spain.alfa.product.workflow.undewrtiting.UnderwritingWorkflows.Activities.INSTANTOR_CALLBACK).status == ActivityStatus.COMPLETED
        workflow.getActivityStatus(fintech.spain.alfa.product.workflow.undewrtiting.UnderwritingWorkflows.Activities.INSTANTOR_REVIEW) == ActivityStatus.ACTIVE
    }

    def "instantor review required with more than one bank account"() {
        when:
        def workflow = fintech.spain.alfa.product.testing.TestFactory.newClient()
            .signUp()
            .toLoanWorkflow()
            .runBeforeActivity(fintech.spain.alfa.product.workflow.undewrtiting.UnderwritingWorkflows.Activities.INSTANTOR_CALLBACK)

        then:
        assert workflow.getActivityStatus(fintech.spain.alfa.product.workflow.undewrtiting.UnderwritingWorkflows.Activities.INSTANTOR_CALLBACK) == ActivityStatus.ACTIVE
        assert workflow.getActivityStatus(fintech.spain.alfa.product.workflow.undewrtiting.UnderwritingWorkflows.Activities.INSTANTOR_REVIEW) == ActivityStatus.WAITING

        when:
        def client = workflow.toClient().getClient()
        String settingsJson = settingsService.getString(AlfaSettings.INSTANTOR_SETTINGS)
        def responseId = instantorService.saveResponse(InstantorSimulation.simulateOkResponse(
            SimulateInstantorReq.builder()
                .clientId(client.getId())
                .dni(client.getDocumentNumber())
                .name(workflow.toClient().fullName())
                .account1(client.getAccountNumber())
                .iban(fintech.spain.alfa.product.testing.RandomData.randomIban().toString())
                .averageAmountOfIncomingTransactionsMonth(amount(1000))
                .averageAmountOfOutgoingTransactionsMonth(amount(3500))
                .build(),
            settingsJson))
        instantorService.processResponse(responseId)

        then:
        assert workflow.getActivityStatus(fintech.spain.alfa.product.workflow.undewrtiting.UnderwritingWorkflows.Activities.INSTANTOR_REVIEW) == ActivityStatus.ACTIVE

        and:
        with(instantorFacade.get(client.getId())) {
            assert dniMatch
        }
    }

    def "instantor review required if DNI no match"() {
        when:
        def workflow = fintech.spain.alfa.product.testing.TestFactory.newClient()
            .signUp()
            .addPrimaryBankAccount()
            .toLoanWorkflow()
            .runBeforeActivity(fintech.spain.alfa.product.workflow.undewrtiting.UnderwritingWorkflows.Activities.INSTANTOR_CALLBACK)
        def client = workflow.toClient().getClient()
        String settingsJson = settingsService.getString(AlfaSettings.INSTANTOR_SETTINGS)
        def responseId = instantorService.saveResponse(InstantorSimulation.simulateOkResponse(
            SimulateInstantorReq.builder()
                .clientId(client.getId())
                .dni(client.getDocumentNumber() + "1")
                .name(workflow.toClient().fullName())
                .account1(client.getAccountNumber())
                .iban(fintech.spain.alfa.product.testing.RandomData.randomIban().toString())
                .averageAmountOfIncomingTransactionsMonth(amount(1000))
                .averageAmountOfOutgoingTransactionsMonth(amount(3500))
                .build(),
            settingsJson))
        instantorService.processResponse(responseId)

        then:
        assert workflow.getActivityStatus(fintech.spain.alfa.product.workflow.undewrtiting.UnderwritingWorkflows.Activities.INSTANTOR_REVIEW) == ActivityStatus.ACTIVE

        and:
        with(instantorFacade.get(client.getId())) {
            assert !dniMatch
        }
    }

    def "request retry"() {
        when:
        def workflow = fintech.spain.alfa.product.testing.TestFactory.newClient()
            .signUp()
            .toLoanWorkflow()
            .runBeforeActivity(fintech.spain.alfa.product.workflow.undewrtiting.UnderwritingWorkflows.Activities.INSTANTOR_CALLBACK)
        def client = workflow.toClient().getClient()
        String settingsJson = settingsService.getString(AlfaSettings.INSTANTOR_SETTINGS)
        def responseId = instantorService.saveResponse(InstantorSimulation.simulateOkResponse(
            SimulateInstantorReq.builder()
                .clientId(client.getId())
                .dni(client.getDocumentNumber())
                .name(workflow.toClient().fullName())
                .account1(client.getAccountNumber())
                .iban(fintech.spain.alfa.product.testing.RandomData.randomIban().toString())
                .averageAmountOfIncomingTransactionsMonth(amount(1000))
                .averageAmountOfOutgoingTransactionsMonth(amount(3500))
                .build(),
            settingsJson))
        instantorService.processResponse(responseId)

        then:
        assert workflow.getActivityStatus(fintech.spain.alfa.product.workflow.undewrtiting.UnderwritingWorkflows.Activities.DOCUMENT_FORM) == ActivityStatus.COMPLETED
        assert workflow.getActivityStatus(fintech.spain.alfa.product.workflow.undewrtiting.UnderwritingWorkflows.Activities.INSTANTOR_REVIEW) == ActivityStatus.ACTIVE

        and:
        workflow.completeActivity(fintech.spain.alfa.product.workflow.undewrtiting.UnderwritingWorkflows.Activities.INSTANTOR_REVIEW, fintech.spain.alfa.product.workflow.common.Resolutions.REQUEST_RETRY)

        then:
        assert workflow.getActivityStatus(fintech.spain.alfa.product.workflow.undewrtiting.UnderwritingWorkflows.Activities.DOCUMENT_FORM) == ActivityStatus.ACTIVE
        assert workflow.getActivityStatus(fintech.spain.alfa.product.workflow.undewrtiting.UnderwritingWorkflows.Activities.INSTANTOR_REVIEW) == ActivityStatus.COMPLETED
    }

    @Unroll
    def "manual task resolution: #taskResolution"() {
        given:
        def workflow = fintech.spain.alfa.product.testing.TestFactory.newClient()
            .signUp()
            .toLoanWorkflow()
            .runBeforeActivity(fintech.spain.alfa.product.workflow.undewrtiting.UnderwritingWorkflows.Activities.INSTANTOR_MANUAL_CHECK)

        expect:
        assert workflow.getActivityStatus(fintech.spain.alfa.product.workflow.undewrtiting.UnderwritingWorkflows.Activities.INSTANTOR_MANUAL_CHECK) == ActivityStatus.ACTIVE

        when:
        workflow.taskOfActivity(fintech.spain.alfa.product.workflow.undewrtiting.UnderwritingWorkflows.Activities.INSTANTOR_MANUAL_CHECK).complete(taskResolution)

        then:
        assert workflow.getActivityResolution(fintech.spain.alfa.product.workflow.undewrtiting.UnderwritingWorkflows.Activities.INSTANTOR_MANUAL_CHECK) == activityResolution
        assert workflow.toApplication().getStatusDetail() == applicationStatusDetail

        where:
        taskResolution                   | activityResolution  | applicationStatusDetail
        UnderwritingTasks.InstantorManualCheckTask.APPROVE | fintech.spain.alfa.product.workflow.common.Resolutions.APPROVE | LoanApplicationStatusDetail.PENDING
        UnderwritingTasks.InstantorManualCheckTask.REJECT  | fintech.spain.alfa.product.workflow.common.Resolutions.REJECT  | LoanApplicationStatusDetail.REJECTED
    }

    def "instantor review reject for negative value of averageAmountOfOutgoingTransactionsPerMonth less then expected (abs value must be taken into account)"() {
        when:
        def client = fintech.spain.alfa.product.testing.TestFactory.newClient().signUp()
        def saveInstantorResponseCommand = InstantorSimulation.simulateOkResponseWithSingleAccount(
            client.getClientId(),
            client.getDni(),
            client.fullName(),
            client.getIban().toString(),
            701.00,
            -501.00)
        def workflow = client
            .toLoanWorkflow()
            .setInstantorResponse(saveInstantorResponseCommand)
            .runBeforeActivity(fintech.spain.alfa.product.workflow.undewrtiting.UnderwritingWorkflows.Activities.CREDIT_LIMIT)

        then:
        assert workflow.getActivityStatus(fintech.spain.alfa.product.workflow.undewrtiting.UnderwritingWorkflows.Activities.INSTANTOR_REVIEW) == ActivityStatus.COMPLETED
        assert workflow.getActivityResolution(fintech.spain.alfa.product.workflow.undewrtiting.UnderwritingWorkflows.Activities.INSTANTOR_RULES) == fintech.spain.alfa.product.workflow.common.Resolutions.REJECT
    }

    def "instantor review passed for negative value of averageAmountOfOutgoingTransactionsPerMonth greater then expected (abs value must be taken into account)"() {
        when:
        def client = fintech.spain.alfa.product.testing.TestFactory.newClient().signUp()
        def saveInstantorResponseCommand = InstantorSimulation.simulateOkResponseWithSingleAccount(
            client.getClientId(),
            client.getDni(),
            client.fullName(),
            client.getIban().toString(),
            701.00,
            -601.00)
        def workflow = client
            .toLoanWorkflow()
            .setInstantorResponse(saveInstantorResponseCommand)
            .runBeforeActivity(fintech.spain.alfa.product.workflow.undewrtiting.UnderwritingWorkflows.Activities.CREDIT_LIMIT)

        then:
        assert workflow.getActivityStatus(fintech.spain.alfa.product.workflow.undewrtiting.UnderwritingWorkflows.Activities.INSTANTOR_REVIEW) == ActivityStatus.COMPLETED
        assert workflow.getActivityResolution(fintech.spain.alfa.product.workflow.undewrtiting.UnderwritingWorkflows.Activities.INSTANTOR_RULES) == fintech.spain.alfa.product.workflow.common.Resolutions.APPROVE
    }

    def "Instantor name is taken from userDetails response in case if in account holder name it is empty"() {
        when:
        def client = fintech.spain.alfa.product.testing.TestFactory.newClient().signUp()
        def saveInstantorResponseCommand = InstantorSimulation.simulateOkResponseWithSingleAccount(
            client.getClientId(),
            client.getDni(),
            "",
            client.getIban().toString(),
            701.00,
            -601.00)
        client
            .toLoanWorkflow()
            .setInstantorResponse(saveInstantorResponseCommand)
            .runBeforeActivity(fintech.spain.alfa.product.workflow.undewrtiting.UnderwritingWorkflows.Activities.CREDIT_LIMIT)

        and:
        def response = instantorService.findLatest(InstantorResponseQuery.byClientIdAndResponseStatus(client.clientId, InstantorResponseStatus.OK))

        then:
        response.isPresent()
        response.get().nameForVerification == "D. Cristhian Carlos Arica Soto"
    }

    def "Instantor changing primary bank account updates its attributes"() {
        given:
        def iban1 = "ES1804584088791150228911"
        def iban2 = "ES8346190210811618055380"
        def client = fintech.spain.alfa.product.testing.TestFactory.newClient().signUp().setIban(Iban.valueOf(iban1))
        String settingsJson = settingsService.getString(AlfaSettings.INSTANTOR_SETTINGS)
        def saveInstantorResponseCommand = InstantorSimulation.simulateOkResponse(
            SimulateInstantorReq.builder()
                .clientId(client.getClientId())
                .dni(client.getDni())
                .name(client.getFirstName())
                .iban(iban1)
                .iban2(iban2)
                .averageAmountOfIncomingTransactionsMonth(701.00)
                .averageAmountOfOutgoingTransactionsMonth(-601.00)
                .build(),
            settingsJson
        )
        def workflow = client.toLoanWorkflow().setInstantorResponse(saveInstantorResponseCommand)

        when:
        client.addPrimaryBankAccount()
        workflow.runBeforeActivity(fintech.spain.alfa.product.workflow.undewrtiting.UnderwritingWorkflows.Activities.INSTANTOR_REVIEW)

        and:
        def response = instantorService.findLatest(InstantorResponseQuery.byClientIdAndResponseStatus(client.clientId, InstantorResponseStatus.OK))

        then:
        response.get().getAverageAmountOfIncomingTransactionsPerMonth() == 701.00
        response.get().getAverageAmountOfOutgoingTransactionsPerMonth() == 601.00
        response.get().getAverageMinimumBalancePerMonth() == 1.47
        response.get().getTotalNumberOfTransactions() == 336
        response.get().getMonthsAvailable() == 11

        when:
        client.setIban(Iban.valueOf(iban2)).addPrimaryBankAccount()
        workflow.runBeforeActivity(fintech.spain.alfa.product.workflow.undewrtiting.UnderwritingWorkflows.Activities.CREDIT_LIMIT)

        and:
        response = instantorService.findLatest(InstantorResponseQuery.byClientIdAndResponseStatus(client.clientId, InstantorResponseStatus.OK))

        then:
        response.get().getAverageAmountOfIncomingTransactionsPerMonth() == 549.5
        response.get().getAverageAmountOfOutgoingTransactionsPerMonth() == 521.78
        response.get().getAverageMinimumBalancePerMonth() == 45.67
        response.get().getTotalNumberOfTransactions() == 82
        response.get().getMonthsAvailable() == 9
    }

    def "Instantor load fake response from settings"() {
        when:
        def workflow = fintech.spain.alfa.product.testing.TestFactory.newClient()
            .signUp()
            .toLoanWorkflow()
            .runBeforeActivity(fintech.spain.alfa.product.workflow.undewrtiting.UnderwritingWorkflows.Activities.INSTANTOR_CALLBACK)

        then:
        assert workflow.getActivityStatus(fintech.spain.alfa.product.workflow.undewrtiting.UnderwritingWorkflows.Activities.INSTANTOR_CALLBACK) == ActivityStatus.ACTIVE
        assert workflow.getActivityStatus(fintech.spain.alfa.product.workflow.undewrtiting.UnderwritingWorkflows.Activities.INSTANTOR_REVIEW) == ActivityStatus.WAITING

        when:
        def client = workflow.toClient().getClient()

        String settingsJson = settingsService.getString(AlfaSettings.INSTANTOR_SETTINGS)
        String iban = fintech.spain.alfa.product.testing.RandomData.randomIban().toString()

        def responseId = instantorService.saveResponse(InstantorSimulation.simulateOkResponse(
            SimulateInstantorReq.builder()
                .clientId(client.getId())
                .dni(client.getDocumentNumber())
                .name(StringUtils.join(client.getFirstName(), client.getLastName(), client.getSecondLastName()))
                .iban(iban)
                .iban2(fintech.spain.alfa.product.testing.RandomData.randomIban().toString())
                .iban3(fintech.spain.alfa.product.testing.RandomData.randomIban().toString())
                .averageAmountOfIncomingTransactionsMonth(amount(1000))
                .averageAmountOfOutgoingTransactionsMonth(amount(3500))
                .build(), settingsJson))
        instantorService.processResponse(responseId)

        then:
        assert workflow.getActivityStatus(fintech.spain.alfa.product.workflow.undewrtiting.UnderwritingWorkflows.Activities.INSTANTOR_REVIEW) == ActivityStatus.ACTIVE

        and:
        with(instantorFacade.get(client.getId())) {
            assert dniMatch
            assert accounts.any { fintech.spain.alfa.product.instantor.InstantorReviewResponse.Account a -> a.bankAccountNumber == iban }
        }
    }

}
