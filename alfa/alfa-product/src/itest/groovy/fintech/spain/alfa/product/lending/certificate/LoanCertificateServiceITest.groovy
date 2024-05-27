package fintech.spain.alfa.product.lending.certificate

import fintech.TimeMachine
import fintech.crm.attachments.ClientAttachmentService
import fintech.spain.alfa.product.AbstractAlfaTest
import org.springframework.beans.factory.annotation.Autowired

class LoanCertificateServiceITest extends AbstractAlfaTest {

    @Autowired
    LoanCertificateService certificateService

    @Autowired
    ClientAttachmentService clientAttachmentService

    def "generate early repayment certificate"() {
        fintech.spain.alfa.product.testing.TestLoan loan = fintech.spain.alfa.product.testing.TestFactory.newClient()
            .registerDirectly()
            .issueLoan(300.00, 30, TimeMachine.today())

        when:
        def certificate = certificateService.generateCertificate(loan.getLoanId(), LoanCertificateType.EARLY_REPAYMENT)

        then:
        certificate
        !clientAttachmentService.findAttachments(
            new ClientAttachmentService.AttachmentQuery(fileId: certificate.fileId)
        ).isEmpty()
    }

    def "generate certificate of debt"() {
        def issueDate = TimeMachine.today().minusDays(50)
        fintech.spain.alfa.product.testing.TestLoan loan = fintech.spain.alfa.product.testing.TestFactory.newClient()
            .registerDirectly()
            .issueLoan(300.00, 30, issueDate)

        when:
        TimeMachine.useDefaultClock()
        def certificate = certificateService.generateCertificate(loan.getLoan().getId(), LoanCertificateType.DEBT)

        then:
        certificate
        !clientAttachmentService.findAttachments(
            new ClientAttachmentService.AttachmentQuery(fileId: certificate.fileId)
        ).isEmpty()
    }
}
