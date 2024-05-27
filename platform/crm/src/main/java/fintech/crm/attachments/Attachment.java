package fintech.crm.attachments;

import fintech.crm.attachments.spi.AttachmentDefinition;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class Attachment {

    private Long id;
    private String type;
    private AttachmentSubType subType;
    private String name;
    private Long fileId;
    private String status;
    private String statusDetail;
    private AttachmentDefinition definition;
    private LocalDateTime createdAt;

    private Integer autoApproveTerm;
    private boolean autoApprove;

    private Long clientId;
    private Long loanId;
    private Long applicationId;
    private Long transactionId;
}
