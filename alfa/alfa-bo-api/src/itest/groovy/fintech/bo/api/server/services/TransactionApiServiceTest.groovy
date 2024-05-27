package fintech.bo.api.server.services

import fintech.TimeMachine
import fintech.bo.api.AbstractAlfaBoApiTest
import fintech.bo.api.model.transaction.VoidTransactionRequest
import fintech.dc.DcService
import fintech.spain.alfa.product.testing.TestFactory
import fintech.transactions.TransactionQuery
import fintech.transactions.TransactionService
import fintech.transactions.TransactionType
import org.springframework.beans.factory.annotation.Autowired

class TransactionApiServiceTest extends AbstractAlfaBoApiTest {

    @Autowired
    private TransactionService transactionService
    @Autowired
    private TransactionApiService transactionApiService
    @Autowired
    private DcService dcService

    def "Voiding paid loans runs triggers on void"() {
        when:
        def loan = TestFactory.newClient()
            .signUp()
            .toLoanWorkflow()
            .runAll()
            .exportDisbursement()
            .toLoan()
            .repayAll(TimeMachine.today())

        and:
        dcService.triggerActions(dcService.findByLoanId(loan.loanId).get().id)

        then:
        loan.isPaid()
        dcService.findByLoanId(loan.loanId).get().portfolio == 'Paid'

        when:
        def repaidTransactions = transactionService.findTransactions(TransactionQuery.byLoan(loan.loanId).setTransactionType(TransactionType.REPAYMENT))
        transactionApiService.voidTransaction(new VoidTransactionRequest(transactionId: repaidTransactions[0].id))

        then:
        dcService.findByLoanId(loan.loanId).get().portfolio == 'Current'
    }
}
