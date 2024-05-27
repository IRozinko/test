package fintech.spain.alfa.product

import fintech.TimeMachine
import fintech.lending.core.application.LoanApplicationStatusDetail
import org.springframework.beans.factory.annotation.Autowired

import static fintech.spain.alfa.product.workflow.undewrtiting.UnderwritingWorkflows.Activities.PREPARE_OFFER

class RetryApplicationTest extends AbstractAlfaTest {

    @Autowired
    fintech.spain.alfa.product.lending.UnderwritingFacade underwritingFacade

    def "Retry application"() {
        when: 'when activity fails'
        def workflow = fintech.spain.alfa.product.testing.TestFactory.newClient()
            .signUp()
            .toLoanWorkflow()
            .runBeforeActivity(PREPARE_OFFER)
            .failSystemActivity(PREPARE_OFFER)
        workflow.print()
        def client = workflow.toClient()
        def oldWorkflowId = workflow.workflowId

        then: 'workflow is terminated'
        assert workflow.isTerminated()
        assert client.findAllLoans().isEmpty()

        when: 'third party services throw errors workflow will not fail because during retry the previous attributes are used'
        mockSpainCrosscheckProvider.throwError = true
        mockEquifaxProvider.throwError = true
        mockExperianCaisProvider.throwError = true
        mockIovationProvider.throwError = true
        mockNordigenProvider.throwError = true

        and: 'retry application and create new workflow'
        workflow = client
            .retryApplication()
            .toLoanWorkflow()
            .runAll()
            .exportDisbursement()
        workflow.print()

        then: 'the new workflow is completed and a loan is issued'
        assert workflow.workflowId != oldWorkflowId
        assert workflow.isCompleted()
        assert client.findOpenLoans().size() == 1
    }

    def "Retrying old application in case if client has another open application is not possible"() {
        given: "Client with closed loan application"
        def oldLoanApplication = fintech.spain.alfa.product.testing.TestFactory
            .newClient()
            .signUp()
            .toLoanWorkflow()
            .toApplication()
        def client = oldLoanApplication
            .toClient()

        when:
        client.toLoanWorkflow()
            .runAll()
            .exportDisbursement(TimeMachine.today())
            .toLoan()
            .repayAll(TimeMachine.today())
            .toClient()

        then:
        oldLoanApplication.statusDetail == LoanApplicationStatusDetail.APPROVED

        when: "Client has active another application"
        client.submitApplicationAndStartFirstLoanWorkflow(200.00, 15, TimeMachine.today())

        and: "Try to restart old application"
        underwritingFacade.retryApplication(oldLoanApplication.application.id)

        then:
        IllegalArgumentException ex = thrown()
        ex.message == "Client already has an active loan application"
    }

    def "Retrying old application in case if client has active loans is not possible"() {
        given: "Client with closed loan application"
        def oldLoanApplication = fintech.spain.alfa.product.testing.TestFactory
            .newClient()
            .signUp()
            .toLoanWorkflow()
            .toApplication()
        def client = oldLoanApplication
            .toClient()

        when:
        client.toLoanWorkflow()
            .runAll()
            .exportDisbursement(TimeMachine.today())
            .toLoan()
            .repayAll(TimeMachine.today())
            .toClient()

        then:
        oldLoanApplication.statusDetail == LoanApplicationStatusDetail.APPROVED

        when: "Client has active loan"
        client.issueActiveLoan(300.00, 15, TimeMachine.today())

        and: "Try to restart old application"
        underwritingFacade.retryApplication(oldLoanApplication.application.id)

        then:
        IllegalArgumentException ex = thrown()
        ex.message == "Client already has an active loan"
    }
}
