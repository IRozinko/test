package fintech.spain.alfa.product

import fintech.filestorage.FileStorageService
import fintech.lending.core.loan.LoanService
import fintech.payments.DisbursementService
import fintech.payments.impl.FileBasedDisbursementProcessorBean
import fintech.payments.model.DisbursementStatusDetail
import fintech.payments.model.ExportPendingDisbursementCommand
import fintech.payments.spi.DisbursementProcessorRegistry
import org.springframework.beans.factory.annotation.Autowired

import java.nio.charset.StandardCharsets

import static fintech.DateUtils.date

class SepaExportTest extends AbstractAlfaTest {

    @Autowired
    DisbursementProcessorRegistry disbursementProcessorRegistry

    @Autowired
    DisbursementService disbursementService

    @Autowired
    FileStorageService fileStorageService

    @Autowired
    LoanService loanService

    @Autowired
    FileBasedDisbursementProcessorBean processorBean

    def "export to SEPA format"() {
        when:
        def loan = fintech.spain.alfa.product.testing.TestFactory.newClient().registerDirectly().issueLoan(300.00, 30, date("2018-01-01"))
        def pendingDisbursements = disbursementService.findDisbursements(DisbursementService.DisbursementQuery.byLoan(loan.getLoanId(), DisbursementStatusDetail.PENDING))


        then:
        assert pendingDisbursements.size() == 1

        when:
        def disbursement = pendingDisbursements[0]
        ExportPendingDisbursementCommand exportCommand = new ExportPendingDisbursementCommand(disbursement.getInstitutionId(), disbursement.getInstitutionAccountId())
        def result = processorBean.exportPendingDisbursements(exportCommand)
        disbursement = disbursementService.getDisbursement(disbursement.id)

        then:
        assert result.isFile()
        assert disbursement.statusDetail == DisbursementStatusDetail.EXPORTED
        assert disbursement.exportedFileName != null
        assert disbursement.exportedCloudFileId == result.fileId

        and:
        def content = fileStorageService.readContentAsString(result.fileId, StandardCharsets.UTF_8)
        println content
        assert content.contains("<NbOfTxs>1</NbOfTxs>")
        assert content.contains("<InstdAmt Ccy=\"EUR\">300.00</InstdAmt>")
        assert content.contains("<Ustrd>LOAN ${loan.loan.number}</Ustrd>")
    }
}
