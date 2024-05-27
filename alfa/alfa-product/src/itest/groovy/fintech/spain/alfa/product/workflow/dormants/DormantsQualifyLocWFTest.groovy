package fintech.spain.alfa.product.workflow.dormants

import fintech.TimeMachine
import fintech.lending.core.application.LoanApplicationStatus
import fintech.lending.core.application.LoanApplicationStatusDetail
import fintech.spain.alfa.product.AbstractAlfaTest

import java.time.LocalDateTime

class DormantsQualifyLocWFTest extends AbstractAlfaTest {

    def "Full Straightforward , all steps are passed"() {
        given:
        def client = fintech.spain.alfa.product.testing.TestFactory.newClient().signUp().toLoanWorkflow().runAll().toClient()

        when:
        def qualifyLoc = client.submitLineOfCreditAndStartWorkflow(2000.00, TimeMachine.now()).toDormantsQualifyLocWorkflow()
        qualifyLoc.runAll()
        then:
        qualifyLoc.areAllActivitiesCompleted(
            LOC_PRESTO_CROSSCHECK,
            LOC_PRESTO_CROSSCHECK_RULES,
            LOC_HISTORICAL_EXPERIAN_RESUMEN_CHECK,
            LOC_HISTORICAL_EXPERIAN_OPERATIONS_CHECK,
            LOC_EXPERIAN_CAIS_RESUMEN,
            LOC_EXPERIAN_CAIS_OPERATIONS,
            LOC_EXPERIAN_RULES,
            LOC_HISTORICAL_EQUIFAX_CHECK_RULE,
            LOC_EQUIFAX,
            LOC_EQUIFAX_RULES,
            LOC_HISTORICAL_INSTANTOR_CHECK_RULES
        )
        qualifyLoc.isCompleted()
    }

    def "Qualify workflow starts workflow without Instantor, because instantor response exists in 60 days"() {
        given:
        def client = fintech.spain.alfa.product.testing.TestFactory.newClient().signUp().toLoanWorkflow().runAll().toClient()

        when:
        def qualifyLoc = client.submitLineOfCreditAndStartWorkflow(2000.00, TimeMachine.now()).toDormantsQualifyLocWorkflow()
        qualifyLoc.runAll()
        def withoutInstantorWf = client.toWorkflow(LocInstantorWorkflows.DORMANTS_LOC_RECENT_INSTANTOR, fintech.spain.alfa.product.testing.TestDormantsRecentInstantorWorkflow.class)

        then:
        withoutInstantorWf
        withoutInstantorWf.isActive()
        withoutInstantorWf.workflow.parentWorkflowId == qualifyLoc.workflowId
    }

    def "Qualify workflow starts Instantor workflow, because no instantor response in 60 days"() {
        given:
        def client = fintech.spain.alfa.product.testing.TestFactory.newClient().signUp().toLoanWorkflow().runAll().toClient()
        def requestDate = LocalDateTime.now().plusDays(61)

        when:
        // run Qualify Loc with delay of more than 60 days from latest Instantor's response.
        TimeMachine.useFixedClockAt(requestDate)
        def qualifyLoc = client.submitLineOfCreditAndStartWorkflow(2000.00, requestDate).toDormantsQualifyLocWorkflow()
        qualifyLoc.runAll()
        def instantorWf = client.toWorkflow(LocInstantorWorkflows.DORMANTS_LOC_INSTANTOR, fintech.spain.alfa.product.testing.TestDormantsLocInstantorWorkflow.class)
        then:
        instantorWf
        instantorWf.isActive()
        instantorWf.workflow.parentWorkflowId == qualifyLoc.workflowId
    }

    def "Restarting loan application on child workflow-> restarts DormantQualifyLoc"() {
        given:
        def client = fintech.spain.alfa.product.testing.TestFactory.newClient()
            .signUp()
            .toLoanWorkflow()
            .runAll()
            .exportDisbursement(TimeMachine.today())
            .toLoan()
            .repayAll(TimeMachine.today()).toClient()

        when:
        def qualifyLoc = client.submitLineOfCreditAndStartWorkflow(2000.00, TimeMachine.now()).toDormantsQualifyLocWorkflow()
        qualifyLoc.runAll()
        def withoutInstantorWf = client.toWorkflow(LocInstantorWorkflows.DORMANTS_LOC_RECENT_INSTANTOR, fintech.spain.alfa.product.testing.TestDormantsRecentInstantorWorkflow.class)

        then:
        withoutInstantorWf
        withoutInstantorWf.isActive()
        withoutInstantorWf.workflow.parentWorkflowId == qualifyLoc.workflowId

        when:
        client.retryApplication()
        def qualifyLocRestarted = client.toDormantsQualifyLocWorkflow()

        then:
        qualifyLocRestarted
        qualifyLocRestarted.isActive()
        qualifyLocRestarted.workflowId != qualifyLoc.workflowId

    }

    def "Executing Experian call skipped in case if there are any responses in past 80 days"() {
        given:
        def client = fintech.spain.alfa.product.testing.TestFactory.newClient().signUp().toLoanWorkflow().runAll().toClient()

        when:
        def qualifyLoc = client.submitLineOfCreditAndStartWorkflow(2000.00, TimeMachine.now()).toDormantsQualifyLocWorkflow()
        qualifyLoc.runAll()
        then:
        qualifyLoc.getActivityResolution(LOC_EXPERIAN_CAIS_RESUMEN) == fintech.spain.alfa.product.workflow.common.Resolutions.SKIP
        qualifyLoc.getActivityResolution(LOC_EXPERIAN_CAIS_OPERATIONS) == fintech.spain.alfa.product.workflow.common.Resolutions.SKIP
    }

    def "Executing Experian call not skipped in case if there are any responses in past 80 days"() {
        given:
        def client = fintech.spain.alfa.product.testing.TestFactory.newClient().signUp().toLoanWorkflow().runAll().toClient()
        def requestDate = LocalDateTime.now().plusDays(81)

        when:
        TimeMachine.useFixedClockAt(requestDate)
        def qualifyLoc = client.submitLineOfCreditAndStartWorkflow(2000.00, requestDate).toDormantsQualifyLocWorkflow()
        qualifyLoc.runAll()
        then:
        qualifyLoc.getActivityResolution(LOC_EXPERIAN_CAIS_RESUMEN) == fintech.spain.alfa.product.workflow.common.Resolutions.OK
        qualifyLoc.getActivityResolution(LOC_EXPERIAN_CAIS_OPERATIONS) == fintech.spain.alfa.product.workflow.common.Resolutions.OK
    }

    def "Executing Equifax call skipped in case if there are any responses in past 80 days"() {
        given:
        def client = fintech.spain.alfa.product.testing.TestFactory.newClient().signUp().toLoanWorkflow().runAll().toClient()

        when:
        def qualifyLoc = client.submitLineOfCreditAndStartWorkflow(2000.00, TimeMachine.now()).toDormantsQualifyLocWorkflow()
        qualifyLoc.runAll()
        then:
        qualifyLoc.getActivityResolution(LOC_EQUIFAX) == fintech.spain.alfa.product.workflow.common.Resolutions.SKIP
    }

    def "Executing Equifax call not skipped in case if there are any responses in past 80 days"() {
        given:
        def client = fintech.spain.alfa.product.testing.TestFactory.newClient().signUp().toLoanWorkflow().runAll().toClient()
        def requestDate = LocalDateTime.now().plusDays(81)

        when:
        TimeMachine.useFixedClockAt(requestDate)
        def qualifyLoc = client.submitLineOfCreditAndStartWorkflow(2000.00, requestDate).toDormantsQualifyLocWorkflow()
        qualifyLoc.runAll()
        then:
        qualifyLoc.getActivityResolution(LOC_EQUIFAX) == fintech.spain.alfa.product.workflow.common.Resolutions.OK
    }

    def "In case if Equifax call failed the workflow terminates and cancels application"() {
        given:
        def client = fintech.spain.alfa.product.testing.TestFactory.newClient().signUp().toLoanWorkflow().runAll().toClient()
        def requestDate = LocalDateTime.now().plusDays(81)

        when:
        TimeMachine.useFixedClockAt(requestDate)
        def qualifyLoc = client.submitLineOfCreditAndStartWorkflow(2000.00, requestDate).toDormantsQualifyLocWorkflow()
        mockEquifaxProvider.throwError = true
        qualifyLoc.runAll()
        then:
        qualifyLoc.isTerminated()
        qualifyLoc.toApplication().application.status == LoanApplicationStatus.CLOSED
        qualifyLoc.toApplication().application.statusDetail == LoanApplicationStatusDetail.CANCELLED
        qualifyLoc.getActivityResolution(LOC_EQUIFAX) == fintech.spain.alfa.product.workflow.common.Resolutions.FAIL
    }

    def "In case if Experian resumen call failed the workflow terminates and cancels application"() {
        given:
        def client = fintech.spain.alfa.product.testing.TestFactory.newClient().signUp().toLoanWorkflow().runAll().toClient()
        def requestDate = LocalDateTime.now().plusDays(81)

        when:
        TimeMachine.useFixedClockAt(requestDate)
        def qualifyLoc = client.submitLineOfCreditAndStartWorkflow(2000.00, requestDate).toDormantsQualifyLocWorkflow()
        mockExperianCaisProvider.throwError = true
        qualifyLoc.runAll()
        then:
        qualifyLoc.isTerminated()
        qualifyLoc.toApplication().application.status == LoanApplicationStatus.CLOSED
        qualifyLoc.toApplication().application.statusDetail == LoanApplicationStatusDetail.CANCELLED
        qualifyLoc.getActivityResolution(LOC_EXPERIAN_CAIS_RESUMEN) == fintech.spain.alfa.product.workflow.common.Resolutions.FAIL
    }

    def "In case if Experian operation call failed the workflow terminates and cancels application"() {
        given:
        def client = fintech.spain.alfa.product.testing.TestFactory.newClient().signUp().toLoanWorkflow().runAll().toClient()
        def requestDate = LocalDateTime.now().plusDays(81)

        when:
        TimeMachine.useFixedClockAt(requestDate)
        def qualifyLoc = client.submitLineOfCreditAndStartWorkflow(2000.00, requestDate).toDormantsQualifyLocWorkflow()
        qualifyLoc.runBeforeActivity(LOC_EXPERIAN_CAIS_OPERATIONS)
        mockExperianCaisProvider.throwError = true
        qualifyLoc.runAll()
        then:
        qualifyLoc.isTerminated()
        qualifyLoc.toApplication().application.status == LoanApplicationStatus.CLOSED
        qualifyLoc.toApplication().application.statusDetail == LoanApplicationStatusDetail.CANCELLED
        qualifyLoc.getActivityResolution(LOC_EXPERIAN_CAIS_OPERATIONS) == fintech.spain.alfa.product.workflow.common.Resolutions.FAIL
    }
}
