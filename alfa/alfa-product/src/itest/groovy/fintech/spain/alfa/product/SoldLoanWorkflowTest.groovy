package fintech.spain.alfa.product

import fintech.TimeMachine
import fintech.lending.core.loan.LoanStatus
import fintech.lending.core.loan.LoanStatusDetail

class SoldLoanWorkflowTest extends AbstractAlfaTest {

    def "sold issued first loan "() {
        when:
        def workflow = fintech.spain.alfa.product.testing.TestFactory.newClient()
            .signUp()
            .toLoanWorkflow()
            .runAll()

        then:
        workflow.print()
        assert workflow.isActive()
        assert workflow.toLoan().getStatusDetail() == LoanStatusDetail.DISBURSING

        when:
        workflow.exportDisbursement()

        then:
        workflow.print()
        assert workflow.isCompleted()
        assert workflow.toLoan().getStatusDetail() == LoanStatusDetail.ACTIVE

        when:
        workflow.toLoan().sellLoan()

        then:
        assert workflow.toLoan().getStatusDetail() == LoanStatusDetail.SOLD
        assert workflow.toLoan().getStatus() == LoanStatus.CLOSED
        assert workflow.toLoan().getCloseDate() == TimeMachine.today()
    }

}
