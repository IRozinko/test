package fintech.crm.attachments;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AddAttachmentCommand {

    private Long clientId;
    private Long loanId;
    private Long applicationId;
    private Long transactionId;

    private String attachmentType;
    private AttachmentSubType attachmentSubType;
    private String name;
    private Long fileId;
    private String status;
    private String statusDetail;

    @Setter(AccessLevel.NONE)
    private boolean autoApprove;

    @Setter(AccessLevel.NONE)
    private Integer autoApproveTerm;

    public void enableAutoApprove(int autoApproveTerm) {
        this.autoApprove = true;
        this.autoApproveTerm = autoApproveTerm;
    }

    public void disableAutoApprove() {
        this.autoApprove = false;
        this.autoApproveTerm = null;
    }

}
