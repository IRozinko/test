package fintech.spain.alfa.product

import fintech.lending.core.loan.LoanStatusDetail

import static fintech.DateUtils.date

class RepeatedLoanWorkflowTest extends AbstractAlfaTest {

    def "issue second loan"() {
        when:
        def workflow = fintech.spain.alfa.product.testing.TestFactory.newClient()
            .registerDirectly()
            .issueActiveLoan(300.00, 30, date("2018-01-01"))
            .repayAll(date("2018-02-01"))
            .toClient()
            .submitApplicationAndStartFirstLoanWorkflow(300.00, 30, date("2018-03-01"))
            .toLoanWorkflow()

        then:
        workflow.print()
        assert workflow.isActive()

        when:
        workflow.runAll()
        workflow.print()
        workflow.exportDisbursement(date("2018-03-01"))

        then:
        assert workflow.isCompleted()
        assert workflow.toLoan().getStatusDetail() == LoanStatusDetail.ACTIVE

        and:
        assert workflow.toClient().findPaidLoans().size() == 1
        assert workflow.toClient().findOpenLoans().size() == 1
    }
}
