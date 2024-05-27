package fintech.spain.alfa.product.workflow.dormants

import com.google.common.collect.ImmutableList
import fintech.TimeMachine
import fintech.instantor.InstantorService
import fintech.instantor.InstantorSimulation
import fintech.instantor.model.SaveInstantorResponseCommand
import fintech.lending.core.application.LoanApplicationStatus
import fintech.lending.core.application.LoanApplicationStatusDetail
import fintech.sms.SmsService
import fintech.spain.platform.web.SpecialLinkType
import fintech.spain.platform.web.spi.SpecialLinkService
import fintech.spain.alfa.product.AbstractAlfaTest
import fintech.spain.alfa.product.cms.CmsSetup
import fintech.spain.alfa.product.workflow.dormants.task.LocApproveLoanOfferCallTask
import fintech.spain.alfa.product.workflow.dormants.task.LocPhoneValidationCallTask
import fintech.spain.alfa.product.workflow.dormants.task.LocPreOfferCallTask
import fintech.task.TaskService
import fintech.workflow.WorkflowService
import fintech.workflow.WorkflowStatus
import fintech.workflow.impl.WorkflowBackgroundJobs
import org.apache.commons.lang3.StringUtils
import org.springframework.beans.factory.annotation.Autowired
import spock.lang.Ignore
import spock.lang.Unroll

import java.time.Duration

import static fintech.BigDecimalUtils.amount
import static fintech.DateUtils.D_15_MINUTES
import static fintech.DateUtils.D_72_HOURS
import static fintech.spain.platform.web.model.command.SpecialLinkQuery.byClientId
import static fintech.spain.alfa.product.workflow.dormants.LocInstantorWorkflows.Activities.*

class DormantsRecentInstantorWFTest extends AbstractAlfaTest {

    static final Duration _SYSTEM_ACTIVITY = Duration.ofMillis(100)

    @Autowired
    WorkflowService workflowService

    @Autowired
    fintech.spain.alfa.product.lending.UnderwritingFacade underwritingFacade

    @Autowired
    WorkflowBackgroundJobs workflowBackgroundJobs

    @Autowired
    SpecialLinkService specialLinkService

    @Autowired
    fintech.spain.alfa.product.web.spi.PopupService popupService

    @Autowired
    SmsService smsService

    @Autowired
    TaskService taskService

    @Autowired
    InstantorService instantorService

    @Autowired
    fintech.spain.alfa.product.workflow.common.WorkflowEventListeners eventListeners

    def "Full Straightforward Scenario"() {
        when:
        def workflow = "Somebody started DormantsRecentInstantor workflow"()
        def client = workflow.toClient()

        then: "DormantsRecentInstantor WF started"
        workflow.isActive()
        workflow.areOnlyActiveActivities(LocInstantorRules)

        when: "wf execution started"
        workflow.runBeforeActivity(LocPreOfferCall)

        then: "offer data has prepared"
        workflow.areAllActivitiesCompleted(
            LocInstantorRules,
            LocNordigen,
            LocCreditLimit,
            LocCreditLimitRules,
            LocPreOfferDataPreparation,
            LocPreOffer,
            LocPreOfferEmail,
            LocPreOfferSMS
        )
        workflow.areOnlyActiveActivities(LocPreOfferCall)

        and: "client can see pre offer popup"
        popupService.getActual(client.clientId).stream().filter { popup -> popup.type == fintech.spain.alfa.product.web.model.PopupType.SPECIAL_OFFER }.findAny().isPresent()

        and: "LocPreOfferCallTask created"
        workflow.taskOfActivity(LocPreOfferCall).getTask().getTaskType() == LocPreOfferCallTask.TYPE_RECENT_INSTANTOR

        and:
        "Client received pre offer SMS and Email"(client)

        when: "client got pre offer call and accepted it"
        workflow.taskOfActivity(LocPreOfferCall).complete(LocPreOfferCallTask.CLIENT_INTERESTED)

        then: "client can see Instantor form in UA"
        workflow.isActivityCompleted(LocPreOfferCall)

        when: "system prepared offer"
        workflow.runAfterActivity(LocLoanOffer)

        then: "all steps were completed"
        workflow.areAllActivitiesCompleted(LocPhoneValidationCall, LocLoanOffer)

        and: "system ready to send offer"
        workflow.areOnlyActiveActivities(LocLoanOfferEmail, LocLoanOfferSMS, LocApprovalCall, LocApproveLoanOffer)

        when: "system sent offer"
        workflow.runBeforeActivity(LocApprovalCall)

        then: "client can see LOC Offer in UA"
        workflow.areAllActivitiesCompleted(LocLoanOfferEmail, LocLoanOfferSMS)

        and:
        "Client received SMS and Email with offer"(client)

        and: "LocApprovalCallTask was created"
        workflow.taskOfActivity(LocApprovalCall).getTask().getTaskType() == LocApproveLoanOfferCallTask.TYPE

        when: "client still interested in offer during call"
        workflow.taskOfActivity(LocApprovalCall).complete(LocApproveLoanOfferCallTask.CLIENT_INTERESTED)

        and:
        "Client accepted offer via UA"(client)

        then:
        workflow.areAllActivitiesCompleted(LocApprovalCall, LocApproveLoanOffer)
        workflow.areOnlyActiveActivities(LocSendToPresto)

        when:
        workflow.runAll()

        then:
        workflow.areAllActivitiesCompleted(LocSendToPresto, LocSendToPrestoRedirect)
        // Are you going to change number? Write tests!
        workflow.getWorkflow().getActivities().size() == 16

        workflow.isCompleted()
    }

    @Unroll
    def "Pre Offer accepted via #preOfferBy And Offer Accepted via #offerBy"() {
        when:
        def workflow = "Somebody started DormantsRecentInstantor workflow"()
        def client = workflow.toClient()

        and: "wf execution started"
        workflow.runBeforeActivity(LocPreOfferCall)

        then:
        "Client received pre offer SMS and Email"(client)

        when:
        "Client accepted pre offer via $preOfferBy"(client)

        and: "Client filled out all forms in UA"
        workflow.runBeforeActivity(LocApprovalCall)

        then:
        "Client received SMS and Email with offer"(client)

        when:
        "Client accepted offer via $preOfferBy"(client)

        and:
        workflow.runAll()

        then:
        workflow.isActivityCompleted(LocSendToPrestoRedirect)
        workflow.isCompleted()

        where:
        preOfferBy | offerBy
        "UA"       | "UA"
        "UA"       | "SMS"
        "UA"       | "Email"
        "SMS"      | "UA"
        "SMS"      | "SMS"
        "SMS"      | "EMail"
        "Email"    | "UA"
        "Email"    | "SMS"
        "Email"    | "Email"
    }

    @Unroll
    def "#activity -> #taskType:#taskResolution -> WF:#wfStatus"() {
        when:
        def workflow = "Somebody started DormantsRecentInstantor workflow"()
        def client = workflow.toClient()

        if (activity == LocInstantorManualCheck) {
            workflow.setInstantorResponseSupplier(INSTANTOR_RESPONSE_WITH_MISTAKE(client))
        }

        and:
        if (activity == LocPhoneValidationCall) {
            workflow.runBeforeActivity(LocPreOfferCall)
            "Client accepted pre offer via SMS"(client)
        }
        workflow.runBeforeActivity(activity)

        then:
        def task = workflow.taskOfActivity(activity)
        task.getTask().getTaskType() == taskType

        when:
        task.complete(taskResolution)

        then:
        workflow.getWorkflow().getStatus() == wfStatus

        where:
        activity               | taskType                                  | taskResolution                                    | wfStatus
        LocPreOfferCall        | LocPreOfferCallTask.TYPE_RECENT_INSTANTOR | LocPreOfferCallTask.NO_ANSWER                     | WorkflowStatus.ACTIVE
        LocPreOfferCall        | LocPreOfferCallTask.TYPE_RECENT_INSTANTOR | LocPreOfferCallTask.UNREACHABLE                   | WorkflowStatus.ACTIVE
        LocPreOfferCall        | LocPreOfferCallTask.TYPE_RECENT_INSTANTOR | LocPreOfferCallTask.CLIENT_INTERESTED             | WorkflowStatus.ACTIVE
        LocPreOfferCall        | LocPreOfferCallTask.TYPE_RECENT_INSTANTOR | LocPreOfferCallTask.CLIENT_NOT_INTERESTED         | WorkflowStatus.TERMINATED
        LocPreOfferCall        | LocPreOfferCallTask.TYPE_RECENT_INSTANTOR | LocPreOfferCallTask.EXPIRE                        | WorkflowStatus.EXPIRED
        LocPhoneValidationCall | LocPhoneValidationCallTask.TYPE           | LocPhoneValidationCallTask.POSTPONE               | WorkflowStatus.ACTIVE
        LocPhoneValidationCall | LocPhoneValidationCallTask.TYPE           | LocPhoneValidationCallTask.UNREACHABLE            | WorkflowStatus.ACTIVE
        LocPhoneValidationCall | LocPhoneValidationCallTask.TYPE           | LocPhoneValidationCallTask.PHONE_IS_VALID         | WorkflowStatus.ACTIVE
        LocPhoneValidationCall | LocPhoneValidationCallTask.TYPE           | LocPhoneValidationCallTask.PHONE_IS_INVALID       | WorkflowStatus.TERMINATED
        LocPhoneValidationCall | LocPhoneValidationCallTask.TYPE           | LocPhoneValidationCallTask.EXPIRE                 | WorkflowStatus.EXPIRED
        LocApprovalCall        | LocApproveLoanOfferCallTask.TYPE          | LocApproveLoanOfferCallTask.POSTPONE              | WorkflowStatus.ACTIVE
        LocApprovalCall        | LocApproveLoanOfferCallTask.TYPE          | LocApproveLoanOfferCallTask.UNREACHABLE           | WorkflowStatus.ACTIVE
        LocApprovalCall        | LocApproveLoanOfferCallTask.TYPE          | LocApproveLoanOfferCallTask.CLIENT_INTERESTED     | WorkflowStatus.ACTIVE
        LocApprovalCall        | LocApproveLoanOfferCallTask.TYPE          | LocApproveLoanOfferCallTask.CLIENT_REJECTED_OFFER | WorkflowStatus.TERMINATED
        LocApprovalCall        | LocApproveLoanOfferCallTask.TYPE          | LocApproveLoanOfferCallTask.EXPIRE                | WorkflowStatus.ACTIVE
    }

    @Unroll
    def "Expiration of #activity is #expiration with WF state #wfStatus"() {
        when:
        def workflow = "Somebody started DormantsRecentInstantor workflow"()
        def client = workflow.toClient()

        if (activity == LocApproveLoanOffer) {
            workflow.setTaskResolution(LocApproveLoanOfferCallTask.TYPE, LocApproveLoanOfferCallTask.UNREACHABLE)
        }

//        if (activity == LocInstantorManualCheck) {
//            workflow.setInstantorResponseSupplier(INSTANTOR_RESPONSE_WITH_MISTAKE(client))
//        }

        and:
        if (activity == LocPhoneValidationCall) {
            workflow.runBeforeActivity(LocPreOfferCall)
            "Client accepted pre offer via SMS"(client)
        }
        workflow.runBeforeActivity(activity)
        workflowBackgroundJobs.run(
            TimeMachine.now()
                .plus(expiration)
                .plusSeconds(15)
        )

        then:
        workflow.getActivity(activity).resolution == resolution
        workflow.getWorkflow().getStatus() == wfStatus


        where:
        activity                   | expiration       | resolution          | wfStatus
        LocInstantorRules          | _SYSTEM_ACTIVITY | fintech.spain.alfa.product.workflow.common.Resolutions.APPROVE | WorkflowStatus.ACTIVE
//        LocInstantorManualCheck    | D_72_HOURS       | Resolutions.EXPIRE  | WorkflowStatus.TERMINATED
//        LocNordigen                | _SYSTEM_ACTIVITY | Resolutions.OK      | WorkflowStatus.ACTIVE
        LocCreditLimit             | _SYSTEM_ACTIVITY | fintech.spain.alfa.product.workflow.common.Resolutions.OK      | WorkflowStatus.ACTIVE
        LocPreOfferDataPreparation | _SYSTEM_ACTIVITY | fintech.spain.alfa.product.workflow.common.Resolutions.OK      | WorkflowStatus.ACTIVE
        LocPreOffer                | _SYSTEM_ACTIVITY | fintech.spain.alfa.product.workflow.common.Resolutions.OK      | WorkflowStatus.ACTIVE
        LocPreOfferEmail           | _SYSTEM_ACTIVITY | fintech.spain.alfa.product.workflow.common.Resolutions.OK      | WorkflowStatus.ACTIVE
        LocPreOfferSMS             | _SYSTEM_ACTIVITY | fintech.spain.alfa.product.workflow.common.Resolutions.OK      | WorkflowStatus.ACTIVE
        LocPreOfferCall            | D_72_HOURS       | fintech.spain.alfa.product.workflow.common.Resolutions.EXPIRE  | WorkflowStatus.EXPIRED
        LocPhoneValidationCall     | D_72_HOURS       | fintech.spain.alfa.product.workflow.common.Resolutions.EXPIRE  | WorkflowStatus.EXPIRED
        LocLoanOffer               | _SYSTEM_ACTIVITY | fintech.spain.alfa.product.workflow.common.Resolutions.OK      | WorkflowStatus.ACTIVE
        LocLoanOfferEmail          | _SYSTEM_ACTIVITY | fintech.spain.alfa.product.workflow.common.Resolutions.OK      | WorkflowStatus.ACTIVE
        LocLoanOfferSMS            | _SYSTEM_ACTIVITY | fintech.spain.alfa.product.workflow.common.Resolutions.OK      | WorkflowStatus.ACTIVE
        LocApprovalCall            | D_72_HOURS       | fintech.spain.alfa.product.workflow.common.Resolutions.EXPIRE  | WorkflowStatus.EXPIRED
        LocApproveLoanOffer        | D_72_HOURS       | fintech.spain.alfa.product.workflow.common.Resolutions.EXPIRE  | WorkflowStatus.EXPIRED
        LocSendToPresto            | _SYSTEM_ACTIVITY | fintech.spain.alfa.product.workflow.common.Resolutions.OK      | WorkflowStatus.ACTIVE
    }

    @Unroll
    def "Instantor Rules #avgOutTxs #avgInTxs #resolution"() {
        when:
        def workflow = "Somebody started DormantsRecentInstantor workflow with "({
            fintech.spain.alfa.product.testing.TestClient client ->
                return InstantorSimulation.simulateOkResponseWithSingleAccount(
                    client.clientId, dni(client), fullName(client), client.iban.toString(), avgInTxs, avgOutTxs
                )
        })

        and:
        workflow.runAll()

        then:
        workflow.getActivity(LocInstantorRules).resolution == resolution
        workflow.getWorkflow().getStatus() == wfStatus

        where:
        dni            | fullName            | avgOutTxs | avgInTxs | resolution          | wfStatus
        ORIGINAL_DNI   | ORIGINAL_FULLNAME   | 2500.00   | 600.00   | fintech.spain.alfa.product.workflow.common.Resolutions.APPROVE | WorkflowStatus.COMPLETED
        SIMILAR_DNI    | ORIGINAL_FULLNAME   | 2500.00   | 600.00   | fintech.spain.alfa.product.workflow.common.Resolutions.MANUAL  | WorkflowStatus.COMPLETED
        DISSIMILAR_DNI | ORIGINAL_FULLNAME   | 2500.00   | 600.00   | fintech.spain.alfa.product.workflow.common.Resolutions.MANUAL  | WorkflowStatus.COMPLETED
        ORIGINAL_DNI   | SIMILAR_FULLNAME    | 2500.00   | 600.00   | fintech.spain.alfa.product.workflow.common.Resolutions.MANUAL  | WorkflowStatus.COMPLETED
        ORIGINAL_DNI   | DISSIMILAR_FULLNAME | 2500.00   | 600.00   | fintech.spain.alfa.product.workflow.common.Resolutions.MANUAL  | WorkflowStatus.COMPLETED
        ORIGINAL_DNI   | ORIGINAL_FULLNAME   | 2000.00   | 600.00   | fintech.spain.alfa.product.workflow.common.Resolutions.APPROVE | WorkflowStatus.COMPLETED
        ORIGINAL_DNI   | ORIGINAL_FULLNAME   | 599.00    | 600.00   | fintech.spain.alfa.product.workflow.common.Resolutions.REJECT  | WorkflowStatus.TERMINATED
        ORIGINAL_DNI   | ORIGINAL_FULLNAME   | 600.00    | 0.00     | fintech.spain.alfa.product.workflow.common.Resolutions.APPROVE | WorkflowStatus.COMPLETED
    }

    // Not implementen in "platform"
    @Ignore
    def "Instantor Review Form Retry Test"() {
        when:
        def workflow = "Somebody started DormantsRecentInstantor workflow"()

        and: "client retries requests 2 times"
        workflow.setAction(LocInstantorReview, {
            workflow.completeActivity(LocInstantorReview, fintech.spain.alfa.product.workflow.common.Resolutions.REQUEST_RETRY)
        })
        3.times { workflow.runAll() }

        then: "Instantor steps were retried 3 times"
        workflow.getActivity(LocInstantorForm).attempts == 3
        workflow.getActivity(LocInstantorForm).resolution == fintech.spain.alfa.product.workflow.common.Resolutions.INSTANTOR_FORM_COMPLETED

        workflow.getActivity(LocInstantorReview).attempts == 3
        workflow.getActivity(LocInstantorReview).resolution == fintech.spain.alfa.product.workflow.common.Resolutions.REQUEST_RETRY

        workflow.getActivity(LocInstantorRules).attempts == 0
        workflow.getActivity(LocInstantorRules).resolution == null

        workflow.getActivity(LocInstantorManualCheck).attempts == 0
        workflow.getActivity(LocInstantorManualCheck).resolution == null

        when: "client retries request fourth time"
        workflow.runAll()

        then: "Workflow should be terminated"
        workflow.getActivity(LocInstantorReview).attempts == 4
        workflow.isTerminated()
    }

    @Unroll
    def "Notification #activity -> #duration -> #cmsTemplate"() {
        when:
        def workflow = "Somebody started DormantsRecentInstantor workflow"()
        def client = workflow.toClient()

        and:
        prepare(workflow)
        if (duration == _SYSTEM_ACTIVITY) {
            workflow.runAfterActivity(activity)
        } else {
            workflow.runBeforeActivity(activity)
            workflowBackgroundJobs.run(
                TimeMachine.now()
                    .plus(duration)
                    .plusSeconds(5)
            )
        }

        then:
        type == "email" || client.smsCount(cmsTemplate) == 1
        type == "sms"   || client.emailCount(cmsTemplate) == 1


        where:
        activity               | duration         | type    | cmsTemplate                                 | prepare
        LocPreOfferEmail       | _SYSTEM_ACTIVITY | "email" | CmsSetup.LOC_PRE_OFFER_EMAIL                | NOTHING
        LocPreOfferSMS         | _SYSTEM_ACTIVITY | "sms"   | CmsSetup.LOC_PRE_OFFER_SMS                  | NOTHING

        LocPreOfferCall        | D_72_HOURS       | "email" | CmsSetup.LOC_LOAN_APPLICATION_EXPIRED_EMAIL | NOTHING
        LocPreOfferCall        | D_72_HOURS       | "sms"   | CmsSetup.LOC_LOAN_APPLICATION_EXPIRED_SMS   | NOTHING

        LocPhoneValidationCall | D_72_HOURS       | "email" | CmsSetup.LOC_LOAN_APPLICATION_EXPIRED_EMAIL | LOC_PRE_OFFER_CALL_SKIPPED
        LocPhoneValidationCall | D_72_HOURS       | "sms"   | CmsSetup.LOC_LOAN_APPLICATION_EXPIRED_SMS   | LOC_PRE_OFFER_CALL_SKIPPED

        LocLoanOfferEmail      | _SYSTEM_ACTIVITY | "email" | CmsSetup.LOC_LOAN_OFFER_EMAIL               | NOTHING
        LocLoanOfferSMS        | _SYSTEM_ACTIVITY | "sms"   | CmsSetup.LOC_LOAN_OFFER_SMS                 | NOTHING

        LocApprovalCall        | D_72_HOURS       | "email" | CmsSetup.LOC_LOAN_APPLICATION_EXPIRED_EMAIL | NOTHING
        LocApprovalCall        | D_72_HOURS       | "sms"   | CmsSetup.LOC_LOAN_APPLICATION_EXPIRED_SMS   | NOTHING

        LocApproveLoanOffer    | D_15_MINUTES     | "email" | CmsSetup.LOC_LOAN_OFFER_EMAIL_REMINDER      | NOTHING
        LocApproveLoanOffer    | D_15_MINUTES     | "sms"   | CmsSetup.LOC_LOAN_OFFER_SMS_REMINDER        | NOTHING
        LocApproveLoanOffer    | D_72_HOURS       | "email" | CmsSetup.LOC_LOAN_APPLICATION_EXPIRED_EMAIL | NOTHING
        LocApproveLoanOffer    | D_72_HOURS       | "sms"   | CmsSetup.LOC_LOAN_APPLICATION_EXPIRED_SMS   | NOTHING

    }

    @Unroll
    def "Credit limit calculation checks : maxAllowed = #maxAllowed, coefficient = #coefficient, maxPaidPrincipal= #maxPaidPrincipal, expectedAmount: #expectedAmount"() {
        given:
        def client = fintech.spain.alfa.product.testing.TestFactory.newClient().registerDirectly()


        3.times { it ->
            client
                .submitApplicationAndStartFirstLoanWorkflow(amount(maxPaidPrincipal), 30L, TimeMachine.today().minusDays((4 - it) * 30))
                .toLoanWorkflow()
                .setInstantorResponse(INSTANTOR_RESPONSE_GOOD(client))
                .runAll()
                .toLoan()
                .exportDisbursements(TimeMachine.today().minusDays((4 - it) * 30))
                .settleDisbursements(TimeMachine.today().minusDays((4 - it) * 30))
                .repayAll(TimeMachine.today().minusDays((3 - it) * 30))
        }


        def settings = settingsService.getJson(fintech.spain.alfa.product.settings.LocSettings.LOC_CREDIT_LIMIT_SETTINGS, fintech.spain.alfa.product.settings.LocSettings.LocCreditLimitSettings.class)
        settings.setCreditLimitCalculatedCoefficient(coefficient)
        settings.setMaxCreditLimitAllowed(maxAllowed)
        saveJsonSettings(fintech.spain.alfa.product.settings.LocSettings.LOC_CREDIT_LIMIT_SETTINGS, settings)

        when:
        def workflow = "Somebody started DormantsLocInstantor workflow for "(client)
        workflow.runAfterActivity(LocCreditLimit)

        then:
        workflow.toApplication().application.getCreditLimit() == expectedAmount

        where:
        maxAllowed | coefficient | maxPaidPrincipal | expectedAmount
        500.00     | 1.5         | 1000.00          | 500.00
        500.00     | 1.5         | 150.00           | 200.00
        500.00     | 1.5         | 100.00           | 200.00
    }

    @Unroll
    def "Credit limit rules checks : minAllowed = #minAllowed; maxAllowed = #maxAllowed, coefficient = #coefficient, maxPaidPrincipal= #maxPaidPrincipal, resolution: #resolution"() {
        given:
        def client = fintech.spain.alfa.product.testing.TestFactory.newClient().registerDirectly()

        client
            .submitApplicationAndStartFirstLoanWorkflow(amount(maxPaidPrincipal), 30L, TimeMachine.today().minusDays(30))
            .toLoanWorkflow()
            .setInstantorResponse(INSTANTOR_RESPONSE_GOOD(client))
            .runAll()
            .exportDisbursement()
            .toLoan()
            .repayAll(TimeMachine.today())

        def settings = settingsService.getJson(fintech.spain.alfa.product.settings.LocSettings.LOC_CREDIT_LIMIT_SETTINGS, fintech.spain.alfa.product.settings.LocSettings.LocCreditLimitSettings.class)
        settings.setCreditLimitCalculatedCoefficient(coefficient)
        settings.setMinCreditLimitAllowed(minAllowed)
        settings.setMaxCreditLimitAllowed(maxAllowed)
        saveJsonSettings(fintech.spain.alfa.product.settings.LocSettings.LOC_CREDIT_LIMIT_SETTINGS, settings)

        when:
        def workflow = "Somebody started DormantsLocInstantor workflow for "(client)
        workflow.runAfterActivity(LocCreditLimitRules)

        then:
        workflow.getActivityResolution(LocCreditLimitRules) == resolution

        where:
        minAllowed | maxAllowed | coefficient | maxPaidPrincipal | resolution
        300.00     | 500.00     | 1.5         | 300.00           | fintech.spain.alfa.product.workflow.common.Resolutions.APPROVE
        300.00     | 500.00     | 1.5         | 500.00           | fintech.spain.alfa.product.workflow.common.Resolutions.APPROVE
        300.00     | 500.00     | 1.5         | 200.00           | fintech.spain.alfa.product.workflow.common.Resolutions.APPROVE
        300.00     | 500.00     | 1.5         | 100.00           | fintech.spain.alfa.product.workflow.common.Resolutions.REJECT
    }

    def "Application approved after WF completion"() {
        when:
        def workflow = 'Somebody started DormantsRecentInstantor workflow'()

        and:
        workflow.runAll()

        then:
        workflow.workflow.status == WorkflowStatus.COMPLETED
        workflow.toApplication().application.status == LoanApplicationStatus.CLOSED
        workflow.toApplication().application.statusDetail == LoanApplicationStatusDetail.APPROVED
    }

    private fintech.spain.alfa.product.testing.TestClient newClient() {
        def client = fintech.spain.alfa.product.testing.TestFactory.newClient()
            .setDateOfBirth(TimeMachine.today().minusYears(36))
            .setAmount(1000.00)
            .signUp()

        client.toLoanWorkflow()
            .setInstantorResponse(INSTANTOR_RESPONSE_GOOD(client))
            .runAll()
            .exportDisbursement()
            .toLoan()
            .repayAll(TimeMachine.today())

        return client
    }

    def "Somebody started DormantsLocInstantor workflow for "(fintech.spain.alfa.product.testing.TestClient client) {
        assert client.submitLineOfCreditAndStartWorkflow(2000.00, TimeMachine.now())
            .toDormantsQualifyLocWorkflow()
            .runAll()
            .isCompleted()

        return client.toWorkflow(LocInstantorWorkflows.DORMANTS_LOC_RECENT_INSTANTOR, fintech.spain.alfa.product.testing.TestDormantsRecentInstantorWorkflow.class)
    }

    def 'Somebody started DormantsRecentInstantor workflow'() {
        def client = newClient()

        assert client.submitLineOfCreditAndStartWorkflow(2000.00, TimeMachine.now())
            .toDormantsQualifyLocWorkflow()
            .runAll()
            .isCompleted()

        return client.toWorkflow(LocInstantorWorkflows.DORMANTS_LOC_RECENT_INSTANTOR, fintech.spain.alfa.product.testing.TestDormantsRecentInstantorWorkflow.class)
    }

    def 'Somebody started DormantsRecentInstantor workflow with '(getInstantorSaveCommand) {
        def client = newClient()

        SaveInstantorResponseCommand command = getInstantorSaveCommand(client)
        instantorService.processResponse(instantorService.saveResponse(command))

        assert client.submitLineOfCreditAndStartWorkflow(2000.00, TimeMachine.now())
            .toDormantsQualifyLocWorkflow()
            .runAll()
            .isCompleted()

        return client.toWorkflow(LocInstantorWorkflows.DORMANTS_LOC_RECENT_INSTANTOR, fintech.spain.alfa.product.testing.TestDormantsRecentInstantorWorkflow.class)
    }

    def "Client received pre offer SMS and Email"(fintech.spain.alfa.product.testing.TestClient client) {
        return client.smsCount(CmsSetup.LOC_PRE_OFFER_SMS) == 1 && client.emailCount(CmsSetup.LOC_PRE_OFFER_EMAIL) == 1
    }

    def "Client received SMS and Email with offer"(fintech.spain.alfa.product.testing.TestClient client) {
        return client.smsCount(CmsSetup.LOC_LOAN_OFFER_SMS) == 1 && client.emailCount(CmsSetup.LOC_LOAN_OFFER_EMAIL) == 1
    }

    def "Client accepted pre offer via UA"(fintech.spain.alfa.product.testing.TestClient client) {
        def popupInfo = popupService.getActual(client.clientId).stream()
            .filter { popup -> popup.type == fintech.spain.alfa.product.web.model.PopupType.SPECIAL_OFFER }
            .findAny().orElseThrow { new IllegalStateException() }
        popupService.resolve(popupInfo.id, fintech.spain.alfa.product.web.model.PopupResolution.ACCEPTED)
    }

    def "Client accepted offer via UA"(fintech.spain.alfa.product.testing.TestClient client) {
        def workflow = client.toWorkflow(LocInstantorWorkflows.DORMANTS_LOC_RECENT_INSTANTOR, fintech.spain.alfa.product.testing.TestDormantsRecentInstantorWorkflow.class)
        underwritingFacade.webApproveApplication(client.getClientId(), workflow.applicationId, "127.0.0.1")
    }

    def "Client accepted pre offer via SMS"(fintech.spain.alfa.product.testing.TestClient client) {
        def link = specialLinkService.findRequiredLink(byClientId(client.getClientId(), SpecialLinkType.LOC_SPECIAL_OFFER))
        specialLinkService.activateLink(link.token)
    }

    def "Client accepted offer via SMS"(fintech.spain.alfa.product.testing.TestClient client) {
        def link = specialLinkService.findRequiredLink(byClientId(client.getClientId(), SpecialLinkType.LOC_SPECIAL_OFFER))
        specialLinkService.activateLink(link.token)
    }

    def "Client accepted pre offer via Email"(fintech.spain.alfa.product.testing.TestClient client) {
        def link = specialLinkService.findRequiredLink(byClientId(client.getClientId(), SpecialLinkType.LOC_SPECIAL_OFFER))
        specialLinkService.activateLink(link.token)
    }

    def "Client accepted offer via Email"(fintech.spain.alfa.product.testing.TestClient client) {
        def workflow = client.toWorkflow(LocInstantorWorkflows.DORMANTS_LOC_RECENT_INSTANTOR, fintech.spain.alfa.product.testing.TestDormantsRecentInstantorWorkflow.class)
        underwritingFacade.webApproveApplication(client.getClientId(), workflow.applicationId, "127.0.0.1")
    }

    def static INSTANTOR_RESPONSE_GOOD = { fintech.spain.alfa.product.testing.TestClient client ->
        return InstantorSimulation.simulateOkResponseWithSingleAccount(client.clientId, client.dni, client.fullName(), client.iban.toString(), amount(1000), amount(3500))
    }

    def static INSTANTOR_RESPONSE_WITH_MISTAKE = { fintech.spain.alfa.product.testing.TestClient client ->
        return InstantorSimulation.simulateOkResponseWithSingleAccount(client.clientId, client.dni, client.fullName() + "O", client.iban.toString(), amount(1000), amount(3500))
    }

    def static NOTHING = { fintech.spain.alfa.product.testing.TestDormantsRecentInstantorWorkflow workflow -> }

    def static LOC_PRE_OFFER_CALL_SKIPPED = { fintech.spain.alfa.product.testing.TestDormantsRecentInstantorWorkflow workflow ->
        workflow.setAction(LocPreOfferCall, { workflow.completeActivity(LocPreOfferCall, fintech.spain.alfa.product.workflow.common.Resolutions.SKIP) })
    }

    def static ORIGINAL_DNI = { fintech.spain.alfa.product.testing.TestClient client -> client.dni }
    def static SIMILAR_DNI = { fintech.spain.alfa.product.testing.TestClient client -> StringUtils.join(client.dni, "O") }
    def static DISSIMILAR_DNI = { fintech.spain.alfa.product.testing.TestClient client -> fintech.spain.alfa.product.testing.RandomData.randomDni() }

    def static ORIGINAL_FULLNAME = { fintech.spain.alfa.product.testing.TestClient client -> client.fullName() }
    def static SIMILAR_FULLNAME = { fintech.spain.alfa.product.testing.TestClient client -> StringUtils.join(ImmutableList.of(client.firstName + "O", client.lastName, client.secondLastName)) }
    def static DISSIMILAR_FULLNAME = { fintech.spain.alfa.product.testing.TestClient client -> "Some Fake Full Name" }
}
