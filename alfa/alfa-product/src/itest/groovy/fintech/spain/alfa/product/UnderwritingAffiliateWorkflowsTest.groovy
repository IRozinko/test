package fintech.spain.alfa.product

import fintech.DateUtils
import fintech.JsonUtils
import fintech.TimeMachine
import fintech.decision.model.DecisionRequestStatus
import fintech.decision.model.DecisionResult
import fintech.decision.spi.MockDecisionEngine
import fintech.instantor.InstantorService
import fintech.instantor.InstantorSimulation
import fintech.instantor.model.InstantorResponseStatus
import fintech.iovation.IovationService
import fintech.iovation.model.SaveBlackboxCommand
import fintech.lending.core.application.LoanApplicationService
import fintech.lending.core.application.LoanApplicationStatus
import fintech.lending.core.application.LoanApplicationStatusDetail
import fintech.lending.core.application.db.LoanApplicationRepository
import fintech.lending.core.creditlimit.CreditLimitService
import fintech.lending.core.loan.LoanStatus
import fintech.notification.NotificationHelper
import fintech.settings.commands.UpdatePropertyCommand
import fintech.spain.alfa.product.cms.CmsSetup
import fintech.spain.alfa.product.settings.AlfaSettings
import fintech.spain.alfa.product.workflow.dormants.event.InstantorCanceledByClient

import fintech.task.TaskService
import fintech.task.command.AddTaskCommand
import fintech.task.command.CompleteTaskCommand
import fintech.task.command.ExpireTaskCommand
import fintech.task.command.PostponeTaskCommand
import fintech.task.model.TaskQuery
import fintech.task.model.TaskStatus
import fintech.task.spi.ExpiredTaskConsumer
import fintech.workflow.ActivityStatus
import fintech.workflow.WorkflowService
import fintech.workflow.WorkflowStatus
import fintech.workflow.impl.WorkflowBackgroundJobs
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.transaction.support.TransactionTemplate
import spock.lang.Unroll

import java.time.LocalDate

import static fintech.decision.spi.DecisionEngineStrategy.ID_VALIDATION_SCENARIO
import static fintech.spain.alfa.product.workflow.undewrtiting.UnderwritingWorkflows.Activities.*

class UnderwritingAffiliateWorkflowsTest extends AbstractAlfaTest {

    @Autowired
    WorkflowService workflowService

    @Autowired
    TaskService taskService

    @Autowired
    WorkflowBackgroundJobs workflowBackgroundJobs

    @Autowired
    fintech.spain.alfa.product.registration.RegistrationFacade registrationFacade

    @Autowired
    fintech.spain.alfa.product.lending.UnderwritingFacade underwritingFacade

    @Autowired
    fintech.spain.alfa.product.instantor.InstantorFacade instantorFacade

    @Autowired
    InstantorService instantorService

    @Autowired
    ExpiredTaskConsumer expiredTaskConsumer

    @Autowired
    NotificationHelper notificationHelper

    @Autowired
    CreditLimitService creditLimitService

    @Autowired
    LoanApplicationService loanApplicationService

    @Autowired
    IovationService iovationService

    @Autowired
    fintech.spain.alfa.product.workflow.common.WorkflowEventListeners eventListeners

    @Autowired
    fintech.spain.alfa.product.web.spi.PopupService popupService

    @Autowired
    TransactionTemplate txTemplate

    @Autowired
    LoanApplicationRepository loanApplicationRepository

    @Autowired
    MockDecisionEngine mockDecisionEngine

    def cleanup() {
        mockDecisionEngine.setResponseForScenario(ID_VALIDATION_SCENARIO, { ->
            new DecisionResult()
                .setDecision("Valid")
                .setStatus(DecisionRequestStatus.OK)
                .setScore(BigDecimal.TEN)
                .setUsedFields(Collections.emptyList())
                .setVariablesResult(new HashMap<>())
        })
    }

    def "Affiliates - Application form"() {
        when:
        def client = fintech.spain.alfa.product.testing.TestFactory.newAffiliateClient().randomEmailAndName("test").signUp()
        def workflow = client.toLoanAffiliateWorkflow()

        then:
        workflow.getWorkflow().name == fintech.spain.alfa.product.workflow.undewrtiting.UnderwritingWorkflows.FIRST_LOAN_AFFILIATE

        when:
        workflow.runBeforeActivity(APPLICATION_FORM)

        then:
        workflow.getActivity(APPLICATION_FORM).status == ActivityStatus.ACTIVE

        when:
        def tasks = taskService.findTasks(TaskQuery.byActivityId(workflow.getActivity(APPLICATION_FORM).id))

        then:
        tasks.size() == 0

        when:
        workflowBackgroundJobs.run(TimeMachine.now().plusMinutes(30))
        tasks = taskService.findTasks(TaskQuery.byActivityId(workflow.getActivity(APPLICATION_FORM).id))

        then:
        tasks.size() == 1

        with(tasks[0]) {
            taskType == fintech.spain.alfa.product.workflow.undewrtiting.UnderwritingTasks.ApplicationFormCall.TYPE
            status == TaskStatus.OPEN
            dueAt < TimeMachine.now().plusMinutes(30)
        }

        notificationHelper.countSms(client.clientId, CmsSetup.LOAN_APPLICATION_REMINDER_NOTIFICATION) == 1

        when:
        client.saveApplicationForm()

        and:
        tasks = taskService.findTasks(TaskQuery.byActivityId(workflow.getActivity(APPLICATION_FORM).id))

        then:
        workflow.getActivity(APPLICATION_FORM).status == ActivityStatus.COMPLETED

        tasks.size() == 1

        with(tasks[0]) {
            taskType == fintech.spain.alfa.product.workflow.undewrtiting.UnderwritingTasks.ApplicationFormCall.TYPE
            status == TaskStatus.CANCELLED
        }
    }

    def "Affiliates - Phone verification"() {
        when:
        def client = fintech.spain.alfa.product.testing.TestFactory.newAffiliateClient().randomEmailAndName("test").signUp()
        def workflow = client.toLoanAffiliateWorkflow()
        workflow.runBeforeActivity(PHONE_VERIFICATION)

        then:
        workflow.getActivity(PHONE_VERIFICATION).status == ActivityStatus.ACTIVE

        when:
        def tasks = taskService.findTasks(TaskQuery.byActivityId(workflow.getActivity(PHONE_VERIFICATION).id))

        then:
        tasks.size() == 0

        notificationHelper.countSms(client.clientId, CmsSetup.PHONE_VERIFICATION_NOTIFICATION) == 1

        when:
        client.verifyPhone()

        then:
        workflow.getActivity(PHONE_VERIFICATION).status == ActivityStatus.COMPLETED
    }

    def "Affiliates - Phone verification is skipped in case of repeated client"() {
        when:
        def client = fintech.spain.alfa.product.testing.TestFactory.newAffiliateClient()
            .signUp()
            .toLoanAffiliateWorkflow()
            .runAll()
            .exportDisbursement()
            .toLoan()
            .repayAll(TimeMachine.today())
            .toClient()

        and:
        def workflow = client.submitApplicationAndStartAffiliateWorkflow(300.00, 30, TimeMachine.today())
            .toLoanAffiliateWorkflow()
            .runAll()

        then:
        workflow.getActivityResolutionDetail(PHONE_VERIFICATION) == "AutoCompleted"
    }

    def "Affiliates - Phone verification is not skipped in case if client verified phone x months ago"() {
        when:
        TimeMachine.useFixedClockAt(LocalDate.now().minusMonths(12).minusDays(1))
        def client = fintech.spain.alfa.product.testing.TestFactory.newAffiliateClient()
            .signUp()
            .toLoanAffiliateWorkflow()
            .runAll()
            .exportDisbursement()
            .toLoan()
            .repayAll(TimeMachine.today())
            .toClient()

        and:
        TimeMachine.useDefaultClock()
        def workflow = client.submitApplicationAndStartAffiliateWorkflow(300.00, 30, TimeMachine.today())
            .toLoanAffiliateWorkflow()
            .runAfterActivity(PHONE_VERIFICATION)

        then:
        workflow.getActivityStatus(PHONE_VERIFICATION) == ActivityStatus.COMPLETED
        workflow.getActivityResolutionDetail(PHONE_VERIFICATION) == ""

    }

    def "Document form"() {
        when:
        def client = fintech.spain.alfa.product.testing.TestFactory.newAffiliateClient().randomEmailAndName("test").signUp()
        def workflow = client.toLoanAffiliateWorkflow()
        workflow.runBeforeActivity(DOCUMENT_FORM)

        then:
        workflow.getActivity(DOCUMENT_FORM).status == ActivityStatus.ACTIVE

        when:
        def tasks = taskService.findTasks(TaskQuery.byActivityId(workflow.getActivity(DOCUMENT_FORM).id))

        then:
        tasks.size() == 0

        when:
        workflowBackgroundJobs.run(TimeMachine.now().plusMinutes(30))

        then:
        notificationHelper.countSms(client.clientId, CmsSetup.CS_01_INSTANTOR_NOTIFICATION) == 1

        when:
        workflowBackgroundJobs.run(TimeMachine.now().plusDays(2))

        then:
        notificationHelper.countEmails(client.clientId, CmsSetup.CS_02_INSTANTOR_NOTIFICATION) == 1

        when:
        workflow.instantorFormCompleted()

        then:
        workflow.getActivity(DOCUMENT_FORM).status == ActivityStatus.COMPLETED
    }

    def "Affiliates - Document form - expire"() {
        when:
        def client = fintech.spain.alfa.product.testing.TestFactory.newAffiliateClient().randomEmailAndName("test").signUp()
        def workflow = client.toLoanAffiliateWorkflow()
        workflow.runBeforeActivity(DOCUMENT_FORM)

        then:
        workflow.getActivity(DOCUMENT_FORM).status == ActivityStatus.ACTIVE

        when:
        workflowBackgroundJobs.run(TimeMachine.now().plusHours(72 + 48))

        then:
        workflow.isExpired()
    }

    def "Affiliates - Instantor review - expire"() {
        when:
        def client = fintech.spain.alfa.product.testing.TestFactory.newAffiliateClient().signUp()
        def workflow = client.toLoanAffiliateWorkflow()
        workflow.runBeforeActivity(INSTANTOR_REVIEW)

        then:
        workflow.getActivity(INSTANTOR_REVIEW).status == ActivityStatus.ACTIVE

        when:
        workflowBackgroundJobs.run(TimeMachine.now().plusHours(72 + 48))

        then:
        workflow.isExpired()
    }


    def "Affiliates - Instantor review"() {
        when:
        def client = fintech.spain.alfa.product.testing.TestFactory.newAffiliateClient().randomEmailAndName("test").signUp()
        def workflow = client.toLoanAffiliateWorkflow()
        workflow.runBeforeActivity(INSTANTOR_REVIEW)

        then:
        workflow.getActivity(INSTANTOR_REVIEW).status == ActivityStatus.ACTIVE

        when:
        def tasks = taskService.findTasks(TaskQuery.byActivityId(workflow.getActivity(INSTANTOR_REVIEW).id))

        then:
        tasks.size() == 0

        when:
        workflowBackgroundJobs.run(TimeMachine.now().plusMinutes(30))

        then:
        notificationHelper.countSms(client.clientId, CmsSetup.CS_01_INSTANTOR_REVIEW_NOTIFICATION) == 1

        when:
        workflowBackgroundJobs.run(TimeMachine.now().plusDays(2))

        then:
        notificationHelper.countEmails(client.clientId, CmsSetup.CS_02_INSTANTOR_REVIEW_NOTIFICATION) == 1

        when:
        workflow.instantorReviewCompleted()

        then:
        workflow.getActivity(INSTANTOR_REVIEW).status == ActivityStatus.COMPLETED
    }

    @Unroll
    def "Affiliates - Instantor manual check - InstantorRules resolution = #instantorRulesResolution"() {
        when:
        def client = fintech.spain.alfa.product.testing.TestFactory.newAffiliateClient().randomEmailAndName("test").signUpWithApplication().createIdentificationDocument()
        def workflow = client.toLoanAffiliateWorkflow()
        workflow.runBeforeActivity(INSTANTOR_RULES)

        then:
        workflow.getActivity(INSTANTOR_RULES).status == ActivityStatus.ACTIVE

        when:
        workflow.completeActivity(INSTANTOR_RULES, instantorRulesResolution)

        then:
        workflow.runBeforeActivity(INSTANTOR_MANUAL_CHECK)
        workflow.getActivity(INSTANTOR_RULES).status == ActivityStatus.COMPLETED
        workflow.getActivityResolution(INSTANTOR_RULES) == instantorRulesResolution

        workflow.runSystemActivity(CHECK_VALID_ID_DOC)

        workflow.getActivity(INSTANTOR_MANUAL_CHECK).status == instantorManualCheckStatus
        workflow.getActivity(INSTANTOR_MANUAL_CHECK).resolution == instantorManualCheckResolution

        where:
        instantorRulesResolution | instantorManualCheckStatus | instantorManualCheckResolution
        fintech.spain.alfa.product.workflow.common.Resolutions.MANUAL  | ActivityStatus.ACTIVE    | null
        fintech.spain.alfa.product.workflow.common.Resolutions.APPROVE | ActivityStatus.COMPLETED | fintech.spain.alfa.product.workflow.common.Resolutions.SKIP
        fintech.spain.alfa.product.workflow.common.Resolutions.REJECT  | ActivityStatus.CANCELLED | null

    }

    @Unroll
    def "Affiliates - Instantor manual check - activity resolution"() {
        when:
        def client = fintech.spain.alfa.product.testing.TestFactory.newAffiliateClient().randomEmailAndName("test").signUpWithApplication().createIdentificationDocument()
        def workflow = client.toLoanAffiliateWorkflow()
        workflow.runBeforeActivity(INSTANTOR_RULES)

        then:
        workflow.getActivity(INSTANTOR_RULES).status == ActivityStatus.ACTIVE

        when:
        workflow.completeActivity(INSTANTOR_RULES, fintech.spain.alfa.product.workflow.common.Resolutions.MANUAL)
        workflow.runBeforeActivity(INSTANTOR_MANUAL_CHECK)

        then:
        workflow.getActivity(INSTANTOR_RULES).status == ActivityStatus.COMPLETED
        workflow.getActivityResolution(INSTANTOR_RULES) == fintech.spain.alfa.product.workflow.common.Resolutions.MANUAL

        workflow.runSystemActivity(CHECK_VALID_ID_DOC)

        workflow.getActivity(INSTANTOR_MANUAL_CHECK).status == ActivityStatus.ACTIVE
        notificationHelper.countEmails(client.clientId, CmsSetup.LOAN_ISSUE_IN_PROGRESS_NOTIFICATION) == 1

        when:
        def tasks = taskService.findTasks(TaskQuery.byActivityId(workflow.getActivity(INSTANTOR_MANUAL_CHECK).id))

        then:
        tasks.size() == 1
        with(tasks[0]) {
            taskType == fintech.spain.alfa.product.workflow.undewrtiting.UnderwritingTasks.InstantorManualCheckTask.TYPE
            status == TaskStatus.OPEN
        }

        when:
        workflow.taskOfActivity(INSTANTOR_MANUAL_CHECK).complete(taskResolution)

        then:
        workflow.workflow.status == workflowStatus
        workflow.getActivity(INSTANTOR_MANUAL_CHECK).status == ActivityStatus.COMPLETED
        workflow.getActivity(INSTANTOR_MANUAL_CHECK).resolution == activityResolution

        where:
        taskResolution                                        | activityResolution        | workflowStatus
        fintech.spain.alfa.product.workflow.undewrtiting.UnderwritingTasks.InstantorManualCheckTask.EXPIRE        | fintech.spain.alfa.product.workflow.common.Resolutions.EXPIRE        | WorkflowStatus.EXPIRED
        fintech.spain.alfa.product.workflow.undewrtiting.UnderwritingTasks.InstantorManualCheckTask.REQUEST_RETRY | fintech.spain.alfa.product.workflow.common.Resolutions.REQUEST_RETRY | WorkflowStatus.ACTIVE
        fintech.spain.alfa.product.workflow.undewrtiting.UnderwritingTasks.InstantorManualCheckTask.REJECT        | fintech.spain.alfa.product.workflow.common.Resolutions.REJECT        | WorkflowStatus.TERMINATED
        fintech.spain.alfa.product.workflow.undewrtiting.UnderwritingTasks.InstantorManualCheckTask.APPROVE       | fintech.spain.alfa.product.workflow.common.Resolutions.APPROVE       | WorkflowStatus.ACTIVE
    }

    def "Affiliates - Instantor manual check - expire task after postpone"() {
        when:
        TimeMachine.useFixedClockAt(LocalDate.of(2018, 5, 18))

        def client = fintech.spain.alfa.product.testing.TestFactory.newAffiliateClient().randomEmailAndName("test").signUpWithApplication().createIdentificationDocument()
        def workflow = client.toLoanAffiliateWorkflow()
        workflow.runBeforeActivity(INSTANTOR_RULES)

        then:
        workflow.getActivity(INSTANTOR_RULES).status == ActivityStatus.ACTIVE

        when:
        workflow.completeActivity(INSTANTOR_RULES, fintech.spain.alfa.product.workflow.common.Resolutions.MANUAL)
        workflow.runBeforeActivity(INSTANTOR_MANUAL_CHECK)

        then:
        workflow.getActivity(INSTANTOR_RULES).status == ActivityStatus.COMPLETED
        workflow.getActivityResolution(INSTANTOR_RULES) == fintech.spain.alfa.product.workflow.common.Resolutions.MANUAL
        workflow.runSystemActivity(CHECK_VALID_ID_DOC)
        workflow.getActivity(INSTANTOR_MANUAL_CHECK).status == ActivityStatus.ACTIVE
        notificationHelper.countEmails(client.clientId, CmsSetup.LOAN_ISSUE_IN_PROGRESS_NOTIFICATION) == 1

        when:
        def tasks = taskService.findTasks(TaskQuery.byActivityId(workflow.getActivity(INSTANTOR_MANUAL_CHECK).id))

        then:
        tasks.size() == 1
        with(tasks[0]) {
            taskType == fintech.spain.alfa.product.workflow.undewrtiting.UnderwritingTasks.InstantorManualCheckTask.TYPE
            status == TaskStatus.OPEN
        }

        when:
        def postponeTo = TimeMachine.now().plusDays(2)

        taskService.postponeTask(new PostponeTaskCommand(taskId: tasks[0].id, postponeTo: postponeTo, resolution: fintech.spain.alfa.product.workflow.undewrtiting.UnderwritingTasks.InstantorManualCheckTask.POSTPONE))

        and:
        tasks = taskService.findTasks(TaskQuery.byActivityId(workflow.getActivity(INSTANTOR_MANUAL_CHECK).id))

        then: "skip weekends"
        tasks.size() == 1
        with(tasks[0]) {
            taskType == fintech.spain.alfa.product.workflow.undewrtiting.UnderwritingTasks.InstantorManualCheckTask.TYPE
            status == TaskStatus.OPEN
            resolution == fintech.spain.alfa.product.workflow.undewrtiting.UnderwritingTasks.InstantorManualCheckTask.POSTPONE
        }

        when:
        postponeTo = postponeTo.plusDays(2)

        taskService.postponeTask(new PostponeTaskCommand(taskId: tasks[0].id, postponeTo: postponeTo, resolution: fintech.spain.alfa.product.workflow.undewrtiting.UnderwritingTasks.InstantorManualCheckTask.POSTPONE))

        and:
        tasks = taskService.findTasks(TaskQuery.byActivityId(workflow.getActivity(INSTANTOR_MANUAL_CHECK).id))

        then:
        tasks.size() == 1
        with(tasks[0]) {
            taskType == fintech.spain.alfa.product.workflow.undewrtiting.UnderwritingTasks.InstantorManualCheckTask.TYPE
            status == TaskStatus.OPEN
            resolution == fintech.spain.alfa.product.workflow.undewrtiting.UnderwritingTasks.InstantorManualCheckTask.POSTPONE
            dueAt == postponeTo
        }

        when:
        expiredTaskConsumer.consume(tasks[0].expiresAt.plusMinutes(1))

        then:
        workflow.isExpired()

        and:
        workflow.getActivity(INSTANTOR_MANUAL_CHECK).status == ActivityStatus.COMPLETED
        workflow.getActivity(INSTANTOR_MANUAL_CHECK).resolution == fintech.spain.alfa.product.workflow.common.Resolutions.EXPIRE
    }

    @Unroll
    def "Affiliates - Approve loan offer"() {
        when:
        def client = fintech.spain.alfa.product.testing.TestFactory.newAffiliateClient().randomEmailAndName("test").signUp()
        def workflow = client.toLoanAffiliateWorkflow()
        workflow.runBeforeActivity(APPROVE_LOAN_OFFER)

        then:
        workflow.getActivity(APPROVE_LOAN_OFFER).status == ActivityStatus.ACTIVE

        when:
        def tasks = taskService.findTasks(TaskQuery.byActivityId(workflow.getActivity(APPROVE_LOAN_OFFER).id))

        then:
        tasks.size() == 1

        with(tasks[0]) {
            taskType == fintech.spain.alfa.product.workflow.undewrtiting.UnderwritingTasks.LoanOfferCall.TYPE
            status == TaskStatus.OPEN
            dueAt < TimeMachine.now().plusMinutes(30)
        }

        when:
        workflowBackgroundJobs.run(TimeMachine.now().plusDays(1))

        then:
        notificationHelper.countSms(client.clientId, CmsSetup.APPROVE_LOAN_OFFER_REMINDER_NOTIFICATION) == 0
        notificationHelper.countEmails(client.clientId, CmsSetup.APPROVE_LOAN_OFFER_REMINDER_NOTIFICATION) == 1

        when:
        workflow.taskOfActivity(APPROVE_LOAN_OFFER).complete(taskResolution)

        then:
        workflow.workflow.status == workflowStatus
        workflow.getActivity(APPROVE_LOAN_OFFER).status == ActivityStatus.COMPLETED
        workflow.getActivity(APPROVE_LOAN_OFFER).resolution == activityResolution

        where:
        taskResolution                                        | activityResolution  | workflowStatus
        fintech.spain.alfa.product.workflow.undewrtiting.UnderwritingTasks.LoanOfferCall.EXPIRE                | fintech.spain.alfa.product.workflow.common.Resolutions.EXPIRE  | WorkflowStatus.EXPIRED
        fintech.spain.alfa.product.workflow.undewrtiting.UnderwritingTasks.LoanOfferCall.CLIENT_REJECTED_OFFER | fintech.spain.alfa.product.workflow.common.Resolutions.CANCEL  | WorkflowStatus.TERMINATED
        fintech.spain.alfa.product.workflow.undewrtiting.UnderwritingTasks.LoanOfferCall.CLIENT_APPROVED_OFFER | fintech.spain.alfa.product.workflow.common.Resolutions.APPROVE | WorkflowStatus.ACTIVE
    }

    def "Affiliates - expired phone verification notification check"() {
        when:
        def client = fintech.spain.alfa.product.testing.TestFactory.newAffiliateClient().randomEmailAndName("test").signUp()
        def workflow = client.toLoanAffiliateWorkflow()
        workflow.runBeforeActivity(PHONE_VERIFICATION)

        then:
        workflow.getActivity(PHONE_VERIFICATION).status == ActivityStatus.ACTIVE

        when:
        workflowBackgroundJobs.run(TimeMachine.now().plusHours(25))

        then:
        workflow.isExpired()

        and:
        workflow.getActivity(PHONE_VERIFICATION).status == ActivityStatus.COMPLETED

        and:
        notificationHelper.countSms(client.clientId, CmsSetup.PHONE_VERIFICATION_EXPIRED_NOTIFICATION) == 1
    }

    @Unroll
    def "Affiliates - #activity activity call - cancel application"() {
        when:
        def client = fintech.spain.alfa.product.testing.TestFactory.newAffiliateClient().randomEmailAndName("test").signUp()
        def workflow = client.toLoanAffiliateWorkflow()
        workflow.runBeforeActivity(activity)

        then:
        workflow.getActivity(activity).status == ActivityStatus.ACTIVE

        when:
        workflowBackgroundJobs.run(TimeMachine.now().plusMinutes(30))
        def tasks = taskService.findTasks(TaskQuery.byActivityId(workflow.getActivity(activity).id))

        then:
        tasks.size() == 1

        with(tasks[0]) {
            taskType == taskType
            status == TaskStatus.OPEN
        }

        when:
        taskService.completeTask(new CompleteTaskCommand(taskId: tasks[0].id, resolution: taskResolution))

        then:
        workflow.isTerminated()

        and:
        workflow.getActivity(activity).status == ActivityStatus.COMPLETED

        and:
        notificationHelper.countSms(client.clientId, CmsSetup.LOAN_APPLICATION_EXPIRED_NOTIFICATION) == 1
        notificationHelper.countEmails(client.clientId, CmsSetup.LOAN_APPLICATION_EXPIRED_NOTIFICATION) == 1

        where:
        activity               | taskType                                   | taskResolution
        APPLICATION_FORM       | fintech.spain.alfa.product.workflow.undewrtiting.UnderwritingTasks.ApplicationFormCall.TYPE | fintech.spain.alfa.product.workflow.undewrtiting.UnderwritingTasks.ApplicationFormCall.CLIENT_CANCELLED_APPLICATION
        APPLICATION_FORM       | fintech.spain.alfa.product.workflow.undewrtiting.UnderwritingTasks.ApplicationFormCall.TYPE | fintech.spain.alfa.product.workflow.undewrtiting.UnderwritingTasks.ApplicationFormCall.CANCEL_APPLICATION
    }

    @Unroll
    def "Affiliates - #activity activity call - task expired"() {
        when:
        def client = fintech.spain.alfa.product.testing.TestFactory.newAffiliateClient().randomEmailAndName("test").signUp()
        def workflow = client.toLoanAffiliateWorkflow()
        workflow.runBeforeActivity(activity)

        then:
        workflow.getActivity(activity).status == ActivityStatus.ACTIVE

        when:
        workflowBackgroundJobs.run(TimeMachine.now().plusMinutes(30))
        def tasks = taskService.findTasks(TaskQuery.byActivityId(workflow.getActivity(activity).id))

        then:
        tasks.size() == 1

        with(tasks[0]) {
            taskType == taskType
            status == TaskStatus.OPEN
        }

        when:
        taskService.expireTask(new ExpireTaskCommand(tasks[0].id))

        then:
        workflow.isExpired()

        and:
        workflow.getActivity(activity).status == ActivityStatus.COMPLETED
        workflow.getActivity(activity).resolution == fintech.spain.alfa.product.workflow.common.Resolutions.EXPIRE

        and:
        notificationHelper.countSms(client.clientId, CmsSetup.LOAN_APPLICATION_EXPIRED_NOTIFICATION) == 1
        notificationHelper.countEmails(client.clientId, CmsSetup.LOAN_APPLICATION_EXPIRED_NOTIFICATION) == 1

        where:
        activity               | taskType
        APPLICATION_FORM       | fintech.spain.alfa.product.workflow.undewrtiting.UnderwritingTasks.ApplicationFormCall.TYPE
        INSTANTOR_MANUAL_CHECK | fintech.spain.alfa.product.workflow.undewrtiting.UnderwritingTasks.InstantorManualCheckTask.TYPE
    }

    @Unroll
    def "Affiliates - Experian first run skipping"() {
        when:
        def client = fintech.spain.alfa.product.testing.TestFactory.newAffiliateClient().randomEmailAndName("test").signUp()
        def workflow = client.toLoanAffiliateWorkflow()
        workflow.runBeforeActivity(EXPERIAN_CAIS_RESUMEN_RUN_1)
        mockExperianCaisProvider.throwError = resumenError
        workflow.runSystemActivity(EXPERIAN_CAIS_RESUMEN_RUN_1)

        then:
        workflow.getActivity(EXPERIAN_CAIS_RESUMEN_RUN_1).status == ActivityStatus.COMPLETED
        workflow.getActivity(EXPERIAN_CAIS_RESUMEN_RUN_1).resolution == resumenResolution

        when:
        mockExperianCaisProvider.throwError = operacionesError
        workflow.runSystemActivity(EXPERIAN_CAIS_OPERACIONES_RUN_1)

        then:
        workflow.getActivity(EXPERIAN_CAIS_OPERACIONES_RUN_1).status == ActivityStatus.COMPLETED
        workflow.getActivity(EXPERIAN_CAIS_OPERACIONES_RUN_1).resolution == operacionesResolution

        when:
        workflow.runSystemActivity(EXPERIAN_RULES_RUN_1)

        then:
        workflow.getActivity(EXPERIAN_RULES_RUN_1).status == rulesStatus
        workflow.getActivity(EQUIFAX_RUN_1).status == ActivityStatus.ACTIVE

        where:
        resumenError | operacionesError | resumenResolution | operacionesResolution | rulesStatus
        true         | false            | fintech.spain.alfa.product.workflow.common.Resolutions.SKIP | fintech.spain.alfa.product.workflow.common.Resolutions.OK   | ActivityStatus.WAITING
        false        | true             | fintech.spain.alfa.product.workflow.common.Resolutions.OK   | fintech.spain.alfa.product.workflow.common.Resolutions.SKIP | ActivityStatus.WAITING
        true         | true             | fintech.spain.alfa.product.workflow.common.Resolutions.SKIP | fintech.spain.alfa.product.workflow.common.Resolutions.SKIP | ActivityStatus.WAITING
        false        | false            | fintech.spain.alfa.product.workflow.common.Resolutions.OK   | fintech.spain.alfa.product.workflow.common.Resolutions.OK   | ActivityStatus.COMPLETED
    }

    def "Affiliates - Repeated application - auto complete workflow steps"() {
        when:
        def client = fintech.spain.alfa.product.testing.TestFactory.newAffiliateClient()
            .signUp()
            .toLoanAffiliateWorkflow()
            .runAll()
            .exportDisbursement()
            .toLoan()
            .repayAll(TimeMachine.today())
            .toClient()

        and:
        def workflow = client.submitApplicationAndStartAffiliateWorkflow(300.00, 30, TimeMachine.today())
            .toLoanAffiliateWorkflow()
            .runAll()

        then:
        !workflow.getActivityResolutionDetail(MANDATORY_LENDING_RULES)
        workflow.getActivityResolution(MANDATORY_LENDING_RULES) == fintech.spain.alfa.product.workflow.common.Resolutions.APPROVE
        workflow.getActivityResolutionDetail(INSTANTOR_CALLBACK) == "AutoCompleted"
        workflow.getActivityResolutionDetail(INSTANTOR_REVIEW) == "AutoCompleted"
        workflow.getActivityResolutionDetail(INSTANTOR_RULES) == "AutoCompleted"
        !workflow.getActivityResolutionDetail(CREDIT_LIMIT)
    }

    def "Affiliates - Repeated application - not auto complete Iovation steps"() {
        when:
        def client = fintech.spain.alfa.product.testing.TestFactory.newAffiliateClient()
            .signUp()
            .toLoanAffiliateWorkflow()
            .runAll()
            .exportDisbursement()
            .toLoan()
            .repayAll(TimeMachine.today())
            .toClient()

        and:
        def workflow = client.submitApplicationAndStartAffiliateWorkflow(300.00, 30, TimeMachine.today())
            .toLoanAffiliateWorkflow()
            .runAll()

        then:

        workflow.getActivityResolutionDetail(IOVATION_BLACKBOX_RUN_1) != "AutoCompleted"
        workflow.getActivityResolutionDetail(IOVATION_RUN_1) != "AutoCompleted"
    }

    def "Affiliates - Repeated application - auto complete Iovation rules for repeated client"() {
        when:
        def client = fintech.spain.alfa.product.testing.TestFactory.newAffiliateClient()
            .signUp()
            .toLoanAffiliateWorkflow()
            .runAll()
            .exportDisbursement()
            .toLoan()
            .repayAll(TimeMachine.today())
            .toClient()

        and:
        def workflow = client.submitApplicationAndStartAffiliateWorkflow(300.00, 30, TimeMachine.today())
            .toLoanAffiliateWorkflow()
            .runAll()

        then:
        workflow.getActivityResolution(IOVATION_RULES_RUN_1) == fintech.spain.alfa.product.workflow.common.Resolutions.APPROVE
        workflow.getActivityResolutionDetail(IOVATION_RULES_RUN_1) == "AutoCompleted"
    }

    def "Affiliates - Good client - skip instantor, credit limit"() {
        given:
        def settings = settingsService.getJson(
                AlfaSettings.CREDIT_LIMIT_SETTINGS, AlfaSettings.CreditLimitSettings.class)
        TimeMachine.useFixedClockAt(TimeMachine.today().minusDays(91))

        def client = fintech.spain.alfa.product.testing.TestFactory.newAffiliateClient()
            .withCreditLimit(600.00)
            .signUp()

        client
            .toLoanAffiliateWorkflow()
            .runAll()
            .exportDisbursement()
            .toLoan()
            .repayAll(TimeMachine.today())

        3.times {
            client
                .submitApplicationAndStartAffiliateWorkflow(1000.00, 30, TimeMachine.today())
                .toLoanAffiliateWorkflow()
                .runAll()
                .exportDisbursement()
                .toLoan()
                .repayAll(TimeMachine.today())
        }

        TimeMachine.useDefaultClock()

        when:
        def workflow = client
            .submitApplicationAndStartAffiliateWorkflow(1_000_000.00, 30, TimeMachine.today())
            .toLoanAffiliateWorkflow()
            .runAll()

        then:
        workflow.getActivityResolutionDetail(INSTANTOR_CALLBACK) == "AutoCompleted"
        workflow.getActivityResolutionDetail(INSTANTOR_REVIEW) == "AutoCompleted"
        workflow.getActivityResolutionDetail(INSTANTOR_RULES) == "AutoCompleted"
        workflow.getActivityResolutionDetail(BASIC_LENDING_RULES) == "AutoCompleted"
        !workflow.getActivityResolutionDetail(MANDATORY_LENDING_RULES)
        workflow.getActivityResolution(MANDATORY_LENDING_RULES) == fintech.spain.alfa.product.workflow.common.Resolutions.APPROVE
        !workflow.getActivityResolutionDetail(CREDIT_LIMIT)

        when:
        def loan = workflow
            .exportDisbursement()
            .toLoan()

        then:
        loan.getBalance().getPrincipalDue() == settings.maxCreditLimit

        when:
        def creditLimit = creditLimitService.getClientLimit(client.clientId, TimeMachine.today())

        then:
        creditLimit.get().limit == settings.maxCreditLimit
    }

    def "Affiliates - Good client - not skip Equifax and Experian"() {
        given:
        TimeMachine.useFixedClockAt(TimeMachine.today().minusDays(91))

        def client = fintech.spain.alfa.product.testing.TestFactory.newAffiliateClient()
            .signUp()

        client
            .toLoanAffiliateWorkflow()
            .runAll()
            .exportDisbursement()
            .toLoan()
            .repayAll(TimeMachine.today())

        3.times {
            client
                .submitApplicationAndStartAffiliateWorkflow(1000.00, 30, TimeMachine.today())
                .toLoanAffiliateWorkflow()
                .runAll()
                .exportDisbursement()
                .toLoan()
                .repayAll(TimeMachine.today())
        }

        TimeMachine.useDefaultClock()

        when:
        def workflow = client
            .submitApplicationAndStartAffiliateWorkflow(1_000_000.00, 30, TimeMachine.today())
            .toLoanAffiliateWorkflow()
            .runAll()

        then:
        workflow.getActivityResolutionDetail(EXPERIAN_RULES_RUN_1) != "AutoCompleted"
        workflow.getActivityResolutionDetail(EXPERIAN_RULES_RUN_2) != "AutoCompleted"
        workflow.getActivityResolutionDetail(EQUIFAX_RULES_RUN_1) != "AutoCompleted"
        workflow.getActivityResolutionDetail(EQUIFAX_RULES_RUN_2) != "AutoCompleted"
    }

    def "Affiliates - Dont autocomplete instantor steps after rejected application"() {
        given:
        def client = fintech.spain.alfa.product.testing.TestFactory.newAffiliateClient()

        when:
        def workflow = client
            .signUp()
            .toLoanAffiliateWorkflow()
            .runBeforeActivity(APPROVE_LOAN_OFFER)
            .completeActivity(APPROVE_LOAN_OFFER, fintech.spain.alfa.product.workflow.common.Resolutions.REJECT)

        then:
        workflow.isTerminated()
        loanApplicationService.get(workflow.applicationId).status == LoanApplicationStatus.CLOSED
        loanApplicationService.get(workflow.applicationId).statusDetail == LoanApplicationStatusDetail.REJECTED

        when:
        workflow = client
            .setDateOfBirth(TimeMachine.today().minusYears(30))
            .submitApplicationAndStartAffiliateWorkflow(500.00, 30, TimeMachine.today())
            .toLoanAffiliateWorkflow()
            .runAll()

        then:
        workflow.getActivityResolutionDetail(EQUIFAX_RUN_1) == "AutoCompleted"
        !workflow.getActivityResolutionDetail(EQUIFAX_RULES_RUN_1)
        workflow.getActivityResolutionDetail(EXPERIAN_CAIS_RESUMEN_RUN_1) == "AutoCompleted"
        workflow.getActivityResolutionDetail(EXPERIAN_CAIS_OPERACIONES_RUN_1) == "AutoCompleted"
        !workflow.getActivityResolutionDetail(EXPERIAN_RULES_RUN_1)
        !workflow.getActivityResolutionDetail(DOCUMENT_FORM)
        !workflow.getActivityResolutionDetail(INSTANTOR_CALLBACK)
        !workflow.getActivityResolutionDetail(INSTANTOR_REVIEW)
        !workflow.getActivityResolutionDetail(INSTANTOR_RULES)
    }

    def "Affiliates - Dont autocomplete instantor steps for client without primary account"() {
        given:
        TimeMachine.useFixedClockAt(TimeMachine.today().minusDays(89))

        def client = fintech.spain.alfa.product.testing.TestFactory.newAffiliateClient()
            .signUp()

        client
            .toLoanAffiliateWorkflow()
            .runAll()
            .exportDisbursement()
            .toLoan()
            .repayAll(TimeMachine.today())

        client
            .submitApplicationAndStartAffiliateWorkflow(1000.00, 30, TimeMachine.today())
            .toLoanAffiliateWorkflow()
            .runAll()
            .exportDisbursement()
            .toLoan()
            .repayAll(TimeMachine.today())

        client.deactivatePrimaryBankAccount()

        TimeMachine.useDefaultClock()

        when:
        def workflow = client
            .submitApplicationAndStartAffiliateWorkflow(1_000_000.00, 30, TimeMachine.today())
            .toLoanAffiliateWorkflow()
            .runAll()

        then:
        workflow.getActivityResolutionDetail(DOCUMENT_FORM) == "AutoCompleted"
        workflow.getActivityResolutionDetail(INSTANTOR_CALLBACK) == "AutoCompleted"
        workflow.getActivityResolutionDetail(INSTANTOR_REVIEW) != "AutoCompleted"
        workflow.getActivityResolutionDetail(INSTANTOR_RULES) == "AutoCompleted"
        !workflow.getActivityResolutionDetail(CREDIT_LIMIT)
    }

    def "Affiliates - Apply UpsellAvailable attribute"() {
        given:
        def client = fintech.spain.alfa.product.testing.TestFactory.newAffiliateClient()
            .withCreditLimit(1000.00)
            .setDateOfBirth(TimeMachine.today().minusYears(36))
            .setAmount(1000.00)
            .signUp()

        client
            .toLoanAffiliateWorkflow()
            .runAll()
            .exportDisbursement()
            .toLoan()
            .repayAll(TimeMachine.today())
            .toClient()

        2.times {
            client
                .submitApplicationAndStartAffiliateWorkflow(1000.00, 30, TimeMachine.today())
                .toLoanAffiliateWorkflow()
                .runAll()
                .exportDisbursement()
                .toLoan()
                .repayAll(TimeMachine.today())
        }

        when:
        def workflow = client
            .submitApplicationAndStartAffiliateWorkflow(100.00, 30, TimeMachine.today())
            .toLoanAffiliateWorkflow()
            .runAll()

        then:
        workflow.workflow.hasAttribute(fintech.spain.alfa.product.workflow.common.Attributes.UPSELL_AVAILABLE)
    }

    def "Affiliates - workflow uses only successful previous instantor response"() {
        given:
        TimeMachine.useFixedClockAt(TimeMachine.today().minusDays(89))

        def client = fintech.spain.alfa.product.testing.TestFactory.newAffiliateClient()
            .signUp()

        client.toLoanAffiliateWorkflow().runAll()
            .exportDisbursement()
            .toLoan()
            .repayAll(TimeMachine.today())
        instantorService.saveResponse(InstantorSimulation.simulateFailResponse(client.clientId))

        client.submitApplicationAndStartAffiliateWorkflow(1000.00, 30, TimeMachine.today())
        TimeMachine.useDefaultClock()

        when:
        def responseId = client.toLoanAffiliateWorkflow().workflow.attribute(fintech.spain.alfa.product.workflow.common.Attributes.INSTANTOR_RESPONSE_ID)

        then:
        responseId
        instantorService.getResponse(Long.valueOf(responseId)).status != InstantorResponseStatus.FAILED

    }

    def "Affiliates - workflow uses only successful previous instantor response of current client's bank account"() {
        given:
        TimeMachine.useFixedClockAt(TimeMachine.today().minusDays(89))

        def client = fintech.spain.alfa.product.testing.TestFactory.newAffiliateClient()
            .signUp()

        client.toLoanAffiliateWorkflow().runAll()
            .exportDisbursement()
            .toLoan()
            .repayAll(TimeMachine.today())
        instantorService.saveResponse(InstantorSimulation.simulateFailResponse(client.clientId))

        when:
        client.addPrimaryBankAccount(fintech.spain.alfa.product.testing.RandomData.randomIban().toString())

        and:
        client.submitApplicationAndStartAffiliateWorkflow(1000.00, 30, TimeMachine.today())
        TimeMachine.useDefaultClock()

        then:
        !client.toLoanAffiliateWorkflow().workflow.hasAttribute(fintech.spain.alfa.product.workflow.common.Attributes.INSTANTOR_RESPONSE_ID)
    }

    def "Affiliates - workflow uses previous instantor response only if last application wasnt rejected"() {
        given:

        def client = fintech.spain.alfa.product.testing.TestFactory.newAffiliateClient()
            .registerDirectly()

        when:
        def loan = client
            .issueActiveLoan(300.00, 30, TimeMachine.today())

        def workflow = client
            .submitApplicationAndStartAffiliateWorkflow(300.00, 30, TimeMachine.today())
            .toLoanAffiliateWorkflow()
            .runAll()

        then:
        workflow.toApplication().isRejected()

        when:
        loan.repayAll(TimeMachine.today())
        instantorService.saveResponse(InstantorSimulation.simulateOkResponse(client.clientId, client.client.documentNumber,
            client.client.firstName, "ES1793 017144274569123112", "ES1793 017144274569123113"))

        client.submitApplicationAndStartAffiliateWorkflow(300.00, 30, TimeMachine.today())

        then:
        !client.toLoanAffiliateWorkflow().workflow.hasAttribute(fintech.spain.alfa.product.workflow.common.Attributes.INSTANTOR_RESPONSE_ID)

        when:
        client.toLoanAffiliateWorkflow().runAll()
        client.toLoanAffiliateWorkflow().toLoan()
            .exportDisbursements(TimeMachine.today())
            .repayAll(TimeMachine.today())

        client.submitApplicationAndStartAffiliateWorkflow(300.00, 30, TimeMachine.today())

        then:
        client.toLoanAffiliateWorkflow().workflow.hasAttribute(fintech.spain.alfa.product.workflow.common.Attributes.INSTANTOR_RESPONSE_ID)

    }

    def "Affiliates - Iovation steps of run_1 run just after 'CollectBasicInformation' step "() {

        given:
        def client = fintech.spain.alfa.product.testing.TestFactory.newAffiliateClient()

        when:
        def workflow = client
            .signUp()
            .toLoanAffiliateWorkflow()
            .runBeforeActivity(COLLECT_BASIC_INFORMATION)

        then:
        workflow.getActivityStatus(COLLECT_BASIC_INFORMATION) == ActivityStatus.ACTIVE
        workflow.getActivityStatus(IOVATION_BLACKBOX_RUN_1) == ActivityStatus.WAITING
        workflow.getActivityStatus(IOVATION_RUN_1) == ActivityStatus.WAITING
        workflow.getActivityStatus(IOVATION_CHECK_REPEATED_RUN_1) == ActivityStatus.WAITING
        workflow.getActivityStatus(IOVATION_RULES_RUN_1) == ActivityStatus.WAITING

        when:
        workflow.runAfterActivity(IOVATION_RULES_RUN_1)

        then:
        workflow.getActivityStatus(COLLECT_BASIC_INFORMATION) == ActivityStatus.COMPLETED
        workflow.getActivityStatus(IOVATION_BLACKBOX_RUN_1) == ActivityStatus.COMPLETED
        workflow.getActivityStatus(IOVATION_RUN_1) == ActivityStatus.COMPLETED
        workflow.getActivityStatus(IOVATION_CHECK_REPEATED_RUN_1) == ActivityStatus.COMPLETED
        workflow.getActivityStatus(IOVATION_RULES_RUN_1) == ActivityStatus.COMPLETED
    }

    @Unroll
    def "Affiliates - Instantor help call - activity resolution"() {
        when:
        def client = fintech.spain.alfa.product.testing.TestFactory.newAffiliateClient().randomEmailAndName("test").signUp()

        and:
        def workflow = client.toLoanAffiliateWorkflow()

        and:
        workflow.runBeforeActivity(activity)

        and:
        taskService.addTask(new AddTaskCommand(clientId: client.clientId, applicationId: workflow.applicationId, activityId: workflow.getActivity(activity).id, type: fintech.spain.alfa.product.workflow.undewrtiting.UnderwritingTasks.InstantorHelpCall.TYPE, dueAt: TimeMachine.now(), expiresAt: TimeMachine.now().plusDays(2)))

        then:
        workflow.getActivity(activity).status == ActivityStatus.ACTIVE

        when:
        def tasks = taskService.findTasks(TaskQuery.byActivityId(workflow.getActivity(activity).id))

        then:
        tasks.size() == 1

        with(tasks[0]) {
            taskType == fintech.spain.alfa.product.workflow.undewrtiting.UnderwritingTasks.InstantorHelpCall.TYPE
            status == TaskStatus.OPEN
        }

        when:
        workflow.taskOfActivity(activity).complete(taskResolution)

        then:
        workflow.isTerminated()
        workflow.getActivity(activity).status == ActivityStatus.COMPLETED
        workflow.getActivity(activity).resolution == fintech.spain.alfa.product.workflow.common.Resolutions.CANCEL

        where:
        activity         | taskResolution
        DOCUMENT_FORM    | fintech.spain.alfa.product.workflow.undewrtiting.UnderwritingTasks.InstantorHelpCall.CLIENT_CANCELLED_APPLICATION
        DOCUMENT_FORM    | fintech.spain.alfa.product.workflow.undewrtiting.UnderwritingTasks.InstantorHelpCall.CANCEL_APPLICATION
        INSTANTOR_REVIEW | fintech.spain.alfa.product.workflow.undewrtiting.UnderwritingTasks.InstantorHelpCall.CLIENT_CANCELLED_APPLICATION
        INSTANTOR_REVIEW | fintech.spain.alfa.product.workflow.undewrtiting.UnderwritingTasks.InstantorHelpCall.CANCEL_APPLICATION
    }

    def "Affiliates - Instantor failures handling"() {
        given:
        def client = fintech.spain.alfa.product.testing.TestFactory.newAffiliateClient().randomEmailAndName("test").signUp()
        def workflow = client.toLoanAffiliateWorkflow()

        when:
        workflow.runBeforeActivity(DOCUMENT_FORM)
        int maxAttempts = settingsService.getJson(INTEGRATION_SETTINGS, AlfaSettings.IntegrationSettings.class).getInstantor().getMaxAttempts()

        for (def i in 1..maxAttempts - 1) {

            assert workflow.getActivity(DOCUMENT_FORM).status == ActivityStatus.ACTIVE

            when:
            def responseId = instantorService.saveResponse(InstantorSimulation.simulateFailResponse(client.clientId))
            instantorService.processResponse(responseId)

            then:
            assert workflow.getActivity(DOCUMENT_FORM).status == ActivityStatus.COMPLETED

            when:
            workflow.runSystemActivity(INSTANTOR_CALLBACK)

            then:
            assert workflow.getActivity(INSTANTOR_CALLBACK).status == ActivityStatus.COMPLETED
            assert workflow.getActivity(INSTANTOR_CALLBACK).resolution == fintech.spain.alfa.product.workflow.common.Resolutions.REQUEST_RETRY
        }

        then:
        workflow.getActivity(DOCUMENT_FORM).status == ActivityStatus.ACTIVE
        def responseId = instantorService.saveResponse(InstantorSimulation.simulateFailResponse(client.clientId))
        instantorService.processResponse(responseId)

        then:
        workflow.getActivity(DOCUMENT_FORM).status == ActivityStatus.COMPLETED

        workflow.runSystemActivity(INSTANTOR_CALLBACK)
        with(workflow.getActivity(INSTANTOR_CALLBACK)) {
            status == ActivityStatus.COMPLETED
            resolution == fintech.spain.alfa.product.workflow.common.Resolutions.FAIL
        }

        and:
        workflow.toApplication().application.status == LoanApplicationStatus.CLOSED
        workflow.toApplication().application.statusDetail == LoanApplicationStatusDetail.CANCELLED
        workflow.toApplication().application.closeReason == "instantorMaxAttempts"
    }

    def "Affiliates - Cancel Instantor_Review step on event and terminate WF"() {
        given:
        def client = fintech.spain.alfa.product.testing.TestFactory.newAffiliateClient()

        when:
        def workflow = client
            .signUp()
            .toLoanAffiliateWorkflow()
            .runBeforeActivity(INSTANTOR_REVIEW)

        and:
        eventListeners.onInstantorCanceledByClient(new InstantorCanceledByClient(client.clientId))

        then:
        workflow.getActivity(INSTANTOR_REVIEW).resolution == fintech.spain.alfa.product.workflow.common.Resolutions.CANCEL
        workflow.isTerminated()
        workflow.toApplication().application.status == LoanApplicationStatus.CLOSED
        workflow.toApplication().application.statusDetail == LoanApplicationStatusDetail.CANCELLED
    }


    def "Affiliates - Cancel PrestoCrossCheckRules step on event and terminate WF"() {
        given:
        def client = fintech.spain.alfa.product.testing.TestFactory.newAffiliateClient()

        when:
        def workflow = client
            .signUp()
            .toLoanAffiliateWorkflow()
            .runBeforeActivity(PRESTO_CROSSCHECK_RULES)

        and:
        workflow.completeActivity(PRESTO_CROSSCHECK_RULES, fintech.spain.alfa.product.workflow.common.Resolutions.CANCEL)

        then:
        workflow.getActivity(PRESTO_CROSSCHECK_RULES).resolution == fintech.spain.alfa.product.workflow.common.Resolutions.CANCEL
        workflow.isTerminated()
        workflow.toApplication().application.status == LoanApplicationStatus.CLOSED
        workflow.toApplication().application.statusDetail == LoanApplicationStatusDetail.CANCELLED
    }

    def "Affiliates - Voiding active loan terminates related active workflow"() {
        given:
        def client = fintech.spain.alfa.product.testing.TestFactory.newAffiliateClient().signUp()

        when: "Client has issued active loan"
        def workflow = client.toLoanAffiliateWorkflow()
        def loan = workflow.runAll().toLoan()

        then: "Loans And workflow are active"
        workflow.isActive()
        loan.status == LoanStatus.OPEN

        when: "Loan is voided"
        loan.voidLoan()

        then: "Workflow is canceled"
        workflow.isTerminated()
        workflow.workflow.terminateReason == "LoanVoided"
    }

    def "Affiliates - DNI_DOC_UPLOAD activity expires after 72 hours"() {
        given:
        enableDocumentValidation()
        def workflow = fintech.spain.alfa.product.testing.TestFactory.newAffiliateClient().signUp().toLoanAffiliateWorkflow()

        when:
        workflowBackgroundJobs.run(TimeMachine.now())

        then:
        workflow.getActivity(DNI_DOC_UPLOAD).getStatus() == ActivityStatus.WAITING

        when:
        workflow.runBeforeActivity(DNI_DOC_UPLOAD)

        and:
        workflowBackgroundJobs.run(TimeMachine.now())

        then:
        workflow.isActivityActive(DNI_DOC_UPLOAD)

        when:
        workflowBackgroundJobs.run(TimeMachine.now().plusHours(73))

        then:
        workflow.getActivity(DNI_DOC_UPLOAD).getStatus() == ActivityStatus.COMPLETED
        workflow.getActivity(DNI_DOC_UPLOAD).getResolution() == fintech.spain.alfa.product.workflow.common.Resolutions.EXPIRE
    }

    def "Affiliates - DNI_DOC_UPLOAD activity is skipped when setting is disabled"() {
        given:
        def documentSettings = settingsService.getJson(AlfaSettings.ID_DOCUMENT_VALIDITY_SETTINGS, AlfaSettings.IdDocumentValiditySettings.class)
        documentSettings.setRequestIdUploadForFirstLoan(false)
        documentSettings.setRequestIdUploadForSecondAndLaterLoan(false)
        settingsService.update(new UpdatePropertyCommand(name: AlfaSettings.ID_DOCUMENT_VALIDITY_SETTINGS, textValue: JsonUtils.writeValueAsString(documentSettings)))

        def workflow = fintech.spain.alfa.product.testing.TestFactory.newAffiliateClient().signUp().toLoanAffiliateWorkflow()

        when:
        workflow.runAfterActivity(DNI_DOC_UPLOAD)

        then:
        workflow.getActivity(DNI_DOC_UPLOAD).getStatus() == ActivityStatus.COMPLETED
        workflow.getActivity(DNI_DOC_UPLOAD).getResolution() == fintech.spain.alfa.product.workflow.common.Resolutions.SKIP
    }

    def "Affiliates - DNI_DOC_UPLOAD activity is completed when agent uploads attachment with type ID Document"() {
        given:
        enableDocumentValidation()
        def client = fintech.spain.alfa.product.testing.TestFactory.newAffiliateClient().signUp()

        def workflow = client.toLoanAffiliateWorkflow()

        when:
        workflow.runBeforeActivity(DNI_DOC_UPLOAD)

        then:
        workflow.getActivity(DNI_DOC_UPLOAD).getStatus() == ActivityStatus.ACTIVE

        when:
        client.saveAttachment()

        then:
        workflow.getActivity(DNI_DOC_UPLOAD).getStatus() == ActivityStatus.COMPLETED
        workflow.getActivity(DNI_DOC_UPLOAD).getResolutionDetail() == "UploadedByAgent"
    }

    def "Affiliates - DNI_DOC_UPLOAD activity is completed when client uploads attachment"() {
        given:
        enableDocumentValidation()
        def client = fintech.spain.alfa.product.testing.TestFactory.newAffiliateClient().signUp()

        def workflow = client.toLoanAffiliateWorkflow()

        when:
        workflow.runBeforeActivity(DNI_DOC_UPLOAD)

        then:
        workflow.getActivity(DNI_DOC_UPLOAD).getStatus() == ActivityStatus.ACTIVE

        when:
        workflow.documentsUploaded()

        then:
        workflow.getActivity(DNI_DOC_UPLOAD).getStatus() == ActivityStatus.COMPLETED
        workflow.getActivity(DNI_DOC_UPLOAD).getResolutionDetail() == "UploadedByClient"
    }

    def "Affiliates - CHECK_VALID_ID_DOC activity completes with Resolution.OK(ID NOT REQUIRED) when setting is disabled"() {
        given:
        def documentSettings = settingsService.getJson(AlfaSettings.ID_DOCUMENT_VALIDITY_SETTINGS, AlfaSettings.IdDocumentValiditySettings.class)
        documentSettings.setRequestIdUploadForFirstLoan(false)
        documentSettings.setRequestIdUploadForSecondAndLaterLoan(false)
        settingsService.update(new UpdatePropertyCommand(name: AlfaSettings.ID_DOCUMENT_VALIDITY_SETTINGS, textValue: JsonUtils.writeValueAsString(documentSettings)))

        def workflow = fintech.spain.alfa.product.testing.TestFactory.newAffiliateClient().signUp().toLoanAffiliateWorkflow()

        assert workflow.getActivity(CHECK_VALID_ID_DOC).getStatus() == ActivityStatus.WAITING

        when:
        workflow.runAfterActivity(CHECK_VALID_ID_DOC)

        then:
        workflow.getActivity(CHECK_VALID_ID_DOC).getStatus() == ActivityStatus.COMPLETED
        workflow.getActivity(CHECK_VALID_ID_DOC).getResolution() == fintech.spain.alfa.product.workflow.common.Resolutions.OK
        workflow.getActivity(CHECK_VALID_ID_DOC).getResolutionDetail() == CheckValidIdDocumentActivity.RESOLUTION_DETAILS_ID_NOT_REQUIRED
    }

    def "Affiliates - CHECK_VALID_ID_DOC activity completes with Resolution.OK(ID REQUIRED) when setting is enabled and IdentificationDocument not exist"() {
        given:
        enableDocumentValidation()
        def workflow = fintech.spain.alfa.product.testing.TestFactory.newAffiliateClient().signUp().toLoanAffiliateWorkflow()

        assert workflow.getActivity(CHECK_VALID_ID_DOC).getStatus() == ActivityStatus.WAITING

        when:
        workflow.runAfterActivity(CHECK_VALID_ID_DOC)

        then:
        workflow.getActivity(CHECK_VALID_ID_DOC).getStatus() == ActivityStatus.COMPLETED
        workflow.getActivity(CHECK_VALID_ID_DOC).getResolution() == fintech.spain.alfa.product.workflow.common.Resolutions.OK
        workflow.getActivity(CHECK_VALID_ID_DOC).getResolutionDetail() == CheckValidIdDocumentActivity.RESOLUTION_DETAILS_ID_REQUIRED
    }

    def "Affiliates - CHECK_VALID_ID_DOC activity completes with Resolution.OK(ID REQUIRED) when setting is enabled and valid IdentificationDocument not exist"() {
        given:
        enableDocumentValidation()
        def workflow = fintech.spain.alfa.product.testing.TestFactory.newAffiliateClient().signUpWithApplication()
            .createIdentificationDocument()
            .invalidateIdentificationDocument()
            .toLoanAffiliateWorkflow()

        assert workflow.getActivity(CHECK_VALID_ID_DOC).getStatus() == ActivityStatus.WAITING

        when:
        workflow.runAfterActivity(CHECK_VALID_ID_DOC)

        then:
        workflow.getActivity(CHECK_VALID_ID_DOC).getStatus() == ActivityStatus.COMPLETED
        workflow.getActivity(CHECK_VALID_ID_DOC).getResolution() == fintech.spain.alfa.product.workflow.common.Resolutions.OK
        workflow.getActivity(CHECK_VALID_ID_DOC).getResolutionDetail() == CheckValidIdDocumentActivity.RESOLUTION_DETAILS_ID_REQUIRED
    }

    def "Affiliates - ID_DOCUMENT_MANUAL_TEXT_EXTRACTION activity completes with Resolution.SKIP when not expired IdentificationDocument exists"() {
        given:
        enableDocumentValidation()
        def workflow = fintech.spain.alfa.product.testing.TestFactory.newAffiliateClient().signUpWithApplication()
            .createIdentificationDocument()
            .invalidateIdentificationDocument()
            .toLoanAffiliateWorkflow()

        when:
        workflow.runAfterActivity(ID_DOCUMENT_MANUAL_TEXT_EXTRACTION)

        then:
        workflow.getActivity(ID_DOCUMENT_MANUAL_TEXT_EXTRACTION).getStatus() == ActivityStatus.COMPLETED
        workflow.getActivity(ID_DOCUMENT_MANUAL_TEXT_EXTRACTION).getResolution() == fintech.spain.alfa.product.workflow.common.Resolutions.SKIP
    }

    def "Affiliates - DNI_DOC_UPLOAD activity completes with Resolution.SKIP when not expired IdentificationDocument exists"() {
        given:
        enableDocumentValidation()
        def workflow = fintech.spain.alfa.product.testing.TestFactory.newAffiliateClient().signUpWithApplication()
            .createIdentificationDocument()
            .invalidateIdentificationDocument()
            .toLoanAffiliateWorkflow()

        when:
        workflow.runAfterActivity(DNI_DOC_UPLOAD)

        then:
        workflow.getActivity(DNI_DOC_UPLOAD).getStatus() == ActivityStatus.COMPLETED
        workflow.getActivity(DNI_DOC_UPLOAD).getResolution() == fintech.spain.alfa.product.workflow.common.Resolutions.SKIP
    }

    def "Affiliates - CHECK_VALID_ID_DOC activity completes with Resolution.OK(ID NOT REQUIRED) when setting is enabled and valid IdentificationDocument exist"() {
        given:
        def client = fintech.spain.alfa.product.testing.TestFactory.newAffiliateClient()
            .signUpWithApplication()
            .createIdentificationDocument()
            .validateIdentificationDocument()

        def workflow = client.toLoanAffiliateWorkflow()

        assert workflow.getActivity(CHECK_VALID_ID_DOC).getStatus() == ActivityStatus.WAITING

        when:
        workflow.runAfterActivity(CHECK_VALID_ID_DOC)

        then:
        workflow.getActivity(CHECK_VALID_ID_DOC).getStatus() == ActivityStatus.COMPLETED
        workflow.getActivity(CHECK_VALID_ID_DOC).getResolution() == fintech.spain.alfa.product.workflow.common.Resolutions.OK
        workflow.getActivity(CHECK_VALID_ID_DOC).getResolutionDetail() == CheckValidIdDocumentActivity.RESOLUTION_DETAILS_ID_NOT_REQUIRED
    }

    def "Affiliates - ID_DOCUMENT_MANUAL_TEXT_EXTRACTION activity expires after 72 hours"() {
        given:
        enableDocumentValidation()
        def workflow = fintech.spain.alfa.product.testing.TestFactory.newAffiliateClient().signUp().toLoanAffiliateWorkflow()

        when:
        workflowBackgroundJobs.run(TimeMachine.now())

        then:
        workflow.getActivity(ID_DOCUMENT_MANUAL_TEXT_EXTRACTION).getStatus() == ActivityStatus.WAITING

        when:
        workflow.runBeforeActivity(ID_DOCUMENT_MANUAL_TEXT_EXTRACTION)

        and:
        workflowBackgroundJobs.run(TimeMachine.now())

        then:
        workflow.isActivityActive(ID_DOCUMENT_MANUAL_TEXT_EXTRACTION)

        when:
        expiredTaskConsumer.consume(TimeMachine.now().plusHours(73))

        then:
        workflow.getActivity(ID_DOCUMENT_MANUAL_TEXT_EXTRACTION).getStatus() == ActivityStatus.COMPLETED
        workflow.getActivity(ID_DOCUMENT_MANUAL_TEXT_EXTRACTION).getResolution() == fintech.spain.alfa.product.workflow.common.Resolutions.EXPIRE

        and:
        with(workflow.taskOfActivity(ID_DOCUMENT_MANUAL_TEXT_EXTRACTION).getTask()) {
            status == TaskStatus.COMPLETED
            resolution == fintech.spain.alfa.product.workflow.undewrtiting.UnderwritingTasks.IdDocumentManualTextExtraction.EXPIRE
        }
    }

    def "Affiliates -ID_DOCUMENT_MANUAL_TEXT_EXTRACTION activity completes with status REJECTED"() {
        given:
        enableDocumentValidation()
        def workflow = fintech.spain.alfa.product.testing.TestFactory.newAffiliateClient().signUp().toLoanAffiliateWorkflow()

        when:
        workflowBackgroundJobs.run(TimeMachine.now())

        then:
        workflow.getActivity(ID_DOCUMENT_MANUAL_TEXT_EXTRACTION).getStatus() == ActivityStatus.WAITING

        when:
        workflow.runBeforeActivity(ID_DOCUMENT_MANUAL_TEXT_EXTRACTION)

        and:
        workflowBackgroundJobs.run(TimeMachine.now())

        then:
        workflow.isActivityActive(ID_DOCUMENT_MANUAL_TEXT_EXTRACTION)

        when:
        def task = taskService.findTasks(TaskQuery.byWorkflowId(workflow.getWorkflowId())).find { it.taskType == ID_DOCUMENT_MANUAL_TEXT_EXTRACTION }

        then:
        task

        when:
        taskService.completeTask(new CompleteTaskCommand(taskId: task.id, resolution: fintech.spain.alfa.product.workflow.undewrtiting.UnderwritingTasks.IdDocumentManualTextExtraction.INAPPROPRIATE_DOCUMENT))

        then:
        workflow.getActivity(ID_DOCUMENT_MANUAL_TEXT_EXTRACTION).getStatus() == ActivityStatus.COMPLETED
        workflow.getActivity(ID_DOCUMENT_MANUAL_TEXT_EXTRACTION).getResolution() == fintech.spain.alfa.product.workflow.common.Resolutions.REJECT

        and:
        with(workflow.taskOfActivity(ID_DOCUMENT_MANUAL_TEXT_EXTRACTION).getTask()) {
            status == TaskStatus.COMPLETED
            resolution == fintech.spain.alfa.product.workflow.undewrtiting.UnderwritingTasks.IdDocumentManualTextExtraction.INAPPROPRIATE_DOCUMENT
        }
    }

    def "Affiliates -ID_DOCUMENT_MANUAL_TEXT_EXTRACTION activity is skipped when setting is disabled"() {
        given:
        def documentSettings = settingsService.getJson(AlfaSettings.ID_DOCUMENT_VALIDITY_SETTINGS, AlfaSettings.IdDocumentValiditySettings.class)
        documentSettings.setRequestIdUploadForFirstLoan(false)
        documentSettings.setRequestIdUploadForSecondAndLaterLoan(false)
        settingsService.update(new UpdatePropertyCommand(name: AlfaSettings.ID_DOCUMENT_VALIDITY_SETTINGS, textValue: JsonUtils.writeValueAsString(documentSettings)))

        def workflow = fintech.spain.alfa.product.testing.TestFactory.newAffiliateClient().signUp().toLoanAffiliateWorkflow()

        when:
        workflow.runAfterActivity(ID_DOCUMENT_MANUAL_TEXT_EXTRACTION)

        then:
        workflow.getActivity(ID_DOCUMENT_MANUAL_TEXT_EXTRACTION).getStatus() == ActivityStatus.COMPLETED
        workflow.getActivity(ID_DOCUMENT_MANUAL_TEXT_EXTRACTION).getResolution() == fintech.spain.alfa.product.workflow.common.Resolutions.SKIP
    }

    def "Affiliates - DECISION_ENG_ID_VALIDATION when Engine returns 'Valid' - activity completed with OK  and id doc is validated"() {
        given:
        enableDocumentValidation()
        def client = fintech.spain.alfa.product.testing.TestFactory.newAffiliateClient()
            .signUpWithApplication()
            .createIdentificationDocument()

        def workflow = client.toLoanAffiliateWorkflow()

        workflow.runBeforeActivity(DECISION_ENG_ID_VALIDATION)

        and:
        assert workflow.getActivity(DECISION_ENG_ID_VALIDATION).getStatus() == ActivityStatus.ACTIVE
        and:
        assert !client.identificationDocument().valid
        and:
        mockDecisionEngine.setResponseForScenario(ID_VALIDATION_SCENARIO, { ->
            new DecisionResult()
                .setStatus(DecisionRequestStatus.OK)
                .setScore(BigDecimal.TEN)
                .setDecision(RequestIdValidationActivity.ENGINE_DECISION_VALID)
                .setUsedFields(Collections.emptyList())
                .setVariablesResult(new HashMap<>())
        })

        when:
        workflow.runAfterActivity(DECISION_ENG_ID_VALIDATION)

        then:
        assert client.identificationDocument().valid

        and:
        with(workflow.getActivity(DECISION_ENG_ID_VALIDATION)) {
            status == ActivityStatus.COMPLETED
            resolution == fintech.spain.alfa.product.workflow.common.Resolutions.OK
            resolutionDetail == RequestIdValidationActivity.REASON_DETAIL_ID_VALID
        }
    }

    def "Affiliates - Send correct rejection notification for traffic excluded affiliates"() {
        given:
        def settings = settingsService.getJson(AlfaSettings.REFERRAL_LENDING_COMPANY_SETTINGS, fintech.spain.alfa.product.referral.ReferralLendingCompanySettings.class)
        settings.excludeTraffic = ['excluded_traffic_affiliate']
        saveJsonSettings(AlfaSettings.REFERRAL_LENDING_COMPANY_SETTINGS, settings)

        fintech.spain.alfa.product.testing.TestClient affiliateClient = fintech.spain.alfa.product.testing.TestFactory.newClient().randomEmailAndName("First Loan Workflow Affiliates Started");
        affiliateClient.buildSignUpForm().setAffiliate(new fintech.spain.alfa.product.registration.forms.AffiliateData()
            .setAffiliateName("excluded_traffic_affiliate")
            .setAffiliateLeadId("1"));

        when:
        def workflow = affiliateClient.signUp()
            .toLoanAffiliateWorkflow()

        workflow.runBeforeActivity(SCORING_DECISION)
            .completeActivity(SCORING_DECISION, fintech.spain.alfa.product.workflow.common.Resolutions.REJECT)

        then:
        workflow.getActivity(SCORING_DECISION).status == ActivityStatus.COMPLETED
        workflow.getActivityResolution(SCORING_DECISION) == fintech.spain.alfa.product.workflow.common.Resolutions.REJECT

        notificationHelper.countEmails(affiliateClient.clientId, CmsSetup.LOAN_REJECTED_NOTIFICATION_NO_LINK) == 1
    }

    def "Affiliates - DECISION_ENG_ID_VALIDATION when Engine returns 'NotValid' - activity completed with REJECT  and id doc is not validated"() {
        given:
        enableDocumentValidation()
        def client = fintech.spain.alfa.product.testing.TestFactory.newAffiliateClient()
            .signUpWithApplication()
            .createIdentificationDocument()

        def workflow = client.toLoanAffiliateWorkflow()

        workflow.runBeforeActivity(DECISION_ENG_ID_VALIDATION)

        and:
        assert workflow.getActivity(DECISION_ENG_ID_VALIDATION).getStatus() == ActivityStatus.ACTIVE
        and:
        assert !client.identificationDocument().valid
        and:
        mockDecisionEngine.setResponseForScenario(ID_VALIDATION_SCENARIO, { ->
            new DecisionResult()
                .setStatus(DecisionRequestStatus.OK)
                .setScore(BigDecimal.TEN)
                .setDecision(RequestIdValidationActivity.ENGINE_DECISION_NOT_VALID)
                .setUsedFields(Collections.emptyList())
                .setVariablesResult(new HashMap<>())
        })

        when:
        workflow.runAfterActivity(DECISION_ENG_ID_VALIDATION)

        then:
        assert !client.identificationDocument().valid

        and:
        with(workflow.getActivity(DECISION_ENG_ID_VALIDATION)) {
            status == ActivityStatus.COMPLETED
            resolution == fintech.spain.alfa.product.workflow.common.Resolutions.REJECT
            resolutionDetail == RequestIdValidationActivity.REASON_DETAIL_ID_NOT_VALID
            notificationHelper.countEmails(client.clientId, CmsSetup.LOAN_REJECTED_NOTIFICATION) == 1
        }

        // run next activity according to TWINX-1978
        !workflow.isTerminated()
    }

    def "Affiliates - DECISION_ENG_ID_VALIDATION The latest 'ID check' data is not being sent to the Decision engine"() {
        given:
        enableDocumentValidation()
        def client = fintech.spain.alfa.product.testing.TestFactory.newAffiliateClient()
            .signUpWithApplication()
            .createExpiredIdentificationDocument()

        def workflow = client.toLoanAffiliateWorkflow()

        workflow.runBeforeActivity(DECISION_ENG_ID_VALIDATION)

        and:
        assert workflow.getActivity(DECISION_ENG_ID_VALIDATION).getStatus() == ActivityStatus.ACTIVE
        and:
        assert !client.identificationDocument().valid
        and:
        mockDecisionEngine.setResponseForScenario("id_validation",
            { ->
                new DecisionResult()
                    .setStatus(DecisionRequestStatus.OK)
                    .setScore(BigDecimal.TEN)
                    .setDecision(RequestIdValidationActivity.ENGINE_DECISION_NOT_VALID)
                    .setUsedFields(Collections.emptyList())
                    .setVariablesResult(new HashMap<>())
            }
        )

        when:
        workflow.runAfterActivity(DECISION_ENG_ID_VALIDATION)

        then:
        assert !client.identificationDocument().valid

        and:
        with(workflow.getActivity(DECISION_ENG_ID_VALIDATION)) {
            status == ActivityStatus.COMPLETED
            resolution == fintech.spain.alfa.product.workflow.common.Resolutions.REJECT
            resolutionDetail == RequestIdValidationActivity.REASON_DETAIL_ID_NOT_VALID
        }
        workflow.resetDecisionEngine()

        when:
        workflow.print()
        workflow = client
            .retryApplication()
            .toLoanAffiliateWorkflow()
            .runAll()

        client.createIdentificationDocument()
        workflow.runBeforeActivity(DECISION_ENG_ID_VALIDATION)

        then:
        mockDecisionEngine.setResponseForScenario("id_validation",
            { ->
                new DecisionResult()
                    .setStatus(DecisionRequestStatus.OK)
                    .setScore(BigDecimal.TEN)
                    .setDecision(RequestIdValidationActivity.ENGINE_DECISION_VALID)
                    .setUsedFields(Collections.emptyList())
                    .setVariablesResult(new HashMap<>())
            }
        )


        when:
        client.validateIdentificationDocument()
        workflow.runAfterActivity(ID_DOCUMENT_MANUAL_VALIDATION)

        then:
        assert client.identificationDocument().valid
        workflow.getActivity(DECISION_ENG_ID_VALIDATION).getStatus() == ActivityStatus.COMPLETED
        workflow.getActivity(DECISION_ENG_ID_VALIDATION).getResolution() == fintech.spain.alfa.product.workflow.common.Resolutions.OK
    }

    def "Affiliates - The 'CheckValidIdDoc' activity fails and loan app rejects if NIE without expiration date after Retry loan app if document was notValid"() {
        given:
        enableDocumentValidation()
        def client = fintech.spain.alfa.product.testing.TestFactory.newAffiliateClient()
            .signUpWithApplication()
            .createNieWithoutExpirationDate()

        def workflow = client.toLoanAffiliateWorkflow()

        workflow.runBeforeActivity(CHECK_VALID_ID_DOC)

        and:
        assert workflow.getActivity(CHECK_VALID_ID_DOC).getStatus() == ActivityStatus.ACTIVE

        when:
        workflow.runAfterActivity(CHECK_VALID_ID_DOC)

        then:
        assert workflow.getActivity(CHECK_VALID_ID_DOC).getStatus() != ActivityStatus.FAILED
        assert workflow.getActivity(CHECK_VALID_ID_DOC).getResolutionDetail() == CheckValidIdDocumentActivity.RESOLUTION_DETAILS_ID_REQUIRED
    }

    def "Affiliates - DECISION_ENG_ID_VALIDATION when Engine returns 'ManualVerificationRequired' - activity completed with OK  and id doc is not validated"() {
        given:
        enableDocumentValidation()
        def client = fintech.spain.alfa.product.testing.TestFactory.newAffiliateClient()
            .signUpWithApplication()
            .createIdentificationDocument()

        def workflow = client.toLoanAffiliateWorkflow()

        workflow.runBeforeActivity(DECISION_ENG_ID_VALIDATION)

        and:
        assert workflow.getActivity(DECISION_ENG_ID_VALIDATION).getStatus() == ActivityStatus.ACTIVE
        and:
        assert !client.identificationDocument().valid
        and:
        mockDecisionEngine.setResponseForScenario(ID_VALIDATION_SCENARIO, { ->
            new DecisionResult()
                .setStatus(DecisionRequestStatus.OK)
                .setScore(BigDecimal.TEN)
                .setDecision(RequestIdValidationActivity.ENGINE_DECISION_MANUAL)
                .setUsedFields(Collections.emptyList())
                .setVariablesResult(new HashMap<>())
        })

        when:
        workflow.runAfterActivity(DECISION_ENG_ID_VALIDATION)

        then:
        assert !client.identificationDocument().valid

        and:
        with(workflow.getActivity(DECISION_ENG_ID_VALIDATION)) {
            status == ActivityStatus.COMPLETED
            resolution == fintech.spain.alfa.product.workflow.common.Resolutions.OK
            resolutionDetail == RequestIdValidationActivity.REASON_DETAIL_MANUAL_VERIFICATION_REQUIRED
        }
    }

    def "Affiliates - Expired ApproveLoanOffer shall close Application"() {
        given:
        def client = fintech.spain.alfa.product.testing.TestFactory.newAffiliateClient()

        when:
        client
            .signUp()
            .toLoanAffiliateWorkflow()
            .runBeforeActivity(APPROVE_LOAN_OFFER)

        def workflow = client.toLoanAffiliateWorkflow()

        and:
        workflowBackgroundJobs.run(TimeMachine.now().plusSeconds(DateUtils.I_48_HOURS))

        then:
        workflow.isExpired()

        with(loanApplicationService.get(client.getApplicationId())) { application ->
            application.getStatus() == LoanApplicationStatus.CLOSED
        }

        when:
        def popups = popupService.getActual(client.clientId)

        then:
        popups.size() == 1
        popups[0].type == fintech.spain.alfa.product.web.model.PopupType.LOAN_RESOLUTION_CANCELLED
    }

    def "Affiliates - Referral lending company popup after cancelled application"() {
        given:
        def client = fintech.spain.alfa.product.testing.TestFactory.newAffiliateClient()

        when:
        def workflow = client
            .signUp()
            .toLoanAffiliateWorkflow()
            .runBeforeActivity(CREDIT_LIMIT)
            .completeActivity(CREDIT_LIMIT, fintech.spain.alfa.product.workflow.common.Resolutions.REJECT)
        def popupId
        txTemplate.execute({
            popupId = loanApplicationRepository.getRequired(workflow.applicationId).getAttributes()["ReferralPopupId"]
        })

        then:
        popupId

        when:
        def popups = popupService.getActual(client.clientId)

        then:
        popups.size() == 1
        popups[0].id == Long.valueOf(popupId)
        popups[0].type == fintech.spain.alfa.product.web.model.PopupType.REFERRAL_LENDING_COMPANY
    }

    def "Affiliates - Enable marketing consent when client has open loan"() {
        given:
        def client = fintech.spain.alfa.product.testing.TestFactory.newAffiliateClient().signUp()
        client.acceptMarketing(false)

        when: "Client has issued active loan"
        def workflow = client.toLoanAffiliateWorkflow()
        def loan = workflow.runAll().toLoan()

        then: "Loans And workflow are active and marketing consent is enabled"
        workflow.isActive()
        loan.status == LoanStatus.OPEN
        client.getClient().acceptMarketing

        when:
        def popups = popupService.getActual(client.clientId)

        then:
        popups.size() == 1
        popups[0].type == fintech.spain.alfa.product.web.model.PopupType.LOAN_RESOLUTION_APPROVED
    }

    def "Affiliates - Scoring Manual verification - activity resolution"() {
        when:
        def client = fintech.spain.alfa.product.testing.TestFactory.newAffiliateClient().randomEmailAndName("test").signUp()
        def workflow = client.toLoanAffiliateWorkflow()
        workflow.runBeforeActivity(SCORING_DECISION)

        then:
        workflow.getActivity(SCORING_DECISION).status == ActivityStatus.ACTIVE

        when:
        workflow.completeActivity(SCORING_DECISION, fintech.spain.alfa.product.workflow.common.Resolutions.MANUAL)

        then:
        workflow.getActivity(SCORING_DECISION).status == ActivityStatus.COMPLETED
        workflow.getActivityResolution(SCORING_DECISION) == fintech.spain.alfa.product.workflow.common.Resolutions.MANUAL
        workflow.getActivity(SCORING_MANUAL_VERIFICATION).status == ActivityStatus.ACTIVE
        workflow.getActivity(CREDIT_LIMIT).status == ActivityStatus.WAITING

        when:
        def tasks = taskService.findTasks(TaskQuery.byActivityId(workflow.getActivity(SCORING_MANUAL_VERIFICATION).id))

        then:
        tasks.size() == 1
        with(tasks[0]) {
            taskType == fintech.spain.alfa.product.workflow.undewrtiting.UnderwritingTasks.ScoringManualVerificationTask.TYPE
            status == TaskStatus.OPEN
        }

        when:
        workflow.taskOfActivity(SCORING_MANUAL_VERIFICATION).complete(taskResolution)

        then:
        workflow.workflow.status == workflowStatus
        workflow.getActivity(SCORING_MANUAL_VERIFICATION).status == ActivityStatus.COMPLETED
        workflow.getActivity(SCORING_MANUAL_VERIFICATION).resolution == activityResolution

        where:
        taskResolution                   | activityResolution  | workflowStatus
        fintech.spain.alfa.product.workflow.undewrtiting.UnderwritingTasks.InstantorManualCheckTask.EXPIRE  | fintech.spain.alfa.product.workflow.common.Resolutions.EXPIRE  | WorkflowStatus.EXPIRED
        fintech.spain.alfa.product.workflow.undewrtiting.UnderwritingTasks.InstantorManualCheckTask.REJECT  | fintech.spain.alfa.product.workflow.common.Resolutions.REJECT  | WorkflowStatus.TERMINATED
        fintech.spain.alfa.product.workflow.undewrtiting.UnderwritingTasks.InstantorManualCheckTask.APPROVE | fintech.spain.alfa.product.workflow.common.Resolutions.APPROVE | WorkflowStatus.ACTIVE
    }

    def "Affiliates - Repeated application - auto complete Scoring Manual Verification for repeated client"() {
        when:
        def client = fintech.spain.alfa.product.testing.TestFactory.newAffiliateClient()
            .signUp()
            .toLoanAffiliateWorkflow()
            .runAll()
            .exportDisbursement()
            .toLoan()
            .repayAll(TimeMachine.today())
            .toClient()

        and:
        def workflow = client.submitApplicationAndStartAffiliateWorkflow(300.00, 30, TimeMachine.today())
            .toLoanAffiliateWorkflow()
            .runAll()

        then:
        workflow.getActivityResolution(SCORING_MANUAL_VERIFICATION) == fintech.spain.alfa.product.workflow.common.Resolutions.SKIP
        workflow.getActivityResolutionDetail(SCORING_MANUAL_VERIFICATION) == "AutoCompleted"
    }

    def "Affiliates - Instantor manual check - reactivates activities and task for REQUEST_RETRY resolution"() {
        given:
        def client = fintech.spain.alfa.product.testing.TestFactory.newAffiliateClient()
            .signUpWithApplication()

        def workflow = client.toLoanAffiliateWorkflow()

        when:
        workflow.runBeforeActivity(INSTANTOR_MANUAL_CHECK)

        then:
        def instantorManualTask = taskService.findTasks(TaskQuery.byActivityId(workflow.getActivity(INSTANTOR_MANUAL_CHECK).id))[0]

        when:
        taskService.completeTask(new CompleteTaskCommand(taskId: instantorManualTask.id, resolution: fintech.spain.alfa.product.workflow.undewrtiting.UnderwritingTasks.InstantorManualCheckTask.REQUEST_RETRY))

        then: "Instantor client form reactivates"
        workflow.getActivity(INSTANTOR_MANUAL_CHECK).status == ActivityStatus.COMPLETED
        workflow.getActivity(DOCUMENT_FORM).active

        when:
        workflow.runBeforeActivity(INSTANTOR_MANUAL_CHECK)

        then:
        taskService.get(instantorManualTask.id).isOpen()

        when:
        taskService.completeTask(new CompleteTaskCommand(taskId: instantorManualTask.id, resolution: fintech.spain.alfa.product.workflow.undewrtiting.UnderwritingTasks.InstantorManualCheckTask.APPROVE))
        then:
        workflow.getActivity(INSTANTOR_MANUAL_CHECK).status == ActivityStatus.COMPLETED
        taskService.get(instantorManualTask.id).isCompleted()
    }

    def "Affiliates - All manual agent tasks generate with parent task"() {
        given:
        enableDocumentValidation()

        when:
        def client = fintech.spain.alfa.product.testing.TestFactory.newAffiliateClient()
            .signUpWithApplication()

        def workflow = client.toLoanAffiliateWorkflow()
        workflow.manualScoringResponse()
            .manualDecisionEngineOnCondition({ ->
                workflow.getWorkflow().activity(DECISION_ENG_ID_VALIDATION).getStatus() == ActivityStatus.ACTIVE
            })
            .runBeforeActivity(INSTANTOR_MANUAL_CHECK)

        then:
        workflow.getWorkflow().activity(INSTANTOR_MANUAL_CHECK).active
        def instantorManualTask = taskService.findTasks(TaskQuery.byActivityId(workflow.getActivity(INSTANTOR_MANUAL_CHECK).id))[0]
        def parentTask = taskService.findTasks(new TaskQuery().setWorkflowId(workflow.workflowId).setType(fintech.spain.alfa.product.workflow.undewrtiting.UnderwritingTasks.AlltManualTasks.TYPE))[0]
        with(instantorManualTask) {
            instantorManualTask.parentTaskId == parentTask.id
        }

        when:
        taskService.completeTask(new CompleteTaskCommand(taskId: instantorManualTask.id, resolution: fintech.spain.alfa.product.workflow.undewrtiting.UnderwritingTasks.InstantorManualCheckTask.APPROVE))
        workflow.runBeforeActivity(ID_DOCUMENT_MANUAL_TEXT_EXTRACTION)

        then:
        workflow.getWorkflow().activity(ID_DOCUMENT_MANUAL_TEXT_EXTRACTION).active
        def textExtractionTask = taskService.findTasks(TaskQuery.byActivityId(workflow.getActivity(ID_DOCUMENT_MANUAL_TEXT_EXTRACTION).id))[0] //dd
        with(textExtractionTask) {
            textExtractionTask.parentTaskId == parentTask.id
        }

        when:
        client.createIdentificationDocument()
        taskService.completeTask(new CompleteTaskCommand(taskId: textExtractionTask.id, resolution: fintech.spain.alfa.product.workflow.undewrtiting.UnderwritingTasks.IdDocumentManualTextExtraction.COMPLETE))

        then:
        workflow.getWorkflow().activity(SCORING_MANUAL_VERIFICATION).active

        def scoringTask = taskService.findTasks(TaskQuery.byActivityId(workflow.getActivity(SCORING_MANUAL_VERIFICATION).id))[0]

        with(scoringTask) {
            scoringTask.parentTaskId == parentTask.id
        }

        when:
        taskService.completeTask(new CompleteTaskCommand(taskId: scoringTask.id, resolution: fintech.spain.alfa.product.workflow.undewrtiting.UnderwritingTasks.ScoringManualVerificationTask.APPROVE))

        then:
        workflow.runSystemActivity(DECISION_ENG_ID_VALIDATION)
        workflow.getWorkflow().activity(ID_DOCUMENT_MANUAL_VALIDATION).active

        then:
        def validationTask = taskService.findTasks(TaskQuery.byActivityId(workflow.getActivity(ID_DOCUMENT_MANUAL_VALIDATION).id))[0]

        with(validationTask) {
            validationTask.parentTaskId == parentTask.id
        }

        when:
        client.createIdentificationDocument()
        taskService.completeTask(new CompleteTaskCommand(taskId: validationTask.id, resolution: fintech.spain.alfa.product.workflow.undewrtiting.UnderwritingTasks.IdDocumentManualValidation.COMPLETE))
        workflow.runBeforeActivity(DOWJONES_MANUAL_CHECK)

        then:
        workflow.getWorkflow().activity(DOWJONES_MANUAL_CHECK).active
        def dowJonesTask = taskService.findTasks(TaskQuery.byActivityId(workflow.getActivity(DOWJONES_MANUAL_CHECK).id))[0]
        with(dowJonesTask) {
            dowJonesTask.parentTaskId == parentTask.id
        }

        when:
        taskService.completeTask(new CompleteTaskCommand(taskId: dowJonesTask.id, resolution: fintech.spain.alfa.product.workflow.undewrtiting.UnderwritingTasks.DowJonesCheckTask.APPROVE))

        then:
        workflow.getWorkflow().activity(DOWJONES_MANUAL_CHECK).status == ActivityStatus.COMPLETED

        when:
        workflow.runSystemActivity(PREPARE_OFFER)

        then:
        workflow.getWorkflow().activity(PREPARE_OFFER).status == ActivityStatus.COMPLETED
        with(taskService.get(parentTask.id)) {
            it.status == TaskStatus.COMPLETED
        }

        cleanup:
        workflow.resetDecisionEngine()
        workflow.resetManualScoring()
    }

    @Unroll
    def "Affiliates - Manual agent task #taskType - 'postpone' resolution postpones parent task"() {
        given:
        enableDocumentValidation()

        when:
        def client = fintech.spain.alfa.product.testing.TestFactory.newAffiliateClient()
            .signUpWithApplication()

        def workflow = client.toLoanAffiliateWorkflow()
        workflow
            .manualScoringResponse()
            .manualDecisionEngineOnCondition({ ->
                workflow.getWorkflow().activity(DECISION_ENG_ID_VALIDATION).getStatus() == ActivityStatus.ACTIVE
            })
            .runBeforeActivity(activity)

        then:
        workflow.getWorkflow().activity(activity).active
        def task = taskService.findTasks(TaskQuery.byActivityId(workflow.getActivity(activity).id))[0]
        def parentTask = taskService.findTasks(new TaskQuery().setWorkflowId(workflow.workflowId).setType(fintech.spain.alfa.product.workflow.undewrtiting.UnderwritingTasks.AlltManualTasks.TYPE))[0]
        with(task) {
            task.parentTaskId == parentTask.id
            task.taskType == taskType
        }

        when:
        def postponeTo = TimeMachine.now().plusDays(1)
        taskService.postponeTask(new PostponeTaskCommand(taskId: task.id, postponeTo: postponeTo, resolution: 'Postpone'))

        then:
        workflow.getWorkflow().activity(activity).active
        with(taskService.get(task.id)) {
            it.dueAt == postponeTo
            it.status == TaskStatus.OPEN
        }
        with(taskService.get(parentTask.id)) {
            it.dueAt == postponeTo
            it.status == TaskStatus.OPEN
        }

        cleanup:
        workflow.resetDecisionEngine()
        workflow.resetManualScoring()

        where:
        activity                           | taskType
        ID_DOCUMENT_MANUAL_TEXT_EXTRACTION | fintech.spain.alfa.product.workflow.undewrtiting.UnderwritingTasks.IdDocumentManualTextExtraction.TYPE
        ID_DOCUMENT_MANUAL_VALIDATION      | fintech.spain.alfa.product.workflow.undewrtiting.UnderwritingTasks.IdDocumentManualValidation.TYPE
        INSTANTOR_MANUAL_CHECK             | fintech.spain.alfa.product.workflow.undewrtiting.UnderwritingTasks.InstantorManualCheckTask.TYPE
        SCORING_MANUAL_VERIFICATION        | fintech.spain.alfa.product.workflow.undewrtiting.UnderwritingTasks.ScoringManualVerificationTask.TYPE
        DOWJONES_MANUAL_CHECK              | fintech.spain.alfa.product.workflow.undewrtiting.UnderwritingTasks.DowJonesCheckTask.TYPE
    }

    @Unroll
    def "Affiliates - Agent task #taskType - Loan application close reason should contain task name on #rejectResolution resolution"() {
        given:
        enableDocumentValidation()

        when:
        def client = fintech.spain.alfa.product.testing.TestFactory.newAffiliateClient()
            .signUpWithApplication()

        def workflow = client.toLoanAffiliateWorkflow()
        workflow
            .manualScoringResponse()
            .manualDecisionEngineOnCondition({ ->
                workflow.getWorkflow().activity(DECISION_ENG_ID_VALIDATION).getStatus() == ActivityStatus.ACTIVE
            })
            .runBeforeActivity(activity)

        then:
        workflow.getWorkflow().activity(activity).active
        def task = taskService.findTasks(TaskQuery.byActivityId(workflow.getActivity(activity).id))[0]

        when:
        taskService.completeTask(new CompleteTaskCommand(taskId: task.id, resolution: rejectResolution))

        then:
        workflow.getWorkflow().activity(activity).status == ActivityStatus.COMPLETED
        workflow.toApplication().rejected
        workflow.toApplication().closeReason == "${activity}: REJECT"

        cleanup:
        workflow.resetDecisionEngine()
        workflow.resetManualScoring()

        where:
        activity                           | taskType                                              | rejectResolution
        ID_DOCUMENT_MANUAL_TEXT_EXTRACTION | fintech.spain.alfa.product.workflow.undewrtiting.UnderwritingTasks.IdDocumentManualTextExtraction.TYPE | fintech.spain.alfa.product.workflow.undewrtiting.UnderwritingTasks.IdDocumentManualTextExtraction.REJECT_DOCUMENT
        ID_DOCUMENT_MANUAL_VALIDATION      | fintech.spain.alfa.product.workflow.undewrtiting.UnderwritingTasks.IdDocumentManualValidation.TYPE     | fintech.spain.alfa.product.workflow.undewrtiting.UnderwritingTasks.IdDocumentManualValidation.REJECT_DOCUMENT
        INSTANTOR_MANUAL_CHECK             | fintech.spain.alfa.product.workflow.undewrtiting.UnderwritingTasks.InstantorManualCheckTask.TYPE       | fintech.spain.alfa.product.workflow.undewrtiting.UnderwritingTasks.InstantorManualCheckTask.REJECT
        SCORING_MANUAL_VERIFICATION        | fintech.spain.alfa.product.workflow.undewrtiting.UnderwritingTasks.ScoringManualVerificationTask.TYPE  | fintech.spain.alfa.product.workflow.undewrtiting.UnderwritingTasks.ScoringManualVerificationTask.REJECT
        DOWJONES_MANUAL_CHECK              | fintech.spain.alfa.product.workflow.undewrtiting.UnderwritingTasks.DowJonesCheckTask.TYPE              | fintech.spain.alfa.product.workflow.undewrtiting.UnderwritingTasks.DowJonesCheckTask.REJECT
    }

    def "Affiliates - Instantor manual check - complete parent task for REQUEST_RETRY resolution"() {
        given:
        def client = fintech.spain.alfa.product.testing.TestFactory.newAffiliateClient()
            .signUpWithApplication()

        def workflow = client.toLoanAffiliateWorkflow()

        when:
        workflow.runBeforeActivity(INSTANTOR_MANUAL_CHECK)

        then:
        def instantorManualTask = taskService.findTasks(TaskQuery.byActivityId(workflow.getActivity(INSTANTOR_MANUAL_CHECK).id))[0]
        def parentTask = taskService.findTasks(new TaskQuery().setWorkflowId(workflow.workflowId).setType(fintech.spain.alfa.product.workflow.undewrtiting.UnderwritingTasks.AlltManualTasks.TYPE))[0]
        with(instantorManualTask) {
            instantorManualTask.parentTaskId == parentTask.id
        }

        when:
        taskService.completeTask(new CompleteTaskCommand(taskId: instantorManualTask.id, resolution: fintech.spain.alfa.product.workflow.undewrtiting.UnderwritingTasks.InstantorManualCheckTask.REQUEST_RETRY))
        instantorManualTask = taskService.get(instantorManualTask.id)

        then:
        taskService.get(parentTask.id).isCompleted()

        and: "Instantor client form reactivates"
        workflow.getActivity(DOCUMENT_FORM).active

        when:
        workflow.runBeforeActivity(INSTANTOR_MANUAL_CHECK)
        instantorManualTask = taskService.get(instantorManualTask.id)
        parentTask = taskService.get(parentTask.id)

        then:
        with(instantorManualTask) {
            instantorManualTask.parentTaskId == parentTask.id
        }
        instantorManualTask.isOpen()
        parentTask.isOpen()

        when:
        taskService.completeTask(new CompleteTaskCommand(taskId: instantorManualTask.id, resolution: fintech.spain.alfa.product.workflow.undewrtiting.UnderwritingTasks.InstantorManualCheckTask.APPROVE))
        then:
        taskService.get(instantorManualTask.id).isCompleted()
        taskService.get(parentTask.id).isOpen()

        then:
        workflow.getActivity(INSTANTOR_MANUAL_CHECK).status == ActivityStatus.COMPLETED
        taskService.get(instantorManualTask.id).isCompleted()
        taskService.get(parentTask.id).isOpen()
    }

    def "Affiliates - client registration - skip blackbox UI screen"() {
        when:
        def workflow = fintech.spain.alfa.product.testing.TestFactory.newAffiliateClient().signUp().toLoanAffiliateWorkflow()
        workflow.runBeforeActivity(IOVATION_BLACKBOX_RUN_1)

        then:
        with(workflow.getActivity(IOVATION_BLACKBOX_RUN_1)) {
            it.status == ActivityStatus.COMPLETED
            it.resolution == fintech.spain.alfa.product.workflow.common.Resolutions.SKIP
        }
    }

    def "Affiliates - repeated client registration - show blackbox UI screen"() {
        when:
        def client = fintech.spain.alfa.product.testing.TestFactory.newAffiliateClient()
            .signUp()
            .toLoanAffiliateWorkflow()
            .runAll()
            .exportDisbursement()
            .toLoan()
            .repayAll(TimeMachine.today())
            .toClient()

        and:
        def workflow = client.submitApplicationAndStartAffiliateWorkflow(300.00, 30, TimeMachine.today())
            .toLoanAffiliateWorkflow()
            .runBeforeActivity(IOVATION_BLACKBOX_RUN_1)

        then:
        with(workflow.getActivity(IOVATION_BLACKBOX_RUN_1)) {
            it.status == ActivityStatus.ACTIVE
        }

        when:
        iovationService.saveBlackbox(new SaveBlackboxCommand(clientId: client.getClientId(), blackBox: "blackBox", ipAddress: "12.23.44.22"))

        then:
        workflow.getActivityStatus(IOVATION_BLACKBOX_RUN_1) == ActivityStatus.COMPLETED
    }

    def enableDocumentValidation() {
        def documentSettings = settingsService.getJson(AlfaSettings.ID_DOCUMENT_VALIDITY_SETTINGS, AlfaSettings.IdDocumentValiditySettings.class)
        documentSettings.setRequestIdUploadForFirstLoan(true)
        documentSettings.setRequestIdUploadForSecondAndLaterLoan(true)
        settingsService.update(new UpdatePropertyCommand(name: AlfaSettings.ID_DOCUMENT_VALIDITY_SETTINGS, textValue: JsonUtils.writeValueAsString(documentSettings)))
    }
}
