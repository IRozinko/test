package fintech.crm

import fintech.crm.attachments.AddAttachmentCommand
import fintech.crm.attachments.Attachment
import fintech.crm.attachments.ClientAttachmentService
import fintech.crm.attachments.spi.AttachmentBuilder
import fintech.crm.attachments.spi.ClientAttachmentRegistry
import org.springframework.beans.factory.annotation.Autowired

import static fintech.crm.attachments.ClientAttachmentService.AttachmentQuery.*

class ClientAttachmentTest extends BaseSpecification {

    @Autowired
    ClientAttachmentRegistry registry

    @Autowired
    ClientAttachmentService service

    Long clientId

    def setup() {
        clientId = createClient()
        registry.addDefinition(new AttachmentBuilder("registration", "idDocument").statuses("valid", "invalid").build())
        registry.addDefinition(new AttachmentBuilder("loan", "loanAgreement").build())
    }

    def "Create & get attachment"() {
        when:
        Long attachmentId = service.addAttachment(new AddAttachmentCommand(clientId: clientId, attachmentType: "idDocument", fileId: 100L, name: "doc.pdf"))
        def attachment = service.get(attachmentId)

        then:
        attachment.fileId == 100L
        attachment.type == "idDocument"
        attachment.status == null
        attachment.statusDetail == null
        attachment.name == "doc.pdf"


        when:
        service.updateStatus(attachmentId, "invalid", "Fake document")
        attachment = service.get(attachmentId)

        then:
        attachment.fileId == 100L
        attachment.type == "idDocument"
        attachment.status == "invalid"
        attachment.statusDetail == "Fake document"
    }



    def "Attachment with unique name"() {
        when:
        service.addAttachment(new AddAttachmentCommand(clientId: clientId, attachmentType: "loanAgreement", fileId: 100L, name: "Loan 1"))

        then:
        findClientAttachments().size() == 1

        when:
        service.addAttachment(new AddAttachmentCommand(clientId: clientId, attachmentType: "loanAgreement", fileId: 101L, name: "Loan 2"))

        then:
        findClientAttachments().size() == 2

        when: "Do not overwrite even if names match"
        service.addAttachment(new AddAttachmentCommand(clientId: clientId, attachmentType: "loanAgreement", fileId: 101L, name: "Loan 2"))

        then:
        findClientAttachments().size() == 3
    }

    def "Find attachments"() {
        given:
        def id = service.addAttachment(new AddAttachmentCommand(clientId: clientId, applicationId: 11L, loanId: 12L, attachmentType: "loanAgreement", fileId: 100L, name: "A"))
        service.addAttachment(new AddAttachmentCommand(clientId: clientId, applicationId: 11L, loanId: 12L, attachmentType: "idDocument", fileId: 100L, name: "B"))

        expect:
        service.findAttachments(byClient(0L)).isEmpty()
        service.findAttachments(byClient(clientId)).size() == 2
        service.findAttachments(byClient(clientId, "loanAgreement"))[0].name == "A"

        and:
        service.findAttachments(byApplication(0L)).isEmpty()
        service.findAttachments(byApplication(11L)).size() == 2
        service.findAttachments(byApplication(11L, "loanAgreement")).size() == 1
        service.findAttachments(byApplication(11L, "loanAgreement"))[0].name == "A"

        and:
        service.findAttachments(byLoan(0L)).isEmpty()
        service.findAttachments(byLoan(12L)).size() == 2
        service.findAttachments(byLoan(12L, "loanAgreement"))[0].name == "A"

        when:
        service.setLoanId(id , 13L)

        then:
        service.findAttachments(byLoan(12L)).size() == 1
        service.findAttachments(byLoan(13L))[0].name == "A"
    }

    private List<Attachment> findClientAttachments() {
        service.findAttachments(byClient(clientId))
    }
}
