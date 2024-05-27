package fintech.spain.alfa.product.payments.processors

import fintech.TimeMachine
import fintech.Validate
import fintech.payments.DisbursementService
import fintech.payments.model.DisbursementStatusDetail
import fintech.spain.alfa.product.AbstractAlfaTest
import org.springframework.beans.factory.annotation.Autowired

class DisbursementProcessorTest extends AbstractAlfaTest {

    public static final String DISBURSEMENTS_FILE_NAME = "ING_20180909164712_QW7.xml";

    @Autowired
    DisbursementService disbursementService

    def "AutoProcessPayment"() {
        given:
        def issueDate = TimeMachine.now().toLocalDate()
        def loan1 = fintech.spain.alfa.product.testing.TestFactory.newClient().registerDirectly().issueLoan(101.0, 10, issueDate)
            .exportDisbursements(issueDate, DISBURSEMENTS_FILE_NAME)

        def loan2 = fintech.spain.alfa.product.testing.TestFactory.newClient().registerDirectly().issueLoan(102.0, 10, issueDate)
            .exportDisbursements(issueDate, DISBURSEMENTS_FILE_NAME)

        when:
        def disbursement1 = disbursementService.getOptional(DisbursementService.DisbursementQuery.byLoan(loan1.loanId, DisbursementStatusDetail.EXPORTED))
        def disbursement2 = disbursementService.getOptional(DisbursementService.DisbursementQuery.byLoan(loan2.loanId, DisbursementStatusDetail.EXPORTED))
        Validate.isTrue(disbursement1.isPresent(), "Disbursement must be existed")
        Validate.isTrue(disbursement2.isPresent(), "Disbursement must be existed")
        def payment = fintech.spain.alfa.product.testing.TestFactory.payments().newOutgoingPayment(203.0, issueDate, "Transferencia emitida\n " + extractDisbursementMsgId(disbursement1.get().exportedFileName))
        fintech.spain.alfa.product.testing.TestFactory.payments().autoProcessPendingPayments()

        then:
        payment.processed
        disbursementService.getDisbursement(disbursement1.get().id).statusDetail == DisbursementStatusDetail.SETTLED
        disbursementService.getDisbursement(disbursement2.get().id).statusDetail == DisbursementStatusDetail.SETTLED
    }

    static def extractDisbursementMsgId(String fileName) {
        Validate.notBlank(fileName, "File name can't be empty")
        return fileName.substring(fileName.indexOf('_') + 1, fileName.lastIndexOf('.'))
    }
}
