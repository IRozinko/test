package fintech.crm.attachments

import fintech.TimeMachine
import fintech.crm.BaseSpecification
import fintech.crm.attachments.spi.AttachmentBuilder
import fintech.crm.attachments.spi.ClientAttachmentRegistry
import org.springframework.beans.factory.annotation.Autowired
import spock.lang.Unroll

class ClientAttachmentServiceTest extends BaseSpecification {

    @Autowired
    ClientAttachmentService clientAttachmentService

    @Autowired
    ClientAttachmentRegistry clientAttachmentRegistry

    @Unroll
    def "AutoApproveAttachments isAutoApprove: #isAutoApprove term: #term status: #status"() {
        given:
        clientAttachmentRegistry.addDefinition(new AttachmentBuilder("Group", "Attachment")
            .statuses(AttachmentStatus.WAITING_APPROVAL, AttachmentStatus.APPROVED).build())
        def client = createClient()

        when:
        def attachment = clientAttachmentService.addAttachment(
            createAttachment(client, isAutoApprove, term)
        )

        then:
        TimeMachine.useFixedClockAt(clock)
        clientAttachmentService.autoApproveAttachments()
        clientAttachmentService.get(attachment).status == status

        cleanup:
        TimeMachine.useDefaultClock()

        where:
        isAutoApprove || term || status                            || clock
        true          || 1    || AttachmentStatus.WAITING_APPROVAL || TimeMachine.now()
        false         || null || AttachmentStatus.WAITING_APPROVAL || TimeMachine.now().plusDays(3)
        true          || 1    || AttachmentStatus.APPROVED         || TimeMachine.now().plusDays(2)
        true          || 1    || AttachmentStatus.APPROVED         || TimeMachine.now().plusDays(2)
    }

    def createAttachment(Long clientId, Boolean autoApprove, Integer term) {
        def command = new AddAttachmentCommand(
            clientId: clientId,
            fileId: 1L,
            name: "Attachment",
            attachmentType: "Attachment",
            autoApprove: autoApprove,
            status: AttachmentStatus.WAITING_APPROVAL)
        autoApprove ? command.enableAutoApprove(term) : command.disableAutoApprove()
        return command
    }
}
