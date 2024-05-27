package fintech.spain.alfa.product.workflow

import com.google.common.collect.ImmutableList
import fintech.TimeMachine
import fintech.instantor.InstantorSimulation
import fintech.lending.core.application.LoanApplicationStatus
import fintech.lending.core.application.LoanApplicationStatusDetail
import fintech.sms.SmsService
import fintech.spain.platform.web.SpecialLinkType
import fintech.spain.platform.web.spi.SpecialLinkService
import fintech.spain.alfa.product.AbstractAlfaTest
import fintech.spain.alfa.product.cms.CmsSetup
import fintech.spain.alfa.product.testing.settings.LocTestSettings
import fintech.spain.alfa.product.workflow.common.Attributes
import fintech.spain.alfa.product.workflow.common.Resolutions
import fintech.spain.alfa.product.workflow.common.WorkflowEventListeners

import fintech.spain.alfa.product.workflow.dormants.event.InstantorCanceledByClient
import fintech.spain.alfa.product.workflow.dormants.task.LocApproveLoanOfferCallTask
import fintech.spain.alfa.product.workflow.dormants.task.LocPhoneValidationCallTask
import fintech.spain.alfa.product.workflow.dormants.task.LocPreOfferCallTask
import fintech.spain.alfa.product.workflow.dormants.task.LocPrestoReminderCallTask
import fintech.task.TaskService
import fintech.task.model.TaskStatus
import fintech.workflow.ActivityStatus
import fintech.workflow.WorkflowService
import fintech.workflow.WorkflowStatus
import fintech.workflow.impl.WorkflowBackgroundJobs
import org.apache.commons.lang3.StringUtils
import org.springframework.beans.factory.annotation.Autowired
import spock.lang.Ignore
import spock.lang.Unroll

import java.time.Duration
import java.time.LocalDateTime

import static fintech.BigDecimalUtils.amount
import static fintech.DateUtils.D_15_MINUTES
import static fintech.DateUtils.D_25_MINUTES
import static fintech.DateUtils.D_2_MINUTES
import static fintech.DateUtils.D_72_HOURS
import static fintech.spain.platform.web.model.command.SpecialLinkQuery.byClientId
import static fintech.spain.alfa.product.workflow.undewrtiting.UnderwritingTasks.InstantorManualCheckTask

class DormantsLocInstantorWFTest extends AbstractAlfaTest {

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
    WorkflowEventListeners eventListeners

    @Autowired
    LocTestSettings locTestSettings

    @Override
    def setup() {
        locTestSettings.setUp()
    }

    def "Full Straightforward Scenario"() {
        when:
        def workflow = "Somebody started DormantsLocInstantor workflow"()
        def client = workflow.toClient()

        then: "DormantsLocInstantor WF started"
        workflow.isActive()
        workflow.areOnlyActiveActivities(LocPreOfferDataPreparation, LocPreOfferCall)

        when: "wf execution started"
        workflow.runBeforeActivity(LocPreOfferCall)

        then: "offer data has prepared"
        workflow.areAllActivitiesCompleted(LocPreOfferDataPreparation, LocPreOffer, LocPreOfferEmail, LocPreOfferSMS)
        workflow.areOnlyActiveActivities(LocPreOfferCall)

        and: "client can see pre offer popup"
        popupService.getActual(client.clientId).stream().filter { popup -> popup.type == fintech.spain.alfa.product.web.model.PopupType.SPECIAL_OFFER }.findAny().isPresent()

        and: "LocPreOfferCallTask created"
        workflow.taskOfActivity(LocPreOfferCall).getTask().getTaskType() == LocPreOfferCallTask.TYPE

        and:
        "Client received pre offer SMS and Email"(client)

        when: "client got pre offer call and accepted it"
        workflow.taskOfActivity(LocPreOfferCall).complete(LocPreOfferCallTask.CLIENT_INTERESTED)

        then: "client can see Instantor form in UA"
        workflow.isActivityCompleted(LocPreOfferCall)
        workflow.areOnlyActiveActivities(LocInstantorForm)

        when: "client got stuck on Instantor form"
        workflowBackgroundJobs.run(TimeMachine.now() + D_25_MINUTES)

        then: "InstantorForm reminder task was created"
        workflow.hasTask(LocInstantorForm, LocPrestoReminderCallTask.INSTANTOR_FORM_REMINDER_TYPE)

        when: "client got saved instantor callback"
        workflow.runAfterActivity(LocInstantorCallback)

        then: "client can see Instantor Review form in UA"
        workflow.isActivityCompleted(LocInstantorCallback)
        workflow.areOnlyActiveActivities(LocInstantorReview)

        when: "client got stuck on Instantor Review"
        workflowBackgroundJobs.run(TimeMachine.now() + D_25_MINUTES)

        then: "InstantorReview reminder task was created"
        workflow.hasTask(LocInstantorReview, LocPrestoReminderCallTask.INSTANTOR_REVIEW_REMINDER_TYPE)

        when: "system prepared offer"
        workflow.runAfterActivity(LocLoanOffer)

        then: "all steps were completed"
        workflow.areAllActivitiesCompleted(
            LocInstantorForm,
            LocInstantorCallback,
            LocInstantorReview,
            LocInstantorRules,
            LocNordigen,
            LocCreditLimit,
            LocCreditLimitRules,
            LocPhoneValidationCall,
            LocLoanOffer
        )
        and:
        workflow.getActivity(LocInstantorManualCheck).getStatus() == ActivityStatus.WAITING

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

        when: 'Client was sent to presto'
        workflow.runBeforeActivity(LocSendToPrestoRedirect)
        workflowBackgroundJobs.run(TimeMachine.now() + D_25_MINUTES)

        then:
        workflow.areOnlyActiveActivities(LocSendToPrestoRedirect)
        workflow.hasTask(LocSendToPrestoRedirect, LocPrestoReminderCallTask.SET_PWD_REMINDER_TYPE)

        when:
        workflow.runAll()

        then:
        workflow.areAllActivitiesCompleted(LocSendToPresto, LocSendToPrestoRedirect)
        // Are you going to change number? Write tests!
        def locPrestoCallTask = workflow.taskOfActivity(LocSendToPrestoRedirect, LocPrestoReminderCallTask.SET_PWD_REMINDER_TYPE)
        locPrestoCallTask.isPresent()
        locPrestoCallTask.get().task.status == TaskStatus.CANCELLED
        workflow.getWorkflow().getActivities().size() == 20

        workflow.isCompleted()
    }

    @Unroll
    def "Pre Offer accepted via #preOfferBy And Offer Accepted via #offerBy"() {
        when:
        def workflow = "Somebody started DormantsLocInstantor workflow"()
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
        def workflow = "Somebody started DormantsLocInstantor workflow"()
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
        if (postpone)
            workflowBackgroundJobs.run(TimeMachine.now() + postpone)
        def task = workflow.taskOfActivity(activity)
        task.getTask().getTaskType() == taskType

        when:
        task.complete(taskResolution)

        then:
        workflow.getWorkflow().getStatus() == wfStatus

        where:
        activity                | taskType                                                 | postpone     | taskResolution                                        | wfStatus
        LocPreOfferCall         | LocPreOfferCallTask.TYPE                                 | null         | LocPreOfferCallTask.NO_ANSWER                         | WorkflowStatus.ACTIVE
        LocPreOfferCall         | LocPreOfferCallTask.TYPE                                 | null         | LocPreOfferCallTask.UNREACHABLE                       | WorkflowStatus.ACTIVE
        LocPreOfferCall         | LocPreOfferCallTask.TYPE                                 | null         | LocPreOfferCallTask.CLIENT_INTERESTED                 | WorkflowStatus.ACTIVE
        LocPreOfferCall         | LocPreOfferCallTask.TYPE                                 | null         | LocPreOfferCallTask.CLIENT_NOT_INTERESTED             | WorkflowStatus.TERMINATED
        LocPreOfferCall         | LocPreOfferCallTask.TYPE                                 | null         | LocPreOfferCallTask.EXPIRE                            | WorkflowStatus.EXPIRED
        LocInstantorManualCheck | InstantorManualCheckTask.TYPE                            | null         | InstantorManualCheckTask.POSTPONE                     | WorkflowStatus.ACTIVE
        LocInstantorManualCheck | InstantorManualCheckTask.TYPE                            | null         | InstantorManualCheckTask.REQUEST_RETRY                | WorkflowStatus.ACTIVE
        LocInstantorManualCheck | InstantorManualCheckTask.TYPE                            | null         | InstantorManualCheckTask.APPROVE                      | WorkflowStatus.ACTIVE
        LocInstantorManualCheck | InstantorManualCheckTask.TYPE                            | null         | InstantorManualCheckTask.REJECT                       | WorkflowStatus.TERMINATED
        LocInstantorManualCheck | InstantorManualCheckTask.TYPE                            | null         | InstantorManualCheckTask.EXPIRE                       | WorkflowStatus.EXPIRED
        LocInstantorForm        | LocPrestoReminderCallTask.INSTANTOR_FORM_REMINDER_TYPE   | D_25_MINUTES | LocPrestoReminderCallTask.POSTPONE                    | WorkflowStatus.ACTIVE
        LocInstantorForm        | LocPrestoReminderCallTask.INSTANTOR_FORM_REMINDER_TYPE   | D_25_MINUTES | LocPrestoReminderCallTask.DONE                        | WorkflowStatus.ACTIVE
        LocInstantorForm        | LocPrestoReminderCallTask.INSTANTOR_FORM_REMINDER_TYPE   | D_25_MINUTES | LocPrestoReminderCallTask.UNREACHABLE                 | WorkflowStatus.ACTIVE
        LocInstantorForm        | LocPrestoReminderCallTask.INSTANTOR_FORM_REMINDER_TYPE   | D_25_MINUTES | LocPrestoReminderCallTask.REJECT                      | WorkflowStatus.TERMINATED
        LocInstantorForm        | LocPrestoReminderCallTask.INSTANTOR_FORM_REMINDER_TYPE   | D_25_MINUTES | LocPrestoReminderCallTask.EXPIRE                      | WorkflowStatus.ACTIVE
        LocInstantorReview      | LocPrestoReminderCallTask.INSTANTOR_REVIEW_REMINDER_TYPE | D_25_MINUTES | LocPrestoReminderCallTask.POSTPONE                    | WorkflowStatus.ACTIVE
        LocInstantorReview      | LocPrestoReminderCallTask.INSTANTOR_REVIEW_REMINDER_TYPE | D_25_MINUTES | LocPrestoReminderCallTask.DONE                        | WorkflowStatus.ACTIVE
        LocInstantorReview      | LocPrestoReminderCallTask.INSTANTOR_REVIEW_REMINDER_TYPE | D_25_MINUTES | LocPrestoReminderCallTask.UNREACHABLE                 | WorkflowStatus.ACTIVE
        LocInstantorReview      | LocPrestoReminderCallTask.INSTANTOR_REVIEW_REMINDER_TYPE | D_25_MINUTES | LocPrestoReminderCallTask.REJECT                      | WorkflowStatus.TERMINATED
        LocInstantorReview      | LocPrestoReminderCallTask.INSTANTOR_REVIEW_REMINDER_TYPE | D_25_MINUTES | LocPrestoReminderCallTask.EXPIRE                      | WorkflowStatus.ACTIVE
        LocPhoneValidationCall  | LocPhoneValidationCallTask.TYPE                          | null         | LocPhoneValidationCallTask.POSTPONE                   | WorkflowStatus.ACTIVE
        LocPhoneValidationCall  | LocPhoneValidationCallTask.TYPE                          | null         | LocPhoneValidationCallTask.UNREACHABLE                | WorkflowStatus.ACTIVE
        LocPhoneValidationCall  | LocPhoneValidationCallTask.TYPE                          | null         | LocPhoneValidationCallTask.PHONE_IS_VALID             | WorkflowStatus.ACTIVE
        LocPhoneValidationCall  | LocPhoneValidationCallTask.TYPE                          | null         | LocPhoneValidationCallTask.PHONE_IS_INVALID           | WorkflowStatus.TERMINATED
        LocPhoneValidationCall  | LocPhoneValidationCallTask.TYPE                          | null         | LocPhoneValidationCallTask.EXPIRE                     | WorkflowStatus.EXPIRED
        LocApprovalCall         | LocApproveLoanOfferCallTask.TYPE                         | null         | LocApproveLoanOfferCallTask.POSTPONE                  | WorkflowStatus.ACTIVE
        LocApprovalCall         | LocApproveLoanOfferCallTask.TYPE                         | null         | LocApproveLoanOfferCallTask.UNREACHABLE               | WorkflowStatus.ACTIVE
        LocApprovalCall         | LocApproveLoanOfferCallTask.TYPE                         | null         | LocApproveLoanOfferCallTask.CLIENT_INTERESTED         | WorkflowStatus.ACTIVE
        LocApprovalCall         | LocApproveLoanOfferCallTask.TYPE                         | null         | LocApproveLoanOfferCallTask.CLIENT_REJECTED_OFFER     | WorkflowStatus.TERMINATED
        LocApprovalCall         | LocApproveLoanOfferCallTask.TYPE                         | null         | LocApproveLoanOfferCallTask.EXPIRE                    | WorkflowStatus.ACTIVE
        LocSendToPrestoRedirect | LocPrestoReminderCallTask.SET_PWD_REMINDER_TYPE          | D_25_MINUTES | LocPrestoReminderCallTask.POSTPONE                    | WorkflowStatus.ACTIVE
        LocSendToPrestoRedirect | LocPrestoReminderCallTask.SET_PWD_REMINDER_TYPE          | D_25_MINUTES | LocPrestoReminderCallTask.DONE                        | WorkflowStatus.ACTIVE
        LocSendToPrestoRedirect | LocPrestoReminderCallTask.SET_PWD_REMINDER_TYPE          | D_25_MINUTES | LocPrestoReminderCallTask.UNREACHABLE                 | WorkflowStatus.ACTIVE
        LocSendToPrestoRedirect | LocPrestoReminderCallTask.SET_PWD_REMINDER_TYPE          | D_25_MINUTES | LocPrestoReminderCallTask.REJECT                      | WorkflowStatus.TERMINATED
        LocSendToPrestoRedirect | LocPrestoReminderCallTask.SET_PWD_REMINDER_TYPE          | D_25_MINUTES | LocPrestoReminderCallTask.EXPIRE                      | WorkflowStatus.ACTIVE
    }

    @Unroll
    def "Expiration of #activity is #expiration with WF state #wfStatus"() {
        when:
        def workflow = "Somebody started DormantsLocInstantor workflow"()
        def client = workflow.toClient()

        if (activity == LocApproveLoanOffer) {
            workflow.setTaskResolution(LocApproveLoanOfferCallTask.TYPE, LocApproveLoanOfferCallTask.UNREACHABLE)
        }

        if (activity == LocInstantorManualCheck) {
            workflow.setInstantorResponseSupplier(INSTANTOR_RESPONSE_WITH_MISTAKE(client))
        }

        and:
        if (activity == LocPhoneValidationCall) {
            workflow.runBeforeActivity(LocPreOfferCall)
            "Client accepted pre offer via SMS"(client)
        }
        workflow.runBeforeActivity(activity)
        workflowBackgroundJobs.run(TimeMachine.now()
            .plus(expiration)
            .plusSeconds(30)
        )

        then:
        workflowService.getActivity(workflow.getActivity(activity).id)
        workflow.getActivity(activity).resolution == resolution
        workflow.getWorkflow().getStatus() == wfStatus


        where:
        activity                   | expiration       | resolution          | wfStatus
        LocPreOfferDataPreparation | _SYSTEM_ACTIVITY | Resolutions.OK      | WorkflowStatus.ACTIVE
        LocPreOffer                | _SYSTEM_ACTIVITY | Resolutions.OK      | WorkflowStatus.ACTIVE
        LocPreOfferEmail           | _SYSTEM_ACTIVITY | Resolutions.OK      | WorkflowStatus.ACTIVE
        LocPreOfferSMS             | _SYSTEM_ACTIVITY | Resolutions.OK      | WorkflowStatus.ACTIVE
        LocPreOfferCall            | D_72_HOURS       | Resolutions.EXPIRE  | WorkflowStatus.EXPIRED
        LocInstantorForm           | D_72_HOURS       | Resolutions.EXPIRE  | WorkflowStatus.EXPIRED
        LocInstantorCallback       | D_2_MINUTES      | Resolutions.EXPIRE  | WorkflowStatus.ACTIVE
        LocInstantorReview         | D_72_HOURS       | Resolutions.EXPIRE  | WorkflowStatus.EXPIRED
        LocInstantorRules          | _SYSTEM_ACTIVITY | Resolutions.APPROVE | WorkflowStatus.ACTIVE
        LocInstantorManualCheck    | D_72_HOURS       | Resolutions.EXPIRE  | WorkflowStatus.EXPIRED
//        LocNordigen                | _SYSTEM_ACTIVITY | Resolutions.OK      | WorkflowStatus.ACTIVE
        LocCreditLimit             | _SYSTEM_ACTIVITY | Resolutions.OK      | WorkflowStatus.ACTIVE
        LocPhoneValidationCall     | D_72_HOURS       | Resolutions.EXPIRE  | WorkflowStatus.EXPIRED
        LocLoanOffer               | _SYSTEM_ACTIVITY | Resolutions.OK      | WorkflowStatus.ACTIVE
        LocLoanOfferEmail          | _SYSTEM_ACTIVITY | Resolutions.OK      | WorkflowStatus.ACTIVE
        LocLoanOfferSMS            | _SYSTEM_ACTIVITY | Resolutions.OK      | WorkflowStatus.ACTIVE
        LocApprovalCall            | D_72_HOURS       | Resolutions.EXPIRE  | WorkflowStatus.EXPIRED
        LocApproveLoanOffer        | D_72_HOURS       | Resolutions.EXPIRE  | WorkflowStatus.EXPIRED
        LocSendToPresto            | _SYSTEM_ACTIVITY | Resolutions.OK      | WorkflowStatus.ACTIVE
    }

    @Unroll
    def "Instantor Rules #avgOutTxs #avgInTxs #resolution"() {
        when:
        def workflow = "Somebody started DormantsLocInstantor workflow"()
        def client = workflow.toClient()

        workflow.setInstantorResponseSupplier {
            InstantorSimulation.simulateOkResponseWithSingleAccount(
                client.clientId, dni(client), fullName(client), client.iban.toString(), avgInTxs, avgOutTxs
            )
        }

        and:
        workflow.runAll()

        then:
        workflow.getActivity(LocInstantorRules).resolution == resolution
        workflow.getWorkflow().getStatus() == wfStatus

        where:
        dni            | fullName            | avgOutTxs | avgInTxs | resolution          | wfStatus
        ORIGINAL_DNI   | ORIGINAL_FULLNAME   | 2500      | 600      | Resolutions.APPROVE | WorkflowStatus.COMPLETED
        ORIGINAL_DNI   | EMPTY_FULLNAME      | 2500      | 600      | Resolutions.MANUAL  | WorkflowStatus.COMPLETED
        SIMILAR_DNI    | ORIGINAL_FULLNAME   | 2500      | 600      | Resolutions.MANUAL  | WorkflowStatus.COMPLETED
        DISSIMILAR_DNI | ORIGINAL_FULLNAME   | 2500      | 600      | Resolutions.MANUAL  | WorkflowStatus.COMPLETED
        ORIGINAL_DNI   | SIMILAR_FULLNAME    | 2500      | 600      | Resolutions.MANUAL  | WorkflowStatus.COMPLETED
        ORIGINAL_DNI   | DISSIMILAR_FULLNAME | 2500      | 600      | Resolutions.MANUAL  | WorkflowStatus.COMPLETED
        ORIGINAL_DNI   | ORIGINAL_FULLNAME   | 1000      | 600      | Resolutions.APPROVE | WorkflowStatus.COMPLETED
        ORIGINAL_DNI   | ORIGINAL_FULLNAME   | 599       | 600      | Resolutions.REJECT  | WorkflowStatus.TERMINATED
        ORIGINAL_DNI   | ORIGINAL_FULLNAME   | 600       | 0        | Resolutions.APPROVE | WorkflowStatus.COMPLETED
    }

    def "Instantor Manual Check Retry Test [Good]"() {
        when:
        def workflow = "Somebody started DormantsLocInstantor workflow"()
        def client = workflow.toClient()

        workflow.setTaskResolution(InstantorManualCheckTask.TYPE, InstantorManualCheckTask.REQUEST_RETRY)

        and:
        workflow.setInstantorResponseSupplier(INSTANTOR_RESPONSE_WITH_MISTAKE(client))

        and: "client does two mistakes"
        2.times { workflow.runAll() }

        then: "Instantor steps were retried 2 times"
        workflow.getActivity(LocInstantorForm).attempts == 2
        workflow.getActivity(LocInstantorForm).resolution == Resolutions.INSTANTOR_FORM_COMPLETED

        workflow.getActivity(LocInstantorReview).attempts == 2
        workflow.getActivity(LocInstantorReview).resolution == Resolutions.OK

        workflow.getActivity(LocInstantorRules).attempts == 2
        workflow.getActivity(LocInstantorRules).resolution == Resolutions.MANUAL

        workflow.getActivity(LocInstantorManualCheck).attempts == 2
        workflow.getActivity(LocInstantorManualCheck).resolution == Resolutions.REQUEST_RETRY

        when: "client gets right Instantor answer"
        workflow.setInstantorResponseSupplier(INSTANTOR_RESPONSE_GOOD(client))
        workflow.runAll()

        then: "Workflow can be completed"
        workflow.getActivity(LocInstantorForm).attempts == 3
        workflow.getActivity(LocInstantorForm).resolution == Resolutions.INSTANTOR_FORM_COMPLETED

        workflow.getActivity(LocInstantorReview).attempts == 3
        workflow.getActivity(LocInstantorReview).resolution == Resolutions.OK

        workflow.getActivity(LocInstantorRules).attempts == 3
        workflow.getActivity(LocInstantorRules).resolution == Resolutions.APPROVE

        workflow.getActivity(LocInstantorManualCheck).attempts == 2
        workflow.getActivity(LocInstantorManualCheck).resolution == Resolutions.REQUEST_RETRY
        workflow.isCompleted()
    }

    def "Instantor Manual Check Retry Test [Bad]"() {
        when:
        def workflow = "Somebody started DormantsLocInstantor workflow"()
        def client = workflow.toClient()

        workflow.setTaskResolution(InstantorManualCheckTask.TYPE, InstantorManualCheckTask.REQUEST_RETRY)

        and:
        workflow.setInstantorResponseSupplier(INSTANTOR_RESPONSE_WITH_MISTAKE(client))

        and: "client does three mistakes"
        4.times { workflow.runAll() }

        then: "Instantor steps were retried 4 times"
        workflow.getActivity(LocInstantorForm).attempts == 4
        workflow.getActivity(LocInstantorForm).resolution == Resolutions.INSTANTOR_FORM_COMPLETED

        workflow.getActivity(LocInstantorReview).attempts == 4
        workflow.getActivity(LocInstantorReview).resolution == Resolutions.OK

        workflow.getActivity(LocInstantorRules).attempts == 4
        workflow.getActivity(LocInstantorRules).resolution == Resolutions.REJECT

        workflow.getActivity(LocInstantorManualCheck).attempts == 3
        workflow.getActivity(LocInstantorManualCheck).resolution == Resolutions.REQUEST_RETRY

        and: "Workflow should be terminated"
        workflow.isTerminated()
    }

    def "Cancel Instantor_Review step on event and terminate WF"() {
        when:
        def workflow = "Somebody started DormantsLocInstantor workflow"()
        def client = workflow.toClient()

        and:
        workflow.runBeforeActivity(LocInstantorReview)

        and:
        eventListeners.onInstantorCanceledByClient(new InstantorCanceledByClient(client.clientId))

        then:
        workflow.getActivity(LocInstantorReview).resolution == Resolutions.CANCEL
        workflow.workflow.status == WorkflowStatus.TERMINATED
        workflow.toApplication().application.status == LoanApplicationStatus.CLOSED
        workflow.toApplication().application.statusDetail == LoanApplicationStatusDetail.CANCELLED
    }

    def "Application approved after WF completion"() {
        when:
        def workflow = "Somebody started DormantsLocInstantor workflow"()

        and:
        workflow.runAll()

        then:
        workflow.workflow.status == WorkflowStatus.COMPLETED
        workflow.toApplication().application.status == LoanApplicationStatus.CLOSED
        workflow.toApplication().application.statusDetail == LoanApplicationStatusDetail.APPROVED
    }

    def "Client was sent to Presto"() {
        when:
        def workflow = "Somebody started DormantsLocInstantor workflow"()
        def client = workflow.toClient()

        and:
        workflow.runBeforeActivity(LocSendToPrestoRedirect)

        then:
        workflow.areAllActivitiesCompleted(LocSendToPresto)
        workflow.getAttribute(Attributes.LOC_REDIRECT_LINK).isPresent()
    }

    // Not implementen in "platform"
    @Ignore
    def "Instantor Review Form Retry Test"() {
        when:
        def workflow = "Somebody started DormantsLocInstantor workflow"()
        def client = workflow.toClient()

        and: "client retries requests 2 times"
        workflow.setAction(LocInstantorReview, {
            workflow.completeActivity(LocInstantorReview, Resolutions.REQUEST_RETRY)
        })
        3.times { workflow.runAll() }

        then: "Instantor steps were retried 3 times"
        workflow.getActivity(LocInstantorForm).attempts == 3
        workflow.getActivity(LocInstantorForm).resolution == Resolutions.INSTANTOR_FORM_COMPLETED

        workflow.getActivity(LocInstantorReview).attempts == 3
        workflow.getActivity(LocInstantorReview).resolution == Resolutions.REQUEST_RETRY

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
        def workflow = "Somebody started DormantsLocInstantor workflow"()
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
        activity                | duration         | type    | cmsTemplate                                      | prepare
        LocPreOfferEmail        | _SYSTEM_ACTIVITY | "email" | CmsSetup.LOC_PRE_OFFER_EMAIL                     | NOTHING
        LocPreOfferSMS          | _SYSTEM_ACTIVITY | "sms"   | CmsSetup.LOC_PRE_OFFER_SMS                       | NOTHING

        LocPreOfferCall         | D_72_HOURS       | "email" | CmsSetup.LOC_LOAN_APPLICATION_EXPIRED_EMAIL      | NOTHING
        LocPreOfferCall         | D_72_HOURS       | "sms"   | CmsSetup.LOC_LOAN_APPLICATION_EXPIRED_SMS        | NOTHING

        LocInstantorForm        | D_15_MINUTES     | "email" | CmsSetup.LOC_INSTANTOR_NOTIFICATION_EMAIL        | NOTHING
        LocInstantorForm        | D_15_MINUTES     | "sms"   | CmsSetup.LOC_INSTANTOR_NOTIFICATION_SMS          | NOTHING
        LocInstantorForm        | D_72_HOURS       | "email" | CmsSetup.LOC_LOAN_APPLICATION_EXPIRED_EMAIL      | NOTHING
        LocInstantorForm        | D_72_HOURS       | "sms"   | CmsSetup.LOC_LOAN_APPLICATION_EXPIRED_SMS        | NOTHING

        LocInstantorReview      | D_15_MINUTES     | "email" | CmsSetup.LOC_INSTANTOR_REVIEW_NOTIFICATION_EMAIL | NOTHING
        LocInstantorReview      | D_15_MINUTES     | "sms"   | CmsSetup.LOC_INSTANTOR_REVIEW_NOTIFICATION_SMS   | NOTHING
        LocInstantorReview      | D_72_HOURS       | "email" | CmsSetup.LOC_LOAN_APPLICATION_EXPIRED_EMAIL      | NOTHING
        LocInstantorReview      | D_72_HOURS       | "sms"   | CmsSetup.LOC_LOAN_APPLICATION_EXPIRED_SMS        | NOTHING

        LocInstantorRules       | _SYSTEM_ACTIVITY | "email" | CmsSetup.LOC_REJECTED_NOTIFICATION_EMAIL         | LOC_INSTANTOR_RULES_REJECT
        LocInstantorRules       | _SYSTEM_ACTIVITY | "sms"   | CmsSetup.LOC_REJECTED_NOTIFICATION_SMS           | LOC_INSTANTOR_RULES_REJECT
        LocInstantorRules       | _SYSTEM_ACTIVITY | "email" | CmsSetup.LOC_LOAN_APPLICATION_EXPIRED_EMAIL      | LOC_INSTANTOR_RULES_EXPIRE
        LocInstantorRules       | _SYSTEM_ACTIVITY | "sms"   | CmsSetup.LOC_LOAN_APPLICATION_EXPIRED_SMS        | LOC_INSTANTOR_RULES_EXPIRE

        LocInstantorManualCheck | _SYSTEM_ACTIVITY | "email" | CmsSetup.LOC_REJECTED_NOTIFICATION_EMAIL         | LOC_INSTANTOR_MANUAL_CHECK_REJECTED
        LocInstantorManualCheck | _SYSTEM_ACTIVITY | "sms"   | CmsSetup.LOC_REJECTED_NOTIFICATION_SMS           | LOC_INSTANTOR_MANUAL_CHECK_REJECTED
        LocInstantorManualCheck | D_72_HOURS       | "email" | CmsSetup.LOC_LOAN_APPLICATION_EXPIRED_EMAIL      | LOC_INSTANTOR_MANUAL_CHECK_POSTPONED
        LocInstantorManualCheck | D_72_HOURS       | "sms"   | CmsSetup.LOC_LOAN_APPLICATION_EXPIRED_SMS        | LOC_INSTANTOR_MANUAL_CHECK_POSTPONED

        LocCreditLimitRules     | _SYSTEM_ACTIVITY | "email" | CmsSetup.LOC_REJECTED_NOTIFICATION_EMAIL         | LOC_CREDIT_LIMIT_RULES_REJECTED
        LocCreditLimitRules     | _SYSTEM_ACTIVITY | "sms"   | CmsSetup.LOC_REJECTED_NOTIFICATION_SMS           | LOC_CREDIT_LIMIT_RULES_REJECTED

        LocPhoneValidationCall  | D_72_HOURS       | "email" | CmsSetup.LOC_LOAN_APPLICATION_EXPIRED_EMAIL      | LOC_PRE_OFFER_CALL_SKIPPED
        LocPhoneValidationCall  | D_72_HOURS       | "sms"   | CmsSetup.LOC_LOAN_APPLICATION_EXPIRED_SMS        | LOC_PRE_OFFER_CALL_SKIPPED

        LocLoanOfferEmail       | _SYSTEM_ACTIVITY | "email" | CmsSetup.LOC_LOAN_OFFER_EMAIL                    | NOTHING
        LocLoanOfferSMS         | _SYSTEM_ACTIVITY | "sms"   | CmsSetup.LOC_LOAN_OFFER_SMS                      | NOTHING

        LocApprovalCall         | D_72_HOURS       | "email" | CmsSetup.LOC_LOAN_APPLICATION_EXPIRED_EMAIL      | NOTHING
        LocApprovalCall         | D_72_HOURS       | "sms"   | CmsSetup.LOC_LOAN_APPLICATION_EXPIRED_SMS        | NOTHING

        LocApproveLoanOffer     | D_15_MINUTES     | "email" | CmsSetup.LOC_LOAN_OFFER_EMAIL_REMINDER           | NOTHING
        LocApproveLoanOffer     | D_15_MINUTES     | "sms"   | CmsSetup.LOC_LOAN_OFFER_SMS_REMINDER             | NOTHING
        LocApproveLoanOffer     | D_72_HOURS       | "email" | CmsSetup.LOC_LOAN_APPLICATION_EXPIRED_EMAIL      | NOTHING
        LocApproveLoanOffer     | D_72_HOURS       | "sms"   | CmsSetup.LOC_LOAN_APPLICATION_EXPIRED_SMS        | NOTHING

    }

    @Unroll
    def "Credit limit calculation checks : maxAllowed = #maxAllowed, coefficient = #coefficient, maxPaidPrincipal= #maxPaidPrincipal, expectedAmount: #expectedAmount"() {
        given:
        def client = fintech.spain.alfa.product.testing.TestFactory.newClient()
            .registerDirectly()
            .issueActiveLoan(maxPaidPrincipal, 30L, TimeMachine.today().minusDays(30))
            .repayAll(TimeMachine.today())
            .toClient()

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
        def client = fintech.spain.alfa.product.testing.TestFactory.newClient()
            .registerDirectly()
            .issueActiveLoan(maxPaidPrincipal, 30L, TimeMachine.today().minusDays(30))
            .repayAll(TimeMachine.today())
            .toClient()

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
        300.00     | 500.00     | 1.5         | 300.00           | Resolutions.APPROVE
        300.00     | 500.00     | 1.5         | 500.00           | Resolutions.APPROVE
        300.00     | 500.00     | 1.5         | 200.00           | Resolutions.APPROVE
        300.00     | 500.00     | 1.5         | 100.00           | Resolutions.REJECT
    }

    @Unroll
    def "Phone validation after #taskType"() {
        given:
        def workflow = "Somebody started DormantsLocInstantor workflow"()
        def client = workflow.toClient()

        when: "wf execution started"
        workflow.runBeforeActivity(activity)

        then:
        "client can see $activity form in UA"
        workflow.areOnlyActiveActivities(activity)

        when: "client got stuck"
        workflowBackgroundJobs.run(TimeMachine.now() + D_25_MINUTES)

        then:
        "$taskType task was created"
        workflow.hasTask(activity, taskType)
        workflow.taskOfActivity(activity).complete(taskResolution)

        when: "continue wf"
        workflow.runAfterActivity(LocPhoneValidationCall)

        then:
        "LocPhoneValidationCall is $phoneValidationStatus with resolution $phoneValidationResolution"
        workflow.getActivity(LocPhoneValidationCall).status == phoneValidationStatus
        workflow.getActivity(LocPhoneValidationCall).resolution == phoneValidationResolution

        where:
        activity           | taskType                                                 | taskResolution                        | phoneValidationStatus    | phoneValidationResolution
        LocInstantorForm   | LocPrestoReminderCallTask.INSTANTOR_FORM_REMINDER_TYPE   | LocPrestoReminderCallTask.DONE        | ActivityStatus.COMPLETED | Resolutions.SKIP
        LocInstantorForm   | LocPrestoReminderCallTask.INSTANTOR_FORM_REMINDER_TYPE   | LocPrestoReminderCallTask.UNREACHABLE | ActivityStatus.COMPLETED | Resolutions.ACCEPT
        LocInstantorForm   | LocPrestoReminderCallTask.INSTANTOR_FORM_REMINDER_TYPE   | LocPrestoReminderCallTask.REJECT      | ActivityStatus.CANCELLED | null
        LocInstantorReview | LocPrestoReminderCallTask.INSTANTOR_REVIEW_REMINDER_TYPE | LocPrestoReminderCallTask.DONE        | ActivityStatus.COMPLETED | Resolutions.SKIP
        LocInstantorReview | LocPrestoReminderCallTask.INSTANTOR_REVIEW_REMINDER_TYPE | LocPrestoReminderCallTask.UNREACHABLE | ActivityStatus.COMPLETED | Resolutions.ACCEPT
        LocInstantorReview | LocPrestoReminderCallTask.INSTANTOR_REVIEW_REMINDER_TYPE | LocPrestoReminderCallTask.REJECT      | ActivityStatus.CANCELLED | null
    }

    private fintech.spain.alfa.product.testing.TestClient newClient() {
        def client = fintech.spain.alfa.product.testing.TestFactory.newClient()
            .setDateOfBirth(TimeMachine.today().minusYears(36))
            .setAmount(1000.00)
            .signUp()

        client.toLoanWorkflow()
            .setInstantorResponse(InstantorSimulation.simulateOkResponseWithSingleAccount(client.clientId, client.dni, client.fullName(), client.iban.toString()))
            .runAll()
            .exportDisbursement()
            .toLoan()
            .repayAll(TimeMachine.today())

        return client
    }

    def "Somebody started DormantsLocInstantor workflow for "(fintech.spain.alfa.product.testing.TestClient client) {
        // run Qualify Loc with delay of more than 60 days from latest Instantor's response.
        TimeMachine.useFixedClockAt(LocalDateTime.now().plusDays(61))
        client.submitLineOfCreditAndStartWorkflow(2000.00, TimeMachine.now()).toDormantsQualifyLocWorkflow().runAll()
        def workflow = client.toWorkflow(LocInstantorWorkflows.DORMANTS_LOC_INSTANTOR, fintech.spain.alfa.product.testing.TestDormantsLocInstantorWorkflow.class)
        workflow.setInstantorResponseSupplier(INSTANTOR_RESPONSE_GOOD(client))
        return workflow
    }

    def "Somebody started DormantsLocInstantor workflow"() {
        def client = newClient()
        // run Qualify Loc with delay of more than 60 days from latest Instantor's response.
        TimeMachine.useFixedClockAt(LocalDateTime.now().plusDays(61))
        client.submitLineOfCreditAndStartWorkflow(2000.00, TimeMachine.now()).toDormantsQualifyLocWorkflow().runAll()
        def workflow = client.toWorkflow(LocInstantorWorkflows.DORMANTS_LOC_INSTANTOR, fintech.spain.alfa.product.testing.TestDormantsLocInstantorWorkflow.class)
        workflow.setInstantorResponseSupplier(INSTANTOR_RESPONSE_GOOD(client))
        return workflow
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
        def workflow = client.toWorkflow(LocInstantorWorkflows.DORMANTS_LOC_INSTANTOR, fintech.spain.alfa.product.testing.TestDormantsLocInstantorWorkflow.class)
        underwritingFacade.webApproveApplication(client.getClientId(), workflow.applicationId, "127.0.0.1")
    }

    def "Client accepted pre offer via SMS"(fintech.spain.alfa.product.testing.TestClient client) {
        def link = specialLinkService.findLink(byClientId(client.getClientId(), SpecialLinkType.LOC_SPECIAL_OFFER)).get()
        specialLinkService.activateLink(link.token)
    }

    def "Client accepted offer via SMS"(fintech.spain.alfa.product.testing.TestClient client) {
        def link = specialLinkService.findLink(byClientId(client.getClientId(), SpecialLinkType.LOC_SPECIAL_OFFER)).get()
        specialLinkService.activateLink(link.token)
    }

    def "Client accepted pre offer via Email"(fintech.spain.alfa.product.testing.TestClient client) {
        def link = specialLinkService.findLink(byClientId(client.getClientId(), SpecialLinkType.LOC_SPECIAL_OFFER)).get()
        specialLinkService.activateLink(link.token)
    }

    def "Client accepted offer via Email"(fintech.spain.alfa.product.testing.TestClient client) {
        def workflow = client.toWorkflow(LocInstantorWorkflows.DORMANTS_LOC_INSTANTOR, fintech.spain.alfa.product.testing.TestDormantsLocInstantorWorkflow.class)
        underwritingFacade.webApproveApplication(client.getClientId(), workflow.applicationId, "127.0.0.1")
    }

    def static INSTANTOR_RESPONSE_GOOD = { fintech.spain.alfa.product.testing.TestClient client ->
        return {
            InstantorSimulation.simulateOkResponseWithSingleAccount(client.clientId, client.dni, client.fullName(), client.iban.toString(), amount(1000), amount(3500))
        }
    }

    def static INSTANTOR_RESPONSE_WITH_MISTAKE = { fintech.spain.alfa.product.testing.TestClient client ->
        return {
            InstantorSimulation.simulateOkResponseWithSingleAccount(client.clientId, client.dni, client.fullName() + "O", client.iban.toString(), amount(1000), amount(3500))
        }
    }

    def static NOTHING = { fintech.spain.alfa.product.testing.TestDormantsLocInstantorWorkflow workflow -> }

    def static LOC_PRE_OFFER_CALL_SKIPPED = { fintech.spain.alfa.product.testing.TestDormantsLocInstantorWorkflow workflow ->
        workflow.setAction(LocPreOfferCall, { workflow.completeActivity(LocPreOfferCall, Resolutions.SKIP) })
    }

    def static LOC_INSTANTOR_RULES_EXPIRE = { fintech.spain.alfa.product.testing.TestDormantsLocInstantorWorkflow workflow ->
        workflow.setAction(LocInstantorRules, { workflow.completeActivity(LocInstantorRules, Resolutions.EXPIRE) })
    }

    def static LOC_INSTANTOR_RULES_REJECT = { fintech.spain.alfa.product.testing.TestDormantsLocInstantorWorkflow workflow ->
        workflow.setAction(LocInstantorRules, { workflow.completeActivity(LocInstantorRules, Resolutions.REJECT) })
    }

    def static LOC_CREDIT_LIMIT_RULES_REJECTED = { fintech.spain.alfa.product.testing.TestDormantsLocInstantorWorkflow workflow ->
        workflow.setAction(LocCreditLimitRules, { workflow.completeActivity(LocCreditLimitRules, Resolutions.REJECT) })
    }

    def static LOC_INSTANTOR_MANUAL_CHECK_POSTPONED = { fintech.spain.alfa.product.testing.TestDormantsLocInstantorWorkflow workflow ->
        workflow.setInstantorResponseSupplier(INSTANTOR_RESPONSE_WITH_MISTAKE.call(workflow.toClient()))
        workflow.setTaskResolution(InstantorManualCheckTask.TYPE, InstantorManualCheckTask.POSTPONE)
    }

    def static LOC_INSTANTOR_MANUAL_CHECK_REJECTED = { fintech.spain.alfa.product.testing.TestDormantsLocInstantorWorkflow workflow ->
        workflow.setInstantorResponseSupplier(INSTANTOR_RESPONSE_WITH_MISTAKE.call(workflow.toClient()))
        workflow.setTaskResolution(InstantorManualCheckTask.TYPE, InstantorManualCheckTask.REJECT)
    }

    def static ORIGINAL_DNI = { fintech.spain.alfa.product.testing.TestClient client -> client.dni }
    def static SIMILAR_DNI = { fintech.spain.alfa.product.testing.TestClient client -> StringUtils.join(client.dni, "O") }
    def static DISSIMILAR_DNI = { fintech.spain.alfa.product.testing.TestClient client -> fintech.spain.alfa.product.testing.RandomData.randomDni() }

    def static ORIGINAL_FULLNAME = { fintech.spain.alfa.product.testing.TestClient client -> client.fullName() }
    def static EMPTY_FULLNAME = { fintech.spain.alfa.product.testing.TestClient client -> "" }
    def static SIMILAR_FULLNAME = { fintech.spain.alfa.product.testing.TestClient client -> StringUtils.join(ImmutableList.of(client.firstName + "O", client.lastName, client.secondLastName)) }
    def static DISSIMILAR_FULLNAME = { fintech.spain.alfa.product.testing.TestClient client -> "Some Fake Full Name" }
}
